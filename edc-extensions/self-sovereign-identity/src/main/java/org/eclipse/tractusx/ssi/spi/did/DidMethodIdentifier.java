package org.eclipse.tractusx.ssi.spi.did;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode
public class DidMethodIdentifier {
  @NonNull @EqualsAndHashCode.Include String value;

  public DidMethodIdentifier(String val) {
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
