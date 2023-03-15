package org.eclipse.tractusx.ssi.agent.embedded.base;

import java.nio.charset.StandardCharsets;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.base.Base64WithPadding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Base64WithPaddingTest {

  private static final byte[] DECODED =
      "Multibase is awesome! \\o/".getBytes(StandardCharsets.UTF_8);
  private static final String ENCODED = "MTXVsdGliYXNlIGlzIGF3ZXNvbWUhIFxvLw==";

  @Test
  public void testEncoding() {
    var multibase = Base64WithPadding.create(DECODED);
    Assertions.assertEquals(ENCODED, multibase.getEncoded());
  }

  @Test
  public void testDecoding() {
    var multibase = Base64WithPadding.create(ENCODED);
    Assertions.assertEquals(new String(DECODED), new String(multibase.getDecoded()));
  }
}
