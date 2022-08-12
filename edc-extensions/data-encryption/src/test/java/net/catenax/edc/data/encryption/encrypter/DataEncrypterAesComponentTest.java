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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.algorithms.CryptoAlgorithm;
import net.catenax.edc.data.encryption.algorithms.aes.AesAlgorithm;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.CryptoDataFactoryImpl;
import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.encrypter.delegates.AesDecryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.AesEncryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.DecryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.EncryptionDelegate;
import net.catenax.edc.data.encryption.key.AesKey;
import net.catenax.edc.data.encryption.key.CryptoKeyFactory;
import net.catenax.edc.data.encryption.key.CryptoKeyFactoryImpl;
import net.catenax.edc.data.encryption.provider.AesKeyProvider;
import net.catenax.edc.data.encryption.util.DataEnveloper;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import lombok.SneakyThrows;

@SuppressWarnings("FieldCanBeLocal")
class DataEncrypterAesComponentTest {

  private static final String KEY_128_BIT_BASE_64 = "7h6sh6t6tchCmNnHjK2kFA==";
  private static final String KEY_256_BIT_BASE_64 = "OSD+3NcZAmS/6UXbq6NL8UL+aQIAJDLL7BE2rBX5MtA=";

  private DataEncrypter dataEncrypter;
  private CryptoAlgorithm<AesKey> algorithm;
  private DataEnveloper dataEnveloper;
  private KeyProvider<AesKey> keyProvider;
  private CryptoKeyFactory cryptoKeyFactory;
  private CryptoDataFactory cryptoDataFactory;

  // mocks
  private Monitor monitor;
  private Vault vault;

  @BeforeEach
  void setup() {
    monitor = Mockito.mock(Monitor.class);
    vault = Mockito.mock(Vault.class);

    dataEnveloper = new DataEnveloper();
    cryptoKeyFactory = new CryptoKeyFactoryImpl();
    cryptoDataFactory = new CryptoDataFactoryImpl();
    algorithm = new AesAlgorithm(cryptoDataFactory);
    keyProvider = new AesKeyProvider(vault, "foo", cryptoKeyFactory);

    EncryptionDelegate encryptionDelegate = new AesEncryptionDelegate(keyProvider, algorithm, dataEnveloper,
        cryptoDataFactory);
    DecryptionDelegate decryptionDelegate = new AesDecryptionDelegate(keyProvider, algorithm, dataEnveloper,
        cryptoDataFactory, monitor);

    dataEncrypter = new DataEncrypterImpl(encryptionDelegate, decryptionDelegate);
  }

  @Test
  @SneakyThrows
  void testKeyRotation() {
    Mockito.when(vault.resolveSecret(Mockito.anyString()))
        .thenReturn(
            String.format("%s, %s, %s, %s", KEY_128_BIT_BASE_64, KEY_128_BIT_BASE_64, KEY_128_BIT_BASE_64,
                KEY_256_BIT_BASE_64));

    final AesKey key256Bit = cryptoKeyFactory.fromBase64(KEY_256_BIT_BASE_64);
    final byte[] expectedResult = "hello".getBytes();
    final byte[] packedResult = dataEnveloper.pack(expectedResult);
    final DecryptedData decryptedResult = cryptoDataFactory.decryptedFromBytes(packedResult);
    final EncryptedData encryptedResult = algorithm.encrypt(decryptedResult, key256Bit);

    var result = dataEncrypter.decrypt(encryptedResult.getText());

    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testEncryption() {
    Mockito.when(vault.resolveSecret(Mockito.anyString())).thenReturn(KEY_128_BIT_BASE_64);

    final String expectedResult = "hello world!";

    var encryptedResult = dataEncrypter.encrypt(expectedResult);
    var result = dataEncrypter.decrypt(encryptedResult);

    Assertions.assertEquals(expectedResult, result);
  }
}
