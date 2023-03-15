package org.eclipse.tractusx.ssi.agent.embedded.base;

import java.nio.charset.StandardCharsets;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.base.Base58Bitcoin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Base58BitcoinTest {

  private static final byte[] DECODED =
      "Multibase is awesome! \\o/".getBytes(StandardCharsets.UTF_8);
  private static final String ENCODED = "zYAjKoNbau5KiqmHPmSxYCvn66dA1vLmwbt";

  @Test
  public void testEncoding() {
    var multibase = Base58Bitcoin.create(DECODED);
    Assertions.assertEquals(ENCODED, multibase.getEncoded());
  }

  @Test
  public void testDecoding() {
    var multibase = Base58Bitcoin.create(ENCODED);
    Assertions.assertEquals(new String(DECODED), new String(multibase.getDecoded()));
  }
}
