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
package net.catenax.edc.data.encryption.algorithms;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.catenax.edc.data.encryption.data.DecryptedData;
import net.catenax.edc.data.encryption.data.EncryptedData;
import net.catenax.edc.data.encryption.key.CryptoKey;

public interface CryptoAlgorithm<TKey extends CryptoKey> {
        EncryptedData encrypt(DecryptedData data, TKey key)
                        throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
                        NoSuchPaddingException, NoSuchAlgorithmException;

        DecryptedData decrypt(EncryptedData data, TKey key)
                        throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
                        NoSuchPaddingException, NoSuchAlgorithmException;
}
