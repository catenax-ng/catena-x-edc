package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode
public class DidMethod {
  @EqualsAndHashCode.Include @NonNull String value;

  public DidMethod(String val) {
    if (val.isEmpty()) {
      throw new IllegalArgumentException("Empty value not allowed");
    }
    this.value = val;
  }

  @Override
  public String toString() {
    return value;
  }
}
