package org.eclipse.tractusx.ssi.extensions.core.proof.verify;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.exception.DidDocumentResolverNotFoundException;
import org.eclipse.tractusx.ssi.extensions.core.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.DidParser;
import org.eclipse.tractusx.ssi.spi.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;

import java.net.URI;

public class LinkedDataVerifier {

    private final DidDocumentResolverRegistry didDocumentResolverRegistry;
    private final Monitor monitor;

    public LinkedDataVerifier(DidDocumentResolverRegistry didDocumentResolverRegistry, Monitor monitor) {
        this.didDocumentResolverRegistry = didDocumentResolverRegistry;
        this.monitor = monitor;
    }

    public boolean verify(HashedLinkedData hashedLinkedData, VerifiableCredential credential) {

        final URI issuer = credential.getIssuer();
        final Did issuerDid = DidParser.parse(issuer);

        final DidDocumentResolver didDocumentResolver;
        try {
            didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());
        } catch (DidDocumentResolverNotFoundException e) {
            monitor.severe("Could not check verifiable credential signature, because no DID Document Resolver is registered for method " + issuerDid.getMethod(), e);
            return false;
        }

        final DidDocument document = didDocumentResolver.resolve(issuerDid);

        final URI verificationMethodId = credential.getProof().getVerificationMethod();
        final Ed25519VerificationKey2020 key = document.getVerificationMethods().stream()
                .filter(v -> v.getId().equals(verificationMethodId))
                .map(Ed25519VerificationKey2020.class::cast)
                .findFirst()
                .orElseThrow();

        final MultibaseString publicKey = key.getMultibase();
        final MultibaseString signature = credential.getProof().getProofValue();

        var message = hashedLinkedData.getValue();
        AsymmetricKeyParameter publicKeyParameters = OpenSSHPublicKeyUtil.parsePublicKey(publicKey.getDecoded());
        Signer verifier = new Ed25519Signer();
        verifier.init(false, publicKeyParameters);
        verifier.update(message, 0, message.length);
        boolean verified = verifier.verifySignature(signature.getDecoded());

        return verified;
    }
}
