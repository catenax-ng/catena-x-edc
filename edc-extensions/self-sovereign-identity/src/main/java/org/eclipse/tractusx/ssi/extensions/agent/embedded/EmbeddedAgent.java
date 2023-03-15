package org.eclipse.tractusx.ssi.extensions.agent.embedded;

import com.nimbusds.jwt.SignedJWT;
import foundation.identity.jsonld.JsonLDObject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdValidator;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtValidator;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

@RequiredArgsConstructor
public class EmbeddedAgent implements SsiAgent {

  /**
   * Unique identifier of the Agent. Changing the Identifier of the Agent is a breaking change and
   * must be documented accordingly.
   */
  public static final String Identifier = "EmbeddedAgent";

  private final SerializedJwtPresentationFactory presentationFactory;
  private final JsonLdSerializer jsonLdSerializer;
  private final SignedJwtVerifier jwtVerifier;
  private final SignedJwtValidator jwtValidator;
  private final VerifiableCredentialWallet credentialWallet;
  private final JsonLdValidator jsonLdValidator;
  private final LinkedDataProofValidation linkedDataProofValidation;

  @Override
  public String getIdentifier() {
    return Identifier;
  }

  @Override
  @SneakyThrows // TODO
  public VerifiablePresentation check(SignedJWT jwt) {

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
        throw new RuntimeException("Invalid Json"); // TODO
      }

      if (credential.getProof() == null) {
        throw new RuntimeException("No proof"); // TODO
      }

      var isValid = linkedDataProofValidation.checkProof(credential);
      if (!isValid) {
        throw new RuntimeException("Invalid proof"); // TODO
      }
    }

    return verifiablePresentation;
  }

  @Override
  public VerifiableCredential getByAll() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public VerifiableCredential getByType(String verifiableCredentialType) {
    return credentialWallet.getCredential(verifiableCredentialType);
  }

  @Override
  public SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience) {
    return presentationFactory.createPresentation(credentials, audience);
  }
}
