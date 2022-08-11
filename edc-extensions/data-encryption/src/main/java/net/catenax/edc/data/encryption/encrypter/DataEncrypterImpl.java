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
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.NonNull;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import net.catenax.edc.data.encryption.util.DataEnveloper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

public class DataEncrypterImpl implements DataEncrypter {

  private final KeyProvider keyProvider;
  private final EncryptionStrategy encryptionStrategy;
  private final DataEnveloper dataEnveloper;
  private final Monitor monitor;

  public DataEncrypterImpl(
      @NonNull Monitor monitor,
      @NonNull EncryptionStrategy encryptionStrategy,
      @NonNull DataEnveloper dataEnveloper,
      @NonNull KeyProvider keyProvider) {
    this.monitor = monitor;
    this.keyProvider = keyProvider;
    this.encryptionStrategy = encryptionStrategy;
    this.dataEnveloper = dataEnveloper;
  }

  @Override
  public String encrypt(String value) {
    try {
      byte[] packedData = dataEnveloper.pack(value);
      byte[] key = keyProvider.getEncryptionKey();
      return new String(encryptionStrategy.encrypt(packedData, key));
    } catch (IllegalBlockSizeException
        | BadPaddingException
        | InvalidKeyException
        | NoSuchPaddingException
        | NoSuchAlgorithmException e) {
      throw new EdcException(e);
    }
  }

  @Override
  public String decrypt(String value) {
    return keyProvider
        .getDecryptionKeySet()
        .map(key -> decrypt(value, key))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(dataEnveloper::tryUnpack)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(
            () ->
                new EdcException(
                    DataEncryptionExtension.NAME
                        + ": Failed to decrypt data. This can happen if the key set is empty, contains invalid keys, the decryption key rotated out of the key set or because the data was originally not encrypted by this extension."));
  }

  private Optional<byte[]> decrypt(String value, byte[] key) {
    try {
      byte[] valueData = value.getBytes(StandardCharsets.UTF_8);
      return Optional.ofNullable(encryptionStrategy.decrypt(valueData, key));
    } catch (IllegalBlockSizeException
        | BadPaddingException
        | InvalidKeyException
        | NoSuchPaddingException
        | NoSuchAlgorithmException e) {
      monitor.warning(
          String.format(
              DataEncryptionExtension.NAME + ": Unusable key in rotating key set. %s",
              e.getMessage()));
      return Optional.empty();
    }
  }
}
