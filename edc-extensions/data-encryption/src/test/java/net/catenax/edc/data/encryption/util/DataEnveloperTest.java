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
package net.catenax.edc.data.encryption.util;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataEnveloperTest {

  private final DataEnveloper dataEnveloper = new DataEnveloper();

  @Test
  void testSuccess() {
    final String expected = "I will be enveloped";
    final byte[] packed = dataEnveloper.pack(expected);
    final Optional<String> unpacked = dataEnveloper.tryUnpack(packed);

    Assertions.assertTrue(unpacked.isPresent());
    Assertions.assertEquals(expected, unpacked.get());
  }
}
