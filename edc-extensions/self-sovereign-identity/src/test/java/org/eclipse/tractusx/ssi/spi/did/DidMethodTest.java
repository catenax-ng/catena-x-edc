package org.eclipse.tractusx.ssi.spi.did;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidMethodTest {

  @Test
  public void createDidMethodTestSuccess() {
    // given
    String methodName = "testMethod";
    // when
    DidMethod didMethod = new DidMethod("testMethod");
    // then
    didMethod.toString().equals(methodName);
  }

  @Test
  public void createDidMethodTestFail() {
    // given
    String expectedMsg = "NullPointer";
    String methodName = null;
    // when
    NullPointerException ex =
        Assertions.assertThrows(NullPointerException.class, () -> new DidMethod(methodName));
    // then
    ex.toString().equals(expectedMsg);
  }

  @Test
  public void createDidMethodTestEmptyVal() {
    // given
    String expectedMsg = "Empty value not allowed";
    String methodName = "";
    // when
    IllegalArgumentException ex =
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DidMethod(methodName));
    // then
    ex.toString().equals(expectedMsg);
  }
}
