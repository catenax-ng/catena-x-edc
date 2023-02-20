package org.eclipse.tractusx.ssi.extensions.core.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

@Value
@EqualsAndHashCode
public class Base58Flickr implements MultibaseString {

  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base58Flickr);
  }

  public static Base58Flickr create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException("Encoded base58 String not in Base58Flickr format");
    }

    final byte[] base58 = Multibase.decode(encoded);

    return new Base58Flickr(base58, encoded);
  }
  byte @NonNull [] encoded;

  @NonNull
  String decoded;
}
