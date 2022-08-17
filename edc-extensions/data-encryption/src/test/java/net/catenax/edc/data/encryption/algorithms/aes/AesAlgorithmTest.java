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
package net.catenax.edc.data.encryption.algorithms.aes;

import lombok.SneakyThrows;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.CryptoDataFactoryImpl;
import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.key.AesKey;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AesAlgorithmTest {

  private static final byte[] KEY_128_BIT = Base64.decode("dVUjmYJzbwVcntkFZU+lNQ==");
  private static final byte[] KEY_196_BIT = Base64.decode("NcgHzzRTUC+z396tWG9hqIbeihujz0m8");
  private static final byte[] KEY_256_BIT =
      Base64.decode("OSD+3NcZAmS/6UXbq6NL8UL+aQIAJDLL7BE2rBX5MtA=");

  private AesAlgorithm strategy = new AesAlgorithm(new CryptoDataFactoryImpl());
  private CryptoDataFactory cryptoDataFactory = new CryptoDataFactoryImpl();

  @Test
  void test128BitKey() {
    testKey(KEY_128_BIT);
  }

  @Test
  void test196BitKey() {
    testKey(KEY_196_BIT);
  }

  @Test
  void test256BitKey() {
    testKey(KEY_256_BIT);
  }

  // TODO Test same data encrypted differently

  @SneakyThrows
  void testKey(byte[] key) {
    final AesKey aesKey = createKey(key);
    final DecryptedData expected = cryptoDataFactory.decryptedFromText("I will be encrypted");
    final EncryptedData encryptedResult = strategy.encrypt(expected, aesKey);
    final DecryptedData result = strategy.decrypt(encryptedResult, aesKey);

    Assertions.assertEquals(expected.getBase64(), result.getBase64());
  }

  AesKey createKey(byte[] key) {
    return new AesKey() {

      @Override
      public byte[] getBytes() {
        return key;
      }

      @Override
      public String getBase64() {
        return Base64.toBase64String(key);
      }
    };
  }
}
