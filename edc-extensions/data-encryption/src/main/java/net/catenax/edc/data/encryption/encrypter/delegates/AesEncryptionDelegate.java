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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.dataspaceconnector.spi.EdcException;

import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.algorithms.CryptoAlgorithm;
import net.catenax.edc.data.encryption.data.CryptoDataFactory;
import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.key.AesKey;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.util.DataEnveloper;

@RequiredArgsConstructor
public class AesEncryptionDelegate implements EncryptionDelegate {

    private final KeyProvider<AesKey> keyProvider;
    private final CryptoAlgorithm<AesKey> algorithm;
    private final DataEnveloper dataEnveloper;
    private final CryptoDataFactory cryptoDataFactory;

    @Override
    public String encrypt(String data) {
        byte[] packedData = dataEnveloper.pack(data.getBytes());
        DecryptedData decryptedData = cryptoDataFactory.decryptedFromBytes(packedData);
        AesKey key = keyProvider.getEncryptionKey();

        try {
            EncryptedData encryptedData = algorithm.encrypt(decryptedData, key);
            return encryptedData.getText();
        } catch (IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchPaddingException
                | NoSuchAlgorithmException e) {
            throw new EdcException(e);
        }
    }
}
