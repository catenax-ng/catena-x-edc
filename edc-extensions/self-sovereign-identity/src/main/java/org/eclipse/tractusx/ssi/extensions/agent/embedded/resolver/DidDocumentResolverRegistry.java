package org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.DidDocumentResolverNotFoundException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;

public interface DidDocumentResolverRegistry {
  DidDocumentResolver get(DidMethod did) throws DidDocumentResolverNotFoundException;

  void register(DidDocumentResolver resolver);

  void unregister(DidDocumentResolver resolver);
}
