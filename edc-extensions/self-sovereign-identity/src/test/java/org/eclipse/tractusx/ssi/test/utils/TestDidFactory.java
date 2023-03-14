package org.eclipse.tractusx.ssi.test.utils;

import java.util.UUID;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.spi.did.DidMethodIdentifier;

public class TestDidFactory {
  public static final DidMethod DID_METHOD = new DidMethod("test");

  public static Did createRandom() {
    return new Did(DID_METHOD, new DidMethodIdentifier(UUID.randomUUID().toString()));
  }
}
