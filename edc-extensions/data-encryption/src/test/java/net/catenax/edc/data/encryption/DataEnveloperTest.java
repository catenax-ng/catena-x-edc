package net.catenax.edc.data.encryption;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataEnveloperTest {

  private final DataEnveloper dataEnveloper = new DataEnveloper();

  @Test
  public void testSuccess() {
    final String expected = "I will be enveloped";
    final byte[] packed = dataEnveloper.pack(expected);
    final Optional<String> unpacked = dataEnveloper.tryUnpack(packed);

    Assertions.assertTrue(unpacked.isPresent());
    Assertions.assertEquals(expected, unpacked.get());
  }
}
