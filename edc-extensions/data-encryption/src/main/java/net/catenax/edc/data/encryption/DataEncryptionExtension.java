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
import net.catenax.edc.data.encryption.encrypter.DataEncrypterConfiguration;
import net.catenax.edc.data.encryption.encrypter.DataEncrypterFactory;
import net.catenax.edc.data.encryption.key.CryptoKeyFactory;
import net.catenax.edc.data.encryption.key.CryptoKeyFactoryImpl;
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

  private Vault vault;
  private String keySetAlias;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void start() {

    // TODO Verify all Keys Base64 encoded
    // Check Length of keys for algorithm

    final String keySecret = vault.resolveSecret(keySetAlias);
    if (keySecret == null || keySecret.isEmpty()) {
      throw new EdcException("No key secret found for alias " + keySetAlias);
    }
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    Monitor monitor = context.getMonitor();

    vault = context.getService(Vault.class);

    final DataEncrypterConfiguration configuration = getConfiguration(context);
    keySetAlias = configuration.getKeySetAlias();

    final CryptoKeyFactory cryptoKeyFactory = new CryptoKeyFactoryImpl();
    final DataEncrypterFactory factory = new DataEncrypterFactory(vault, monitor, cryptoKeyFactory);

    final DataEncrypter dataEncrypter = factory.create(configuration);
    context.registerService(DataEncrypter.class, dataEncrypter);
  }

  private DataEncrypterConfiguration getConfiguration(ServiceExtensionContext context) {
    final String keySetAlias = context.getSetting(ENCRYPTION_KEY_SET, null);
    if (keySetAlias == null) {
      throw new EdcException(NAME + ": Missing setting " + ENCRYPTION_KEY_SET);
    }

    final String encryptionStrategy =
        context.getSetting(ENCRYPTION_ALGORITHM, ENCRYPTION_ALGORITHM_DEFAULT);
    final boolean cachingEnabled = context.getSetting(CACHING_ENABLED, CACHING_ENABLED_DEFAULT);
    final int cachingSeconds = context.getSetting(CACHING_SECONDS, CACHING_SECONDS_DEFAULT);

    return new DataEncrypterConfiguration(
        encryptionStrategy, keySetAlias, cachingEnabled, Duration.ofSeconds(cachingSeconds));
  }
}
