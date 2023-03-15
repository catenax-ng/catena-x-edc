package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi;

import lombok.NonNull;

public interface MultibaseString {
  byte[] getDecoded();

  @NonNull
  String getEncoded();
}
