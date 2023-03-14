package org.eclipse.tractusx.ssi.extensions.core.iam;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import foundation.identity.jsonld.JsonLDObject;
import java.text.ParseException;
import java.util.List;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdValidator;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdValidatorImpl;
import org.eclipse.tractusx.ssi.extensions.core.jwt.SignedJwtValidator;
import org.eclipse.tractusx.ssi.extensions.core.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.extensions.core.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.spi.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWallet;

public class SsiIdentityService implements IdentityService {

  private final SerializedJwtPresentationFactory presentationFactory;
  private final JsonLdSerializer jsonLdSerializer;
  private final SignedJwtVerifier jwtVerifier;
  private final SignedJwtValidator jwtValidator;
  private final VerifiableCredentialWallet credentialWallet;
  private final JsonLdValidator jsonLdValidator;
  private final LinkedDataProofValidation linkedDataProofValidation;

  public SsiIdentityService(
      SerializedJwtPresentationFactory serializedJwtPresentationFactory,
      VerifiableCredentialWallet credentialWallet,
      JsonLdSerializer jsonLdSerializer,
      SignedJwtVerifier jwtVerifier,
      SignedJwtValidator jwtValidator,
      LinkedDataProofValidation linkedDataProofValidation,
      JsonLdValidatorImpl jsonLdValidator) {
    this.presentationFactory = serializedJwtPresentationFactory;
    this.credentialWallet = credentialWallet;
    this.jsonLdSerializer = jsonLdSerializer;
    this.jwtVerifier = jwtVerifier;
    this.jwtValidator = jwtValidator;
    this.linkedDataProofValidation = linkedDataProofValidation;
    this.jsonLdValidator = jsonLdValidator;
  }

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
    final VerifiableCredential membershipCredential = credentialWallet.getMembershipCredential();
    final SignedJWT membershipPresentation =
        presentationFactory.createPresentation(List.of(membershipCredential), audience);
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

    try {
      String token = tokenRepresentation.getToken();
      SignedJWT jwt = SignedJWT.parse(token);

      jwtVerifier.verify(jwt);
      jwtValidator.validate(jwt); // TODO is audience and expiry date enough for validation ?

      final String vpClaimValue = jwt.getJWTClaimsSet().getClaim("vp").toString();
      final SerializedVerifiablePresentation vpSerialized =
          new SerializedVerifiablePresentation(vpClaimValue);
      VerifiablePresentation verifiablePresentation =
          jsonLdSerializer.deserializePresentation(vpSerialized);

      // Todo refactor with ObjectMapper

      for (final VerifiableCredential credential :
          verifiablePresentation.getVerifiableCredentials()) {

        JsonLDObject jsonLDObject =
            JsonLDObject.fromJson(credential.toString()); // Todo JsonParser for Credential
        var isValidJson = jsonLdValidator.validate(jsonLDObject);

        if (!isValidJson) {
          return Result.failure(""); // TODO
        }

        if (credential.getProof() == null) {
          return Result.failure(""); // TODO
        }

        var isValid = linkedDataProofValidation.checkProof(credential);
        if (!isValid) {
          return Result.failure(""); // TODO
        }
      }

      // TODO Parse Information from Verifiable Credentials and add to ClaimToken (e.g.
      // BusinessPartnerNumber)

      final VerifiableCredential membershipCredential =
          verifiablePresentation.getVerifiableCredentials().stream()
              .filter(
                  c ->
                      c.getTypes().stream()
                          .anyMatch(
                              VerifiableCredentialType.MEMBERSHIP_CREDENTIAL::equalsIgnoreCase))
              .findFirst()
              .orElse(null);
      if (membershipCredential != null) {
        final String businessPartnerNumber =
            (String) membershipCredential.credentialSubject.get("holderIdentifier");
        if (businessPartnerNumber != null) {
          claimTokenBuilder.claim("bpn", businessPartnerNumber);
        }
      }

    } catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }

    return Result.success(claimTokenBuilder.build());
  }
}
