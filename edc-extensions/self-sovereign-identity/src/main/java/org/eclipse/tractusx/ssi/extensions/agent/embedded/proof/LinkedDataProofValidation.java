package org.eclipse.tractusx.ssi.extensions.agent.embedded.proof;

import java.net.URI;
import java.time.Instant;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.MultibaseString;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

public class LinkedDataProofValidation {

  public static LinkedDataProofValidation create(
      DidDocumentResolverRegistry didDocumentResolverRegistry, Monitor monitor) {
    return new LinkedDataProofValidation(
        new LinkedDataHasher(),
        new LinkedDataTransformer(),
        new LinkedDataVerifier(didDocumentResolverRegistry, monitor),
        new LinkedDataSigner(),
        monitor);
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final LinkedDataVerifier verifier;
  private final LinkedDataSigner signer;
  private final Monitor monitor;

  public LinkedDataProofValidation(
      LinkedDataHasher hasher,
      LinkedDataTransformer transformer,
      LinkedDataVerifier verifier,
      LinkedDataSigner signer,
      Monitor monitor) {
    this.hasher = hasher;
    this.transformer = transformer;
    this.verifier = verifier;
    this.signer = signer;
    this.monitor = monitor;
  }

  public boolean checkProof(VerifiableCredential verifiableCredential) {
    // TODO Asser proof is linked data proof
    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    final boolean isProofed = verifier.verify(hashedData, verifiableCredential);

    if (isProofed) {
      monitor.debug(
          String.format(
              "Successfully verified signature of verifiable credential proof (id=%s, issuer=%s)",
              verifiableCredential.getId(), verifiableCredential.getIssuer()));
    } else {
      monitor.warning(
          String.format(
              "Signature verification failed for verifiable credential proof (id=%s, issuer=%s)",
              verifiableCredential.getId(), verifiableCredential.getIssuer()));
    }

    return isProofed;
  }

  // TODO move this into other class
  public Ed25519Proof createProof(
      VerifiableCredential verifiableCredential, URI verificationMethodId, byte[] signingKey) {
    var transformedData = transformer.transform(verifiableCredential);
    var hashedData = hasher.hash(transformedData);
    var signature = signer.sign(hashedData, signingKey);
    MultibaseString multibaseString = MultibaseFactory.create(signature);

    return Ed25519Proof.builder()
        .created(Instant.now())
        .verificationMethod(verificationMethodId)
        .proofValue(multibaseString)
        .build();
  }
}
