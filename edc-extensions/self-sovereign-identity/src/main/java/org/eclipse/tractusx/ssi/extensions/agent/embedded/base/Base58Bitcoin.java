package org.eclipse.tractusx.ssi.extensions.agent.embedded.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.MultibaseString;

@Value
@EqualsAndHashCode
public class Base58Bitcoin implements MultibaseString {

  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base58BTC);
  }

  public static Base58Bitcoin create(byte[] decoded) {

    final String encoded = Multibase.encode(Multibase.Base.Base58BTC, decoded);

    return new Base58Bitcoin(decoded, encoded);
  }

  public static Base58Bitcoin create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException("Encoded base58 String not in Base58BTC format");
    }

    final byte[] decoded = Multibase.decode(encoded);

    return new Base58Bitcoin(decoded, encoded);
  }

  byte @NonNull [] decoded;
  @NonNull String encoded;
}
