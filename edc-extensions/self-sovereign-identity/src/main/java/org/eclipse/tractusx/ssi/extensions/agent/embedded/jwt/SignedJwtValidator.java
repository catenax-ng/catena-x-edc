package org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.setting.SsiAgentSettings;

public class SignedJwtValidator {

  private SsiAgentSettings settings;
  private String audience;

  public SignedJwtValidator(SsiAgentSettings settings) {
    this.settings = settings;
    this.audience = settings.getDidConnector().toString();
  }

  @SneakyThrows
  public boolean validate(SignedJWT jwt) {
    List<String> audiences = jwt.getJWTClaimsSet().getAudience();
    Date expiryDate = jwt.getJWTClaimsSet().getExpirationTime();
    return isValidAudience(audiences) && isNotExpired(expiryDate);
  }

  private boolean isValidAudience(List<String> audiences) {
    boolean result = audiences.stream().anyMatch(x -> x.equals(audience));
    return result;
  }

  private boolean isNotExpired(Date expiryDate) {
    return expiryDate.after(new Date()); // Todo add Timezone
  }
}
