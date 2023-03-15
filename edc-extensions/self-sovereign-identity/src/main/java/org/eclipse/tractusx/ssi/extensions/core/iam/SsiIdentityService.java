package org.eclipse.tractusx.ssi.extensions.core.iam;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

@RequiredArgsConstructor
public class SsiIdentityService implements IdentityService {
  private final SsiAgent agent;

  /**
   * This function is called to get the JWT token, that is send to another connector via IDS
   * protocol.
   *
   * @param tokenParameters token parameters
   * @return token
   */
  @Override
  public Result<TokenRepresentation> obtainClientCredentials(TokenParameters tokenParameters) {
    final String audience = tokenParameters.getAudience(); // IDS URL of another connector
    final VerifiableCredential membershipCredential =
        agent.getByType(VerifiableCredentialType.MEMBERSHIP_CREDENTIAL);
    final SignedJWT membershipPresentation =
        agent.createVerifiablePresentationAsJwt(List.of(membershipCredential), audience);
    final TokenRepresentation tokenRepresentation =
        TokenRepresentation.Builder.newInstance()
            .token(membershipPresentation.getParsedString())
            .build();

    return Result.success(tokenRepresentation);
  }

  @Override
  public Result<ClaimToken> verifyJwtToken(
      TokenRepresentation tokenRepresentation, String audience) {

    ClaimToken.Builder claimTokenBuilder = ClaimToken.Builder.newInstance();

    String token = tokenRepresentation.getToken();
    SignedJWT jwt = null;
    try {
      jwt = SignedJWT.parse(token);
    } catch (ParseException e) {
      throw new RuntimeException(e); // TODO
    }

    final VerifiablePresentation verifiablePresentation = agent.check(jwt);

    // TODO Parse Information from Verifiable Credentials and add to ClaimToken (e.g.
    // BusinessPartnerNumber)
    // TODO Check whether credentials issues by dataspace operator
    final VerifiableCredential membershipCredential =
        verifiablePresentation.getVerifiableCredentials().stream()
            .filter(
                c ->
                    c.getTypes().stream()
                        .anyMatch(VerifiableCredentialType.MEMBERSHIP_CREDENTIAL::equalsIgnoreCase))
            .findFirst()
            .orElse(null);
    if (membershipCredential != null) {
      final String businessPartnerNumber =
          (String) membershipCredential.credentialSubject.get("holderIdentifier");
      if (businessPartnerNumber != null) {
        claimTokenBuilder.claim("bpn", businessPartnerNumber);
      }
    }

    return Result.success(claimTokenBuilder.build());
  }
}
