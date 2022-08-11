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
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class AesEncryptionStrategy implements EncryptionStrategy {

  public static final String NAME = "AES";
  private static final String AES = "AES/GCM/NoPadding";

  @Override
  public byte[] encrypt(byte[] value, byte[] key)
      throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance(NAME, new BouncyCastleProvider());
    final SecretKeySpec keySpec = new SecretKeySpec(key, AES);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    byte[] encrypted = cipher.doFinal(value);
    return Base64.encode(encrypted);
  }

  @Override
  public byte[] decrypt(byte[] data, byte[] key)
      throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
          NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance(NAME, new BouncyCastleProvider());
    final SecretKeySpec keySpec = new SecretKeySpec(key, AES);
    cipher.init(Cipher.DECRYPT_MODE, keySpec);
    byte[] decodedBytes = Base64.decode(data);
    return cipher.doFinal(decodedBytes);
  }
}
