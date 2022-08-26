package net.catenax.edc.oauth2.jwt.decorator;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.spi.jwt.JwtDecorator;

@RequiredArgsConstructor
public class ExpJwtDecorator implements JwtDecorator {
  @NonNull private final Clock clock;

  @NonNull private final Duration expiration;

  @Override
  public void decorate(final JWSHeader.Builder header, final JWTClaimsSet.Builder claimsSet) {
    claimsSet.expirationTime(Date.from(clock.instant().plusSeconds(expiration.toSeconds())));
  }
}
