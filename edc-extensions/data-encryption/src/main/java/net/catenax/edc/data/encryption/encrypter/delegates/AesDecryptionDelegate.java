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
package net.catenax.edc.data.encryption.encrypter.delegates;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import net.catenax.edc.data.encryption.algorithms.CryptoAlgorithm;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.key.AesKey;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.util.DataEnveloper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

@RequiredArgsConstructor
public class AesDecryptionDelegate implements DecryptionDelegate {

  private final KeyProvider<AesKey> keyProvider;
  private final CryptoAlgorithm<AesKey> encryptionStrategy;
  private final DataEnveloper dataEnveloper;
  private final CryptoDataFactory cryptoDataFactory;
  private final Monitor monitor;

  @Override
  public String decrypt(String text) {
    EncryptedData encryptedData = cryptoDataFactory.encryptedFromText(text);

    return keyProvider
        .getDecryptionKeySet()
        .map(key -> decrypt(encryptedData, key))
        .map(DecryptedData::getBytes)
        .map(dataEnveloper::tryUnpack)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(String::new)
        .findFirst()
        .orElseThrow(
            () ->
                new EdcException(
                    DataEncryptionExtension.NAME
                        + ": Failed to decrypt data. This can happen if the key set is empty, contains invalid keys, the decryption key rotated out of the key set or because the data was originally not encrypted by this extension."));
  }

  private DecryptedData decrypt(EncryptedData data, AesKey key) {
    try {
      return encryptionStrategy.decrypt(data, key);
    } catch (IllegalBlockSizeException
        | BadPaddingException
        | InvalidKeyException
        | NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException e) {
      // TODO Better message
      monitor.warning(
          String.format(
              DataEncryptionExtension.NAME + ": Unusable key in rotating key set. %s",
              e.getMessage()));
      throw new EdcException(e);
    }
  }
}
