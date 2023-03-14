package org.eclipse.tractusx.ssi.extensions.core.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

@Value
@EqualsAndHashCode
public class Base64 implements MultibaseString {

  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base64);
  }

  public static Base64 create(byte[] decoded) {
    final String encoded = Multibase.encode(Multibase.Base.Base64, decoded);
    return new Base64(decoded, encoded);
  }

  public static Base64 create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException(); // TODO
    }

    final byte[] decoded = Multibase.decode(encoded);

    return new Base64(decoded, encoded);
  }

  byte @NonNull [] decoded;
  @NonNull String encoded;
}
