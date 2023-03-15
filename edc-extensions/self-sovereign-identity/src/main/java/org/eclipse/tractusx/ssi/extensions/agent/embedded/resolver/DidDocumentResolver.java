package org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;

public interface DidDocumentResolver {

  DidMethod getSupportedMethod();

  DidDocument resolve(Did did);
}
