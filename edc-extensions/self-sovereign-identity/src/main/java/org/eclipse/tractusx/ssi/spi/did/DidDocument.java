package org.eclipse.tractusx.ssi.spi.did;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.net.URI;
import java.util.List;

@Value
@Builder
public class DidDocument {
  @NonNull URI id;

  @NonNull List<Ed25519VerificationKey2020> verificationMethods;
}
