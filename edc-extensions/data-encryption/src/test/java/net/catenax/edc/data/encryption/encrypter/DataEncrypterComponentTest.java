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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.provider.SymmetricKeyProvider;
import net.catenax.edc.data.encryption.strategies.AesEncryptionStrategy;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import net.catenax.edc.data.encryption.util.DataEnveloper;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

@SuppressWarnings("FieldCanBeLocal")
class DataEncrypterComponentTest {

  private static final String KEY_128_BIT = "8y/B?E(H+KbPeShV";
  private static final String KEY_192_BIT = "fUjXn2r5u7x!A%D*G-KaPdSg";
  private static final String KEY_256_BIT = "gVkXp2s5v8y/B?E(H+MbQeThWmZq3t6w";

  private DataEncrypter dataEncrypter;
  private EncryptionStrategy encryptionStrategy;
  private DataEnveloper dataEnveloper;
  private KeyProvider keyProvider;

  // mocks
  private Monitor monitor;
  private Vault vault;

  @BeforeEach
  void setup() {
    monitor = Mockito.mock(Monitor.class);
    vault = Mockito.mock(Vault.class);

    encryptionStrategy = new AesEncryptionStrategy();
    dataEnveloper = new DataEnveloper();
    keyProvider = new SymmetricKeyProvider(vault, "foo");

    dataEncrypter = new DataEncrypterImpl(monitor, encryptionStrategy, dataEnveloper, keyProvider);
  }

  @Test
  void testKeyRotation()
      throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {
    Mockito.when(vault.resolveSecret(Mockito.anyString()))
        .thenReturn(
            String.format("%s, %s, %s, %s", KEY_128_BIT, KEY_128_BIT, KEY_128_BIT, KEY_256_BIT));

    final String expectedResult = "hello";
    final byte[] envelopedResult = dataEnveloper.pack(expectedResult);
    final byte[] encryptionKey = KEY_256_BIT.getBytes(StandardCharsets.UTF_8);
    final byte[] encryptedResult = encryptionStrategy.encrypt(envelopedResult, encryptionKey);

    var result = dataEncrypter.decrypt(new String(encryptedResult, StandardCharsets.UTF_8));

    Assertions.assertEquals(expectedResult, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {KEY_128_BIT, KEY_192_BIT, KEY_256_BIT})
  void testEncryption(String key) {
    Mockito.when(vault.resolveSecret(Mockito.anyString())).thenReturn(key);

    final String expectedResult = "hello world!";

    var encryptedResult = dataEncrypter.encrypt(expectedResult);
    var result = dataEncrypter.decrypt(encryptedResult);

    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testWarningOnInvalidKey() {
    // first return only one key for encryption, then for decryption return an
    // invalid key before returning the correct one
    Mockito.when(vault.resolveSecret(Mockito.anyString()))
        .thenReturn(KEY_128_BIT, "invalid-key," + KEY_128_BIT);

    final String expectedResult = "hello world!";

    var encryptedResult = dataEncrypter.encrypt(expectedResult);
    dataEncrypter.decrypt(encryptedResult);

    Mockito.verify(monitor, Mockito.times(1)).warning(Mockito.anyString());
  }
}
