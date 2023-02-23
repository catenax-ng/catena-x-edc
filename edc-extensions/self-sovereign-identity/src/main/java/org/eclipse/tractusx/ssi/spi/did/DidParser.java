package org.eclipse.tractusx.ssi.spi.did;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.extensions.core.exception.DidParseException;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.spi.did.DidMethodIdentifier;

public class DidParser {

  public static Did parse(URI uri) {
    Objects.requireNonNull(uri);

    if (!uri.getScheme().equals("did"))
      throw new DidParseException("URI is not a DID. URI: '" + uri + "'");

    var parts = uri.toString().split(":");
    if (parts.length < 3) {
      throw new DidParseException(
          "DID does not contain at least three parts split by ':'. URI: '" + uri + "'");
    }

    List<String> methodIdentifierParts = Arrays.stream(parts).skip(2).collect(Collectors.toList());

    return new Did(
        new DidMethod(parts[1]), new DidMethodIdentifier(String.join(":", methodIdentifierParts)));
  }

  public static Did parse(String did) {
    Objects.requireNonNull(did);

    final URI uri;
    try {
      uri = URI.create(did);
    } catch (Exception e) {
      throw new DidParseException("Not able to create DID URI from string: " + did, e);
    }

    return parse(uri);
  }
}
