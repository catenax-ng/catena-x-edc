package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did;

import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class DidDocument {
  @NonNull URI id;

  @NonNull List<Ed25519VerificationKey2020> verificationMethods;
}
