package org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

public class SerializedJwtPresentationFactoryImpl implements SerializedJwtPresentationFactory {

  private final SignedJwtFactory signedJwtFactory;
  private final JsonLdSerializer jsonLdSerializer;

  public SerializedJwtPresentationFactoryImpl(
      SignedJwtFactory signedJwtFactory, JsonLdSerializer jsonLdSerializer) {
    this.signedJwtFactory = signedJwtFactory;
    this.jsonLdSerializer = jsonLdSerializer;
  }

  @Override
  public SignedJWT createPresentation(List<VerifiableCredential> credentials, String audience) {
    final VerifiablePresentation verifiablePresentation =
        VerifiablePresentation.builder().verifiableCredentials(credentials).build();

    final SerializedVerifiablePresentation serializedVerifiablePresentation =
        jsonLdSerializer.serializePresentation(verifiablePresentation);

    return signedJwtFactory.create(audience, serializedVerifiablePresentation);
  }
}
