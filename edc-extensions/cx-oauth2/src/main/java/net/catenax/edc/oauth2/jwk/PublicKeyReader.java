package net.catenax.edc.oauth2.jwk;

import java.util.Optional;

public interface PublicKeyReader {
  boolean canRead(JsonWebKey jsonWebKey);

  Optional<PublicKeyHolder> read(JsonWebKey jsonWebKey);
}
