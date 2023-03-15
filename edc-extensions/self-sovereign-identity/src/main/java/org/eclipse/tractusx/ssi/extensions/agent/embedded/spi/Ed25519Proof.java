package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi;

import java.net.URI;
import java.time.Instant;
import lombok.*;

@Value
@Builder
@ToString
@EqualsAndHashCode
public class Ed25519Proof {

  public static final String TYPE = "Ed25519Signature2020";

  @NonNull @Builder.Default String type = "Ed25519Signature2020";

  @NonNull @Builder.Default String proofPurpose = "assertionMethod";
  @NonNull Instant created;
  @NonNull URI verificationMethod;
  @NonNull MultibaseString proofValue;
}
