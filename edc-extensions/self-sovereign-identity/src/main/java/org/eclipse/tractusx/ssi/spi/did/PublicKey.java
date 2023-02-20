package org.eclipse.tractusx.ssi.spi.did;

import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

import java.net.URI;

public interface PublicKey {
  URI getId();

  MultibaseString getMultibase();
}
