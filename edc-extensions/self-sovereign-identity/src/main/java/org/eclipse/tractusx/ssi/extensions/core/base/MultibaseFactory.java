package org.eclipse.tractusx.ssi.extensions.core.base;

import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

public class MultibaseFactory {

  public static MultibaseString create(byte[] decoded) {
    return Base58Bitcoin.create(decoded);
  }

  public static MultibaseString create(String encoded) {

    if (Base58Bitcoin.canDecode(encoded)) {
      return Base58Bitcoin.create(encoded);
    }
    if (Base58Flickr.canDecode(encoded)) {
      return Base58Flickr.create(encoded);
    }
    if (Base64WithPadding.canDecode(encoded)) {
      return Base64WithPadding.create(encoded);
    }
    if (Base64.canDecode(encoded)) {
      return Base64.create(encoded);
    }

    throw new IllegalArgumentException(
        "Encoded Multibase String is not supported. Must be Base64, Base64_WithPadding, Base58_Bitcoin or Base58_Flickr.");
  }
}
