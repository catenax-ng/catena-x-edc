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

  public static Base64 create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException(); // TODO
    }

    final byte[] base64 = Multibase.decode(encoded);

    return new Base64(base64, encoded);
  }

  byte @NonNull [] encoded;
  @NonNull
  String decoded;
}
