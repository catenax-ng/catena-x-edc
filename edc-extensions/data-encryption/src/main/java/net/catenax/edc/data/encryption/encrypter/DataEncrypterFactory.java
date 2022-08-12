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

import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import net.catenax.edc.data.encryption.algorithms.CryptoAlgorithm;
import net.catenax.edc.data.encryption.algorithms.aes.AesAlgorithm;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.CryptoDataFactoryImpl;
import net.catenax.edc.data.encryption.encrypter.delegates.AesDecryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.AesEncryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.DecryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.EncryptionDelegate;
import net.catenax.edc.data.encryption.key.AesKey;
import net.catenax.edc.data.encryption.key.CryptoKeyFactory;
import net.catenax.edc.data.encryption.provider.AesKeyProvider;
import net.catenax.edc.data.encryption.provider.CachingKeyProvider;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.util.DataEnveloper;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

@RequiredArgsConstructor
public class DataEncrypterFactory {

  public static final String AES_ALGORITHM = "AES";
  public static final String NONE = "NONE";

  private final Vault vault;
  private final Monitor monitor;
  private final CryptoKeyFactory keyFactory;

  public DataEncrypter create(DataEncrypterConfiguration configuration) {
    if (configuration.getAlgorithm().equalsIgnoreCase(AES_ALGORITHM)) {
      return createAesEncrypter(configuration);
    } else if (configuration.getAlgorithm().equalsIgnoreCase(NONE)) {
      return createNoneEncrypter(configuration);
    } else {
      final String msg =
          String.format(
              DataEncryptionExtension.NAME
                  + ": Unsupported encryption algorithm '%s'. Supported algorithms are '%s',  %s.",
              configuration.getAlgorithm(),
              AES_ALGORITHM,
              NONE);
      throw new IllegalArgumentException(msg);
    }
  }

  public DataEncrypter createAesEncrypter(DataEncrypterConfiguration configuration) {

    KeyProvider<AesKey> keyProvider =
        new AesKeyProvider(vault, configuration.getKeySetAlias(), keyFactory);

    if (configuration.isCachingEnabled()) {
      keyProvider = new CachingKeyProvider<AesKey>(keyProvider, configuration.getCachingDuration());
    }

    final DataEnveloper enveloper = new DataEnveloper();
    final CryptoDataFactory cryptoDataFactory = new CryptoDataFactoryImpl();
    final CryptoAlgorithm<AesKey> algorithm = new AesAlgorithm(cryptoDataFactory);

    final EncryptionDelegate encryptionDelegate =
        new AesEncryptionDelegate(keyProvider, algorithm, enveloper, cryptoDataFactory);
    final DecryptionDelegate decryptionDelegate =
        new AesDecryptionDelegate(keyProvider, algorithm, enveloper, cryptoDataFactory, monitor);

    return new DataEncrypterImpl(encryptionDelegate, decryptionDelegate);
  }

  public DataEncrypter createNoneEncrypter(DataEncrypterConfiguration configuration) {
    return new DataEncrypter() {
      @Override
      public String encrypt(String data) {
        return data;
      }

      @Override
      public String decrypt(String data) {
        return data;
      }
    };
  }
}
