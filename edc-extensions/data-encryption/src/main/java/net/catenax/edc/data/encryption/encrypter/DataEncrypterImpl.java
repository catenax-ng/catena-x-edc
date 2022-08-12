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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.encrypter.delegates.DecryptionDelegate;
import net.catenax.edc.data.encryption.encrypter.delegates.EncryptionDelegate;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

@RequiredArgsConstructor
public class DataEncrypterImpl implements DataEncrypter {

  @NonNull private final EncryptionDelegate encryptionDelegate;
  @NonNull private final DecryptionDelegate decryptionDelegate;

  @Override
  public String encrypt(String value) {
    return encryptionDelegate.encrypt(value);
  }

  @Override
  public String decrypt(String value) {
    return decryptionDelegate.decrypt(value);
  }
}
