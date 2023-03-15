package org.eclipse.tractusx.ssi.extensions.agent.embedded.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.MultibaseString;

@Value
@EqualsAndHashCode
public class Base64WithPadding implements MultibaseString {

  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base64Pad);
  }

  public static Base64WithPadding create(byte[] decoded) {
    final String encoded = Multibase.encode(Multibase.Base.Base64Pad, decoded);
    return new Base64WithPadding(decoded, encoded);
  }

  public static Base64WithPadding create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException(
          "Encoded base64 String not in Base64 format (with padding)");
    }

    final byte[] base64 = Multibase.decode(encoded);

    return new Base64WithPadding(base64, encoded);
  }

  byte @NonNull [] decoded;
  @NonNull String encoded;
}
