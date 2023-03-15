package org.eclipse.tractusx.ssi.extensions.agent.custodian;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

public class CustodianAgent implements SsiAgent {

  @Override
  public String getIdentifier() {
    return null;
  }

  @Override
  public VerifiablePresentation check(SignedJWT jwtWithVerifiablePresentation) {
    return null;
  }

  @Override
  public VerifiableCredential getByAll() {
    return null;
  }

  @Override
  public VerifiableCredential getByType(String verifiableCredentialType) {
    return null;
  }

  @Override
  public SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience) {
    return null;
  }
}
