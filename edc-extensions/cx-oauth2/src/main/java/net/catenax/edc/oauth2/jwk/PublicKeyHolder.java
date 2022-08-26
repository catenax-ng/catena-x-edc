package net.catenax.edc.oauth2.jwk;

import java.security.PublicKey;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublicKeyHolder {
  private final String keyId;
  private final PublicKey publicKey;
}
