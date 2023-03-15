package org.eclipse.tractusx.ssi.agent.embedded.spi.did;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethodIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidMethodIdentifierTest {

  @Test
  public void createDidMethodIdentifierTestSuccess() {
    // given
    String methodIdentifier = "testIdentifier";
    // when
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(methodIdentifier);
    // then
    didMethodIdentifier.toString().equals(methodIdentifier);
  }

  @Test
  public void createDidMethodIdentifierTestFail() {
    // given
    String expectedMsg = "NullPointer";
    String methodIdentifier = null;
    // when
    NullPointerException ex =
        Assertions.assertThrows(
            NullPointerException.class, () -> new DidMethodIdentifier(methodIdentifier));
    // then
    ex.toString().equals(expectedMsg);
  }

  @Test
  public void createDidMethodIdentifierTestEmptyVal() {
    // given
    String expectedMsg = "Empty value not allowed";
    String methodIdentifier = "";
    // when
    IllegalArgumentException ex =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> new DidMethodIdentifier(methodIdentifier));
    // then
    ex.toString().equals(expectedMsg);
  }
}
