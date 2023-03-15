package org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.util.List;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.DidDocumentResolverNotFoundException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidParser;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Ed25519VerificationKey2020;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtVerifier {

  private final DidDocumentResolverRegistry didDocumentResolverRegistry;
  private final Monitor monitor;

  public SignedJwtVerifier(
      DidDocumentResolverRegistry didDocumentResolverRegistry, Monitor monitor) {
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
      monitor.severe(
          "Could not validate JWT signature, because no DID Document resolver is registered for method "
              + issuerDid.getMethod(),
          e);
      return false;
    }

    final DidDocument issuerDidDocument = didDocumentResolver.resolve(issuerDid);
    final List<Ed25519VerificationKey2020> verificationMethods =
        issuerDidDocument.getVerificationMethods();

    // verify JWT signature
    try {
      for (Ed25519VerificationKey2020 method : verificationMethods) {
        boolean verified = jwt.verify(new ECDSAVerifier((ECPublicKey) method.getKey()));
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
