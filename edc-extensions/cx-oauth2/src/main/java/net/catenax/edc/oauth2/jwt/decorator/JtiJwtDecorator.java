package net.catenax.edc.oauth2.jwt.decorator;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.UUID;
import lombok.NonNull;
import org.eclipse.dataspaceconnector.spi.jwt.JwtDecorator;

public class JtiJwtDecorator implements JwtDecorator {

  @Override
  public void decorate(
      @NonNull final JWSHeader.Builder header, @NonNull final JWTClaimsSet.Builder claimsSet) {
    claimsSet.jwtID(UUID.randomUUID().toString());
  }
}
