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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.GCMParameterSpec;

public class AesInitializationVector {

    private static final int VECTOR_SIZE = 12;

    private byte[] currentVector;

    public AesInitializationVector() throws NoSuchAlgorithmException {
        updateVector();
    }

    public GCMParameterSpec createGCMParameterSpec() {

    }

    public void updateVector() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] newVector = new byte[VECTOR_SIZE];
        random.nextBytes(newVector);
        currentVector = newVector;
    }

}
