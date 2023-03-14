package org.eclipse.tractusx.ssi.spi.did;

import java.net.URI;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidTest {

  @Test
  @SneakyThrows
  public void createDidTestSuccess() {
    // given
    DidMethod didMethod = new DidMethod("testMethod");
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier("testId");
    // when
    Did testDid = new Did(didMethod, didMethodIdentifier);
    // then
    testDid.toString().equals("did:testMethod:testId");
    testDid.getMethod().equals(didMethod);
    testDid.getMethodIdentifier().equals(didMethodIdentifier);
    testDid.toUri().equals(new URI("did:testMethod:testId"));
  }

  @Test
  @SneakyThrows
  public void createDidTestInvalidMethod() {
    // given
    String expectedMsg = "NullPointer";
    DidMethod didMethod = null;
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier("testId");
    // when
    NullPointerException ex =
        Assertions.assertThrows(
            NullPointerException.class, () -> new Did(didMethod, didMethodIdentifier));
    // then
    ex.toString().equals(expectedMsg);
  }

  @Test
  @SneakyThrows
  public void createDidTestInvalidMethodIdentifier() {
    // given
    String expectedMsg = "NullPointer";
    DidMethod didMethod = new DidMethod("testMethod");
    DidMethodIdentifier didMethodIdentifier = null;
    // when
    NullPointerException ex =
        Assertions.assertThrows(
            NullPointerException.class, () -> new Did(didMethod, didMethodIdentifier));
    // then
    ex.toString().equals(expectedMsg);
  }
}
