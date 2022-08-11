package net.catenax.edc.data.encryption;

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
  public byte[] pack(String data) {
    final Envelop envelop = new Envelop(data);
    return objectMapper.writeValueAsBytes(envelop);
  }

  public Optional<String> tryUnpack(byte[] envelopedData) {
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
    @NonNull String data;
  }
}
