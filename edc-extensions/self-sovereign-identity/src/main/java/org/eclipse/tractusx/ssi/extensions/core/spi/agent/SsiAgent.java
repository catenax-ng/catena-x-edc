package org.eclipse.tractusx.ssi.extensions.core.spi.agent;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

public interface SsiAgent {
  String getIdentifier();

  VerifiablePresentation check(SignedJWT jwtWithVerifiablePresentation);

  VerifiableCredential getByAll();

  VerifiableCredential getByType(String verifiableCredentialType);

  SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience);
}
