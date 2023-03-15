package org.eclipse.tractusx.ssi.test.utils;

import jakarta.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.did.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;

public class TestDidDocumentResolver implements DidDocumentResolver {
  private final Map<Did, DidDocument> documents = new HashMap<>();

  @Override
  public DidMethod getSupportedMethod() {
    return TestDidFactory.DID_METHOD;
  }

  @Override
  public DidDocument resolve(Did did) {

    if (!documents.containsKey(did))
      throw new NotFoundException(
          String.format(
              "Did not found: %s. Got [%s]",
              did.toString(),
              documents.values().stream()
                  .map(DidDocument::toString)
                  .collect(Collectors.joining(", "))));

    return documents.get(did);
  }

  public void register(TestIdentity testIdentity) {
    documents.put(testIdentity.getDid(), testIdentity.getDidDocument());
  }

  public DidDocumentResolverRegistry withRegistry() {
    final DidDocumentResolverRegistry registry = new DidDocumentResolverRegistryImpl();
    registry.register(this);
    return registry;
  }
}
