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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.algorithms.CryptoAlgorithm;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.key.AesKey;

@RequiredArgsConstructor
public class AesAlgorithm implements CryptoAlgorithm<AesKey> {

  private static final String AES_GCM = "AES/GCM/NoPadding";
  private static final String AES = "AES";

  @NonNull
  private final CryptoDataFactory cryptoDataFactory;

  @Override
  public EncryptedData encrypt(DecryptedData data, AesKey key) throws IllegalBlockSizeException, BadPaddingException,
      InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance(AES_GCM, new BouncyCastleProvider());
    final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    byte[] encrypted = cipher.doFinal(data.getBytes());
    return cryptoDataFactory.encryptedFromBytes(encrypted);
  }

  @Override
  public DecryptedData decrypt(EncryptedData data, AesKey key) throws IllegalBlockSizeException, BadPaddingException,
      InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance(AES_GCM, new BouncyCastleProvider());
    final SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES);
    cipher.init(Cipher.DECRYPT_MODE, keySpec);
    byte[] decryptedData = cipher.doFinal(data.getBytes());
    return cryptoDataFactory.decryptedFromBytes(decryptedData);
  }

  private IvParameterSpec createInitializationVectorSpec() throws NoSuchAlgorithmException {
    SecureRandom random = SecureRandom.getInstanceStrong();
    // byte[] paramter = new byte[INITIALIZATION_PARAMETER_SIZE_IN_BYTES];
    // byte[] vector = new byte[INITIALIZATION_PARAMETER_VECTOR_SIZE_IN_BYTES];
    // byte[] counter = new byte[INITIALIZATION_PARAMETER_COUNTER_SIZE_IN_BYTES];
    // random.nextBytes(vector);
    // return new IvParameterSpec(paramter);
    return null;
  }

  private class InitializationVectorFactory {

    private static final int INITIALIZATION_PARAMETER_SIZE_IN_BYTES = 16;
    private static final int INITIALIZATION_PARAMETER_VECTOR_SIZE_IN_BYTES = 12;
    private static final int INITIALIZATION_PARAMETER_COUNTER_SIZE_IN_BYTES = 4;

    private byte[] vector = null;
    private byte[] counter = new byte[INITIALIZATION_PARAMETER_COUNTER_SIZE_IN_BYTES];

    private boolean isCounterMaxed() {
      final byte[] maxBytes = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
      final BigInteger counterCurrent = new BigInteger(1, maxBytes);
      final BigInteger counterEnd = new BigInteger(1, counter);

      return false;
      // return counterCurrent. >= counterEnd;
    }

    private void incrementCounter() {
      counter = new BigInteger(1, counter).add(BigInteger.ONE).toByteArray();
    }

    private void updateVector() throws NoSuchAlgorithmException {
      SecureRandom random = SecureRandom.getInstanceStrong();
      byte[] newVector = new byte[INITIALIZATION_PARAMETER_SIZE_IN_BYTES];
      random.nextBytes(newVector);
      vector = newVector;
    }

  }

}
