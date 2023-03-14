package org.eclipse.tractusx.ssi.spi.verifiable;

import lombok.NonNull;

public interface MultibaseString {
  byte[] getDecoded();

  @NonNull
  String getEncoded();
}
