package org.eclipse.tractusx.ssi.extensions.agent.embedded.did.web.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.did.web.exception.DidWebException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;

public class DidWebParser {

  private static String didPath = "/.well-known/did.json";
  private static String optDidPath = "/did.json";

  public static URL parse(Did did) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD)) {
      throw new DidWebException("Did Method not allowed: " + did.getMethod());
    }

    String didUrl = did.getMethodIdentifier().getValue();
    String didUri = "";
    String[] didUrlArray = didUrl.split(":");
    didUrl = String.join("/", didUrlArray);
    didUrl = java.net.URLDecoder.decode(didUrl, StandardCharsets.UTF_8);

    if (didUrlArray.length > 1) {
      didUri = "https://" + didUrl + optDidPath;
    } else {
      didUri = "https://" + didUrl + didPath;
    }

    try {
      return new URL(didUri);
    } catch (MalformedURLException e) {
      throw new DidWebException("Could not build URL from web Did: " + e.getMessage());
    }
  }
}
