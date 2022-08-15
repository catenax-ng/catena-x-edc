/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *
 */
package net.catenax.edc.data.encryption;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import net.catenax.edc.data.encryption.encrypter.DataEncrypterConfiguration;
import net.catenax.edc.data.encryption.encrypter.DataEncrypterFactory;
import net.catenax.edc.data.encryption.key.AesKey;
import net.catenax.edc.data.encryption.key.CryptoKeyFactory;
import net.catenax.edc.data.encryption.key.CryptoKeyFactoryImpl;
import net.catenax.edc.data.encryption.provider.AesKeyProvider;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

@Provides({DataEncrypter.class})
@Requires({Vault.class})
public class DataEncryptionExtension implements ServiceExtension {

  public static final String NAME = "Data Encryption Extension";

  @EdcSetting public static final String ENCRYPTION_KEY_SET = "edc.data.encryption.keys.alias";

  @EdcSetting public static final String ENCRYPTION_ALGORITHM = "edc.data.encryption.algorithm";
  public static final String ENCRYPTION_ALGORITHM_DEFAULT = DataEncrypterFactory.AES_ALGORITHM;

  @EdcSetting public static final String CACHING_ENABLED = "edc.data.encryption.caching.enabled";
  public static final boolean CACHING_ENABLED_DEFAULT = false;

  @EdcSetting public static final String CACHING_SECONDS = "edc.data.encryption.caching.seconds";
  public static final int CACHING_SECONDS_DEFAULT = 3600;

  private Monitor monitor;
  private Vault vault;
  private DataEncrypterConfiguration configuration;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void start() {

    // TODO Add Unit Test here

    final String keyAlias = configuration.getKeySetAlias();
    final String keySecret = vault.resolveSecret(keyAlias);
    if (keySecret == null || keySecret.isEmpty()) {
      throw new EdcException(NAME + ": No vault key secret found for alias " + keyAlias);
    }

    if (configuration.getAlgorithm().equals(DataEncrypterFactory.AES_ALGORITHM)) {
      try {
        final CryptoKeyFactory cryptoKeyFactory = new CryptoKeyFactoryImpl();
        final AesKeyProvider aesKeyProvider =
            new AesKeyProvider(vault, configuration.getKeySetAlias(), cryptoKeyFactory);
        final List<AesKey> keys = aesKeyProvider.getDecryptionKeySet().collect(Collectors.toList());
        monitor.debug(String.format(NAME + ": Found %s registered AES keys in vault", keys.size()));
      } catch (Exception e) {
        throw new EdcException(
            NAME + ": AES keys from vault must be comma separated and Base64 encoded.", e);
      }
    }
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    monitor = context.getMonitor();
    configuration = getConfiguration(context);
    vault = context.getService(Vault.class);

    final CryptoKeyFactory cryptoKeyFactory = new CryptoKeyFactoryImpl();
    final DataEncrypterFactory factory = new DataEncrypterFactory(vault, monitor, cryptoKeyFactory);

    final DataEncrypter dataEncrypter = factory.create(configuration);
    context.registerService(DataEncrypter.class, dataEncrypter);
  }

  private static DataEncrypterConfiguration getConfiguration(ServiceExtensionContext context) {
    final String key = context.getSetting(ENCRYPTION_KEY_SET, null);
    if (key == null) {
      throw new EdcException(NAME + ": Missing setting " + ENCRYPTION_KEY_SET);
    }

    final String encryptionStrategy =
        context.getSetting(ENCRYPTION_ALGORITHM, ENCRYPTION_ALGORITHM_DEFAULT);
    final boolean cachingEnabled = context.getSetting(CACHING_ENABLED, CACHING_ENABLED_DEFAULT);
    final int cachingSeconds = context.getSetting(CACHING_SECONDS, CACHING_SECONDS_DEFAULT);

    return new DataEncrypterConfiguration(
        encryptionStrategy, key, cachingEnabled, Duration.ofSeconds(cachingSeconds));
  }
}
