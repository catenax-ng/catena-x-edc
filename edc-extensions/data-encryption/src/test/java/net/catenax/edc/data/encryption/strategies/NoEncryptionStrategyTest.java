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
package net.catenax.edc.data.encryption.strategies;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoEncryptionStrategyTest {

  private NoEncryptionStrategy strategy = new NoEncryptionStrategy();

  @Test
  public void testDecrypt()
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    final byte[] expected = new byte[] {1, 2, 3, 4, 5};
    final byte[] actual = strategy.decrypt(expected, null);

    Assertions.assertArrayEquals(expected, actual);
  }

  @Test
  public void testEncrypt()
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    final byte[] expected = new byte[] {1, 2, 3, 4, 5};
    final byte[] actual = strategy.encrypt(expected, null);

    Assertions.assertArrayEquals(expected, actual);
  }
}
