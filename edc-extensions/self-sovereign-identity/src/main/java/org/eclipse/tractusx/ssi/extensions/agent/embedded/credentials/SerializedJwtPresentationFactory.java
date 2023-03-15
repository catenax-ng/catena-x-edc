package org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

public interface SerializedJwtPresentationFactory {
  SignedJWT createPresentation(List<VerifiableCredential> credentials, String audience);
}
