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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

public class DataEnveloper {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  public byte[] pack(byte[] data) {
    final Envelop envelop = new Envelop(data);
    return objectMapper.writeValueAsBytes(envelop);
  }

  public Optional<byte[]> tryUnpack(byte[] envelopedData) {
    final Envelop envelop;
    try {
      envelop = objectMapper.readValue(envelopedData, Envelop.class);
    } catch (IOException e) {
      return Optional.empty();
    }
    return Optional.of(envelop.getData());
  }

  @Value
  @AllArgsConstructor
  @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
  private static class Envelop {
    @NonNull
    byte[] data;
  }
}
