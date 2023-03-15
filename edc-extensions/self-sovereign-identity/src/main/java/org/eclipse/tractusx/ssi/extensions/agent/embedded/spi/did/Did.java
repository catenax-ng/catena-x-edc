package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did;

import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode
public class Did {

  @EqualsAndHashCode.Include @NonNull DidMethod method;
  @EqualsAndHashCode.Include @NonNull DidMethodIdentifier methodIdentifier;

  public URI toUri() {
    return URI.create(toString());
  }

  @Override
  public String toString() {
    return String.format("did:%s:%s", method, methodIdentifier);
  }
}
