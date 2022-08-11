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
package net.catenax.edc.data.encryption.provider;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import org.eclipse.dataspaceconnector.spi.security.Vault;

@RequiredArgsConstructor
public class SymmetricKeyProvider implements KeyProvider {

  private static final String KEY_SEPARATOR = ",";

  private final Vault vault;
  private final String vaultKeyAlias;

  @Override
  public Stream<byte[]> getDecryptionKeySet() {
    return getKeysStream();
  }

  @Override
  public byte[] getEncryptionKey() {
    return getKeysStream()
        .findFirst()
        .orElseThrow(
            () ->
                new RuntimeException(
                    DataEncryptionExtension.NAME + ": Vault must contain at least one key."));
  }

  private Stream<byte[]> getKeysStream() {
    return Arrays.stream(getKeys().split(KEY_SEPARATOR))
        .map(String::trim)
        .filter(Predicate.not(String::isEmpty))
        .map(String::getBytes);
  }

  private String getKeys() {
    String keys = vault.resolveSecret(vaultKeyAlias);
    return keys == null ? "" : keys;
  }
}
