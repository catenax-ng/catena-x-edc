package net.catenax.edc.data.encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
