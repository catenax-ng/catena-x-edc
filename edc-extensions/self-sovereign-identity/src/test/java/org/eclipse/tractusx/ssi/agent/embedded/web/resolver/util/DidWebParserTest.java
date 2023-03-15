package org.eclipse.tractusx.ssi.agent.embedded.web.resolver.util;

import java.net.URL;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.web.exception.DidWebException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.web.util.DidWebParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidWebParserTest {

  // Tests Specification of https://w3c-ccg.github.io/did-method-web/
  // For clearer view -> 2.5 DID method operations
  // w3c-ccg.github.io -> /.well-known/did.json
  // w3c-ccg.github.io:user:alice -> /user/alive/did.json
  // example.com%3A3000 -> :3000/.well-known/did.json
  // example.com%3A3000:user:alice -> :3000/user/alice.did.json**/

  private String didPath = "/.well-known/did.json";

  @Test
  @SneakyThrows
  public void parseDidWebUrlVanillaDid() {
    // given
    String validDidMethod = "web";
    String vanillaDid = "example.com";
    DidMethod didMethod = new DidMethod(validDidMethod);
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(vanillaDid);
    Did toTest = new Did(didMethod, didMethodIdentifier);
    URL expected = new URL("https://" + vanillaDid + didPath);
    // when
    var result = DidWebParser.parse(toTest);
    // then
    Assertions.assertEquals(result, expected);
  }

  @Test
  @SneakyThrows
  public void parseDidWebUrlWithOptPath() {
    // given
    String validDidMethod = "web";
    String optPathDid = "example.com:user:alice";
    DidMethod didMethod = new DidMethod(validDidMethod);
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(optPathDid);
    Did toTest = new Did(didMethod, didMethodIdentifier);
    URL expected = new URL("https://example.com/user/alice/did.json");
    // when
    var result = DidWebParser.parse(toTest);
    // then
    Assertions.assertEquals(expected, result);
  }

  @Test
  @SneakyThrows
  public void parseDidWebUrlWithPort() {
    // given
    String validDidMethod = "web";
    String portDid = "example.com%3A3000";
    DidMethod didMethod = new DidMethod(validDidMethod);
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(portDid);
    Did toTest = new Did(didMethod, didMethodIdentifier);
    URL expected = new URL("https://example.com:3000" + didPath);
    // when
    var result = DidWebParser.parse(toTest);
    // then
    Assertions.assertEquals(expected, result);
  }

  @Test
  @SneakyThrows
  public void parseDidWebUrlWithPercentInKey() {
    // given
    String validDidMethod = "web";
    String portOptPathDid = "example.com%3A3000:user:alice";
    DidMethod didMethod = new DidMethod(validDidMethod);
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(portOptPathDid);
    Did toTest = new Did(didMethod, didMethodIdentifier);
    URL expected = new URL("https://example.com:3000/user/alice/did.json");
    // when
    var result = DidWebParser.parse(toTest);
    // then
    Assertions.assertEquals(expected, result);
  }

  @Test
  @SneakyThrows
  public void parseDidWebUrlInvalidDidMethod() {
    // given
    String inValidDidMethod = "web2";
    String validDidMethodId = "someurl.com";
    DidMethod didMethod = new DidMethod(inValidDidMethod);
    DidMethodIdentifier didMethodIdentifier = new DidMethodIdentifier(validDidMethodId);
    Did toTest = new Did(didMethod, didMethodIdentifier);
    String expected = "Did Method not allowed";
    // when
    var result = Assertions.assertThrows(DidWebException.class, () -> DidWebParser.parse(toTest));
    // then
    Assertions.assertTrue(result.getMessage().contains(expected));
  }
}
