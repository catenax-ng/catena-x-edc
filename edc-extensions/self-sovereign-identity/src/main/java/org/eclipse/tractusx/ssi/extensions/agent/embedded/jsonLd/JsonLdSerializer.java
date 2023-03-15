package org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;

// TODO This hides danubtech
public interface JsonLdSerializer {

  SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation);

  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation);
}
