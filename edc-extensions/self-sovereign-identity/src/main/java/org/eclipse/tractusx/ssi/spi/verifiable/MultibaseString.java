package org.eclipse.tractusx.ssi.spi.verifiable;

import lombok.NonNull;


public interface MultibaseString {
  @NonNull
  byte[] getEncoded();

  @NonNull
  String getDecoded();

  default MultibaseString getInstance(String instance){
    return MultibaseFactory.create(instance);
  }
}
