package org.eclipse.tractusx.ssi.extensions.core.proof;

import lombok.SneakyThrows;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.extensions.core.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.extensions.core.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.extensions.core.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.extensions.core.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.spi.did.DidParser;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.spi.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.spi.verifiable.Ed25519Proof;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.test.utils.TestDidDocumentResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LinkedDataProofValidationComponentTest {

    private LinkedDataProofValidation linkedDataProofValidation;

    // fake
    private TestDidDocumentResolver didDocumentResolver;
    private TestIdentity testIdentity;

    // mocks


    private Monitor monitor;

    @BeforeEach
    public void setup() {

        testIdentity = TestIdentityFactory.newIdentity();
        didDocumentResolver = new TestDidDocumentResolver();
        didDocumentResolver.register(testIdentity);
        monitor = Mockito.mock(Monitor.class);

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
        URI verificationMethod = testIdentity.getDidDocument().getVerificationMethods().get(0).getId();
        byte[] privateKey = testIdentity.getPrivateKey();

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
                .issuer(testIdentity.getDid().toUri())
                .expirationDate(new Date(2025, 1, 1))
                .issuanceDate(new Date(2020, 1, 1))
                .proof(proof)
                .credentialStatus(null)
                .build();
    }
}
