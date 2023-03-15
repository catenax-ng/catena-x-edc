package org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation;

import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

@Value
@Builder
public class VerifiablePresentation {
  @NonNull URI id;
  @NonNull List<String> types;
  @NonNull URI holder;
  @NonNull List<VerifiableCredential> verifiableCredentials;
  Ed25519Proof proof;
}
