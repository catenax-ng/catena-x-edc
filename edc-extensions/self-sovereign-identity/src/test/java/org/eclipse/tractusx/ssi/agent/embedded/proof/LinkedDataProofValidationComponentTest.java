package org.eclipse.tractusx.ssi.agent.embedded.proof;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.test.utils.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LinkedDataProofValidationComponentTest {

  private LinkedDataProofValidation linkedDataProofValidation;

  private TestIdentity credentialIssuer;

  @BeforeEach
  public void setup() {

    final Monitor monitor = Mockito.mock(Monitor.class);
    final TestDidDocumentResolver didDocumentResolver = new TestDidDocumentResolver();

    credentialIssuer = TestIdentityFactory.newIdentity();
    didDocumentResolver.register(credentialIssuer);

    linkedDataProofValidation =
        new LinkedDataProofValidation(
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new LinkedDataVerifier(didDocumentResolver.withRegistry(), monitor),
            new LinkedDataSigner(),
            monitor);
  }

  @Test
  public void testLinkedDataProofCheck() {

    // prepare key
    URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();
    byte[] privateKey = credentialIssuer.getPrivateKey();

    VerifiableCredential credential = createCredential(null);

    final Ed25519Proof proof =
        linkedDataProofValidation.createProof(credential, verificationMethod, privateKey);

    credential = createCredential(proof);

    var isOk = linkedDataProofValidation.checkProof(credential);

    Assertions.assertTrue(isOk);
  }

  @SneakyThrows
  private VerifiableCredential createCredential(Ed25519Proof proof) {
    return VerifiableCredential.builder()
        .id(URI.create("did:test:id"))
        .types(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
        .issuer(credentialIssuer.getDid().toUri())
        .expirationDate(Instant.parse("2023-02-15T17:21:42Z").plusSeconds(3600))
        .issuanceDate(Instant.parse("2023-02-15T17:21:42Z"))
        .proof(proof)
        .credentialStatus(null)
        .build();
  }
}
