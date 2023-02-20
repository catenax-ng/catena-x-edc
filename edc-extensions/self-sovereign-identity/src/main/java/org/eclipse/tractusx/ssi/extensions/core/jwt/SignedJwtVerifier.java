package org.eclipse.tractusx.ssi.extensions.core.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.exception.DidDocumentResolverNotFoundException;
import org.eclipse.tractusx.ssi.spi.did.DidParser;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.PublicKey;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;

import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.util.List;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtVerifier {

    private final DidDocumentResolverRegistry didDocumentResolverRegistry;
    private final Monitor monitor;

    public SignedJwtVerifier(DidDocumentResolverRegistry didDocumentResolverRegistry, Monitor monitor) {
        this.didDocumentResolverRegistry = didDocumentResolverRegistry;
        this.monitor = monitor;
    }

    /**
     * Verifies a VerifiableCredential using the issuer's public key
     *
     * @param jwt a {@link SignedJWT} that was sent by the claiming party.
     * @return true if verified, false otherwise
     */
    public boolean verify(SignedJWT jwt) throws JOSEException {

        JWTClaimsSet jwtClaimsSet;
        try {
            jwtClaimsSet = jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new JOSEException(e.getMessage());
        }

        final String issuer = jwtClaimsSet.getIssuer();
        final Did issuerDid = DidParser.parse(issuer);

        final DidDocumentResolver didDocumentResolver;
        try {
            didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());
        } catch (DidDocumentResolverNotFoundException e) {
            monitor.severe("Could not validate JWT signature, because no DID Document resolver is registered for method " + issuerDid.getMethod(), e);
            return false;
        }

        final DidDocument issuerDidDocument = didDocumentResolver.resolve(issuerDid);
        final List<PublicKey> publicKeys = issuerDidDocument.getPublicKeys();

        // verify JWT signature
        try {
            for (org.eclipse.tractusx.ssi.spi.did.PublicKey pk : publicKeys) {
                boolean verified = jwt.verify(new ECDSAVerifier((ECPublicKey) pk.getMultibase()));
                monitor.debug("Successfully validated JWT signature for DID " + issuerDid);
                if (verified) {
                    return true;
                }
            }

            monitor.warning("JWT signature validation failed for DID " + issuerDid);
            return false;
        } catch (JOSEException e) {
            throw new JOSEException(e.getMessage());
        }
    }
}
