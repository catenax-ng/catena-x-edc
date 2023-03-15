package org.eclipse.tractusx.ssi.extensions.agent.embedded.didcomm;

import com.nimbusds.jwt.SignedJWT;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;

/** Internet facing API for other connector to request verifiable credentials */
@Produces({MediaType.APPLICATION_JSON})
@Path("/ssi")
public class DidCommController {

  private final VerifiableCredentialWallet credentialStore;
  private final SerializedJwtPresentationFactory presentationFactory;

  public DidCommController(
      VerifiableCredentialWallet verifiableCredentialStore,
      SerializedJwtPresentationFactory presentationFactory) {
    this.credentialStore = verifiableCredentialStore;
    this.presentationFactory = presentationFactory;
  }

  // maybe https://w3c-ccg.github.io/vp-request-spec/ ?
  @GET
  @Path("/did/comm/{requestedCredentialType}")
  public String request(@PathParam("requestedCredentialType") String requestedCredentialType) {

    // TODO Don't just give out credentials to anybody. There should be some check here for the
    // requestor
    final String audience = "TODO";

    switch (requestedCredentialType) {
      case VerifiableCredentialType.MEMBERSHIP_CREDENTIAL:
        final VerifiableCredential membershipCredential = credentialStore.getMembershipCredential();
        final SignedJWT membershipPresentation =
            presentationFactory.createPresentation(List.of(membershipCredential), audience);
        return membershipPresentation.getParsedString();
      default:
        throw new BadRequestException();
    }
  }
}
