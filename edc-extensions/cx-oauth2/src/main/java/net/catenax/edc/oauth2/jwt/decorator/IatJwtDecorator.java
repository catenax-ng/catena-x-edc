package net.catenax.edc.oauth2.jwt.decorator;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Clock;
import java.util.Date;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.spi.jwt.JwtDecorator;

@RequiredArgsConstructor
public class IatJwtDecorator implements JwtDecorator {

  @NonNull private final Clock clock;

  @Override
  public void decorate(final JWSHeader.Builder header, final JWTClaimsSet.Builder claimsSet) {
    claimsSet.issueTime(Date.from(clock.instant()));
  }
}
