package org.eclipse.tractusx.ssi.extensions.core.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class Base64Test {

    private static final byte[] DECODED = "Multibase is awesome! \\o/".getBytes(StandardCharsets.UTF_8);
    private static final String ENCODED = "mTXVsdGliYXNlIGlzIGF3ZXNvbWUhIFxvLw";

    @Test
    public void testEncoding() {
        var multibase = Base64.create(DECODED);
        Assertions.assertEquals(ENCODED, multibase.getEncoded());
    }

    @Test
    public void testDecoding() {
        var multibase = Base64.create(ENCODED);
        Assertions.assertEquals(new String(DECODED), new String(multibase.getDecoded()));
    }
}
