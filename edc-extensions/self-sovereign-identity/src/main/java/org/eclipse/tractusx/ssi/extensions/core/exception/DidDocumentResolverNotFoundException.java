package org.eclipse.tractusx.ssi.extensions.core.exception;

import org.eclipse.tractusx.ssi.spi.did.Did;

public class DidDocumentResolverNotFoundException extends Exception {
  public DidDocumentResolverNotFoundException(Did did, String contextMessage) {
    super(
        String.format(
            "No DID document resolver registered for DID method '%s'. %s",
            did.getMethod(), contextMessage));
  }
}
