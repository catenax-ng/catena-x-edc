package org.eclipse.tractusx.ssi.spi.did;

import java.net.URI;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.core.exception.DidParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidParserTest {

  @Test
  @SneakyThrows
  public void parseUriSuccess() {
    // given
    String testDid = "did:web:www.someurl.com";
    URI testUri = new URI(testDid);
    // when
    Did result = DidParser.parse(testUri);
    // then
    Assertions.assertTrue(result.toString().equals(testDid));
    Assertions.assertTrue(result.toUri().equals(testUri));
  }

  @Test
  @SneakyThrows
  public void parseUriInvalidUri() {
    // given
    String expectedMessage = "URI is not a DID. URI:";
    String testDid = "web:did";
    URI testUri = new URI(testDid);
    // when
    DidParseException result =
        Assertions.assertThrows(DidParseException.class, () -> DidParser.parse(testUri));
    // then
    Assertions.assertTrue(result.getMessage().contains(expectedMessage));
  }

  @Test
  @SneakyThrows
  public void parseUriInvalidLength() {
    // given
    String expectedMessage = "DID does not contain at least three parts";
    String testDid = "did:web";
    URI testUri = new URI(testDid);
    // when
    DidParseException result =
        Assertions.assertThrows(DidParseException.class, () -> DidParser.parse(testUri));
    // then
    Assertions.assertTrue(result.getMessage().contains(expectedMessage));
  }

  @Test
  @SneakyThrows
  public void parseStringSuccess() {
    // given
    String testDid = "did:web:www.someurl.com";
    URI testUri = new URI(testDid);
    // when
    Did result = DidParser.parse(testDid);
    // then
    Assertions.assertTrue(result.toString().equals(testDid));
    Assertions.assertTrue(result.toUri().equals(testUri));
  }

  @Test
  @SneakyThrows
  public void parseStringInvalidString() {
    // given
    String expectedMessage = "Not able to create DID URI from string";
    String testDid = "[]";
    // when
    DidParseException result =
        Assertions.assertThrows(DidParseException.class, () -> DidParser.parse(testDid));
    // then
    Assertions.assertTrue(result.getMessage().contains(expectedMessage));
  }
}
