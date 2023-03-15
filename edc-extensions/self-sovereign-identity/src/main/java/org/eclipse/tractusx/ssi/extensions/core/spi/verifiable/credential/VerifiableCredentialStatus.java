package org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential;

import java.net.URI;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class VerifiableCredentialStatus {
  URI id;
  String type;
}