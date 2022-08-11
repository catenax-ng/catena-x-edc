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

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SymmetricKeyProviderTest {

  private static final String KEY_ALIAS = "foo";

  private SymmetricKeyProvider keyProvider;

  // mocks
  private Vault vault;

  @BeforeEach
  void setup() {
    vault = Mockito.mock(Vault.class);
    keyProvider = new SymmetricKeyProvider(vault, KEY_ALIAS);
  }

  @Test
  void testEncryptionKeyAlwaysFirstKey() {
    Mockito.when(vault.resolveSecret(KEY_ALIAS)).thenReturn("1,2,3");

    byte[] key = keyProvider.getEncryptionKey();

    Assertions.assertEquals("1", new String(key));
  }

  @Test
  void testEncryptionThrowsOnNoKey() {
    Mockito.when(vault.resolveSecret(KEY_ALIAS)).thenReturn(" ");

    Assertions.assertThrows(RuntimeException.class, () -> keyProvider.getEncryptionKey());
  }

  @Test
  void testGetKeys() {
    Mockito.when(vault.resolveSecret(KEY_ALIAS)).thenReturn("1,2,  ,,3,4");

    List<String> keys =
        keyProvider.getDecryptionKeySet().map(String::new).collect(Collectors.toList());
    List<String> expected = List.of("1", "2", "3", "4");

    Assertions.assertEquals(expected, keys);
  }
}
