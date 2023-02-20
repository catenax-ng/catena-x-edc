package org.eclipse.tractusx.ssi.test.utils;

import jakarta.ws.rs.NotFoundException;
import org.eclipse.tractusx.ssi.extensions.core.resolver.did.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.spi.did.*;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;

import java.util.HashMap;
import java.util.Map;

public class TestDidDocumentResolver implements DidDocumentResolver {
    private final Map<Did, DidDocument> documents = new HashMap<>();

    @Override
    public DidMethod getSupportedMethod() {
        return TestDidFactory.DID_METHOD;
    }

    @Override
    public DidDocument resolve(Did did) {

        if (!documents.containsKey(did))
            throw new NotFoundException(did.toString());

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
