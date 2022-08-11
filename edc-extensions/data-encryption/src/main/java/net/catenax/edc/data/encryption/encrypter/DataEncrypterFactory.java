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

package net.catenax.edc.data.encryption.encrypter;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import net.catenax.edc.data.encryption.provider.CachingKeyProvider;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.provider.SymmetricKeyProvider;
import net.catenax.edc.data.encryption.strategies.AesEncryptionStrategy;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import net.catenax.edc.data.encryption.strategies.NoEncryptionStrategy;
import net.catenax.edc.data.encryption.util.DataEnveloper;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

@RequiredArgsConstructor
public class DataEncrypterFactory {

  private final Vault vault;
  private final Monitor monitor;

  public DataEncrypter create(DataEncrypterConfiguration configuration) {
    final KeyProvider keyProvider = getKeyProvider(vault, configuration);
    final EncryptionStrategy strategy = getStrategy(configuration);
    final DataEnveloper enveloper = new DataEnveloper();
    final DataEncrypter dataEncrypter =
        new DataEncrypterImpl(monitor, strategy, enveloper, keyProvider);

    return dataEncrypter;
  }

  private KeyProvider getKeyProvider(Vault vault, DataEncrypterConfiguration configuration) {

    final KeyProvider keyProvider = new SymmetricKeyProvider(vault, configuration.getKeySetAlias());

    return configuration.isCachingEnabled()
        ? new CachingKeyProvider(keyProvider, configuration.getCachingDuration())
        : keyProvider;
  }

  private EncryptionStrategy getStrategy(DataEncrypterConfiguration configuration) {
    if (AesEncryptionStrategy.NAME.equalsIgnoreCase(configuration.getEncryptionStrategy())) {
      return new AesEncryptionStrategy();
    }
    if (NoEncryptionStrategy.NAME.equalsIgnoreCase(configuration.getEncryptionStrategy())) {
      return new AesEncryptionStrategy();
    }

    final String msg =
        String.format(
            DataEncryptionExtension.NAME
                + ": Unsupported encryption strategy. Supported strategies are '%s', %s.",
            AesEncryptionStrategy.NAME,
            NoEncryptionStrategy.NAME);
    throw new NoSuchElementException(msg);
  }
}
