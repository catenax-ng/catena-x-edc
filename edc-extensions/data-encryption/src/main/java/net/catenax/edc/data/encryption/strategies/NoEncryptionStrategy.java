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

public class NoEncryptionStrategy implements EncryptionStrategy {

  public static final String NAME = "NONE";

  @Override
  public byte[] encrypt(byte[] value, byte[] key)
      throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    return value;
  }

  @Override
  public byte[] decrypt(byte[] value, byte[] key)
      throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    return value;
  }
}
