package net.catenax.edc.oauth2.jwt.decorator;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.spi.jwt.JwtDecorator;

@RequiredArgsConstructor
public class AudJwtDecorator implements JwtDecorator {
  @NonNull private final String audience;

  @Override
  public void decorate(final JWSHeader.Builder header, final JWTClaimsSet.Builder claimsSet) {
    claimsSet.audience(audience);
  }
}
