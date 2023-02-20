package org.eclipse.tractusx.ssi.extensions.core.proof;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import jakarta.xml.bind.DatatypeConverter;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
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
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class LinkedDataProofValidationComponentTest {

    private LinkedDataProofValidation linkedDataProofValidation;

    // fake
    private TestDidDocumentResolver didDocumentResolver;
    private TestIdentity credentialIssuer;

    // mocks
    private Monitor monitor;

    @BeforeEach
    public void setup() {

        credentialIssuer = TestIdentityFactory.newIdentity();
        didDocumentResolver = new TestDidDocumentResolver();
        didDocumentResolver.register(credentialIssuer);
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
        URI verificationMethodId = credentialIssuer.getDidDocument().getPublicKeys().get(0).getId();
        VerifiableCredential credential = createCredential(null);

        final Ed25519Proof proof =
                linkedDataProofValidation.createProof(credential, verificationMethodId, credentialIssuer.getPrivateKey().getEncoded());

        credential = createCredential(proof);

        var isOk = linkedDataProofValidation.checkProof(credential);

        Assertions.assertTrue(isOk);
    }


//    @Test
//    public void testSigning() {
//
//        var signer = new LinkedDataSigner();
//        var verifier = new LinkedDataVerifier(didDocumentResolver.withRegistry(), monitor);
//
//        final URI verificationMethodId = credentialIssuer.getDidDocument().getPublicKeys().get(0).getId();
//        VerifiableCredential credential = createCredential(null); // used to find the public key
//        final Ed25519Proof proof =
//                linkedDataProofValidation.createProof(credential, verificationMethodId, credentialIssuer.getPrivateKey());
//        credential = createCredential(proof);
//
//
//        final byte[] unsignedData = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
//        final HashedLinkedData unsignedHashedData = new HashedLinkedData(unsignedData);
//
//        final byte[] signedData = signer.sign(unsignedHashedData, credentialIssuer.getPrivateKey());
//        final HashedLinkedData signedHashedData = new HashedLinkedData(signedData);
//
//        final boolean signedCorrectly = verifier.verify(signedHashedData, credential);
//
//        Assertions.assertTrue(signedCorrectly);
//    }

//
//    @SneakyThrows
//    @Test
//    public void testSigning2() {
//
//        final byte[] message = "Foo Bar".getBytes(StandardCharsets.UTF_8);
//        final KeyPair keyPair = credentialIssuer.getKeyPair();
//
//        final Signer signer = new Ed25519Signer();
//
//        final Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = new Ed25519PrivateKeyParameters(keyPair.getPrivate().getEncoded(), 0);
//        signer.init(true, ed25519PrivateKeyParameters);
//        signer.update(message, 0, message.length);
//
//        final byte[] signedData = signer.generateSignature();
//
//        final Signer verifier = new Ed25519Signer();
//        final Ed25519PublicKeyParameters ed25519PublicKeyParameters = new Ed25519PublicKeyParameters(keyPair.getPublic().getEncoded(), 0);
//        verifier.init(false, ed25519PublicKeyParameters);
//        verifier.update(message, 0, message.length);
//        final boolean signedCorrectly = verifier.verifySignature(signedData);
//
//        Assertions.assertTrue(signedCorrectly);
//    }
//
//    @SneakyThrows
//    @Test
//    public void testSigning3() {
//
//        byte[] message = "Json String".getBytes(StandardCharsets.UTF_8);
//
//        Security.addProvider(new BouncyCastleProvider());
//        var keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
//        //var keyPair = credentialIssuer.getKeyPair();
//
//        AsymmetricCipherKeyPairGenerator gen = new Ed25519KeyPairGenerator();
//        gen.init(new KeyGenerationParameters(new SecureRandom(), 255));
//        AsymmetricCipherKeyPair pair = gen.generateKeyPair();
//        AsymmetricKeyParameter bprv = pair.getPrivate();
//        AsymmetricKeyParameter bpuv = pair.getPublic();
//
//// Sign
//        Signer signer = new Ed25519Signer();
//        final Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = new Ed25519PrivateKeyParameters(keyPair.getPrivate().getEncoded(), 0);
//        signer.init(true, ed25519PrivateKeyParameters);
//        signer.update(message, 0, message.length);
//        byte[] signature = signer.generateSignature();
//
//// Verify
//        Signer verifier = new Ed25519Signer();
//        final Ed25519PublicKeyParameters ed25519PublicKeyParameters = new Ed25519PublicKeyParameters(keyPair.getPublic().getEncoded(), 0);
//        verifier.init(false, ed25519PublicKeyParameters);
//        verifier.update(message, 0, message.length);
//        boolean verified = verifier.verifySignature(signature);
//
//        System.out.println("Verification: " + verified); // Verification: true
//        Assertions.assertTrue(verified);
//    }
//
    @SneakyThrows
    @Test
    public void test4() {
        System.out.println("ED25519 with BC");
        Security.addProvider(new BouncyCastleProvider());
        Provider provider = Security.getProvider("BC");
        System.out.println("Provider          :" + provider.getName() + " Version: " + provider.getVersion());
        // generate ed25519 keys
        SecureRandom RANDOM = new SecureRandom();
        Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
        keyPairGenerator.init(new Ed25519KeyGenerationParameters(RANDOM));

        OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519)
                .keyID("123")
                .generate();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        var test =jwk.toKeyPair();
        var privateKeyBytes = credentialIssuer.getPrivateKey().getEncoded();
        var publicKeyBytes = credentialIssuer.getPublicKey().getEncoded();

        //AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        //var keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
//        Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
//        var privateKeyBytes = Base64.getUrlDecoder().decode("nWGxne_9WmC6hEr0kuwsxERJxWl7MmkZcDusAxyuf2A");

//        Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters) asymmetricCipherKeyPair.getPublic();
//        var publicKeyBytes = Base64.getUrlDecoder().decode("11qYAYKxCrfVS_7TyWQHOg7hcvPapiMlrwIaaPcHURo");

        final Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);
        final Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(publicKeyBytes, 0);
        // the message
        byte[] message = "Message to sign".getBytes("utf-8");
        // create the signature
        Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);
        signer.update(message, 0, message.length);
        byte[] signature = signer.generateSignature();
        // verify the signature
        Signer verifier = new Ed25519Signer();
        verifier.init(false, publicKey);
        verifier.update(message, 0, message.length);
        boolean shouldVerify = verifier.verifySignature(signature);
        // output
        byte[] privateKeyEncoded = privateKey.getEncoded();
        byte[] publicKeyEncoded = publicKey.getEncoded();
        System.out.println("privateKey Length :" + privateKeyEncoded.length + " Data:"
                + DatatypeConverter.printHexBinary(privateKeyEncoded));
        System.out.println("publicKey Length  :" + publicKeyEncoded.length + " Data:"
                + DatatypeConverter.printHexBinary(publicKeyEncoded));
        System.out.println(
                "signature Length  :" + signature.length + " Data:" + DatatypeConverter.printHexBinary(signature));
        System.out.println("signature correct :" + shouldVerify);
        // rebuild the keys
        System.out.println("Rebuild the keys and verify the signature with rebuild public key");
        Ed25519PrivateKeyParameters privateKeyRebuild = new Ed25519PrivateKeyParameters(privateKeyEncoded, 0);
        Ed25519PublicKeyParameters publicKeyRebuild = new Ed25519PublicKeyParameters(publicKeyEncoded, 0);
        byte[] privateKeyRebuildEncoded = privateKeyRebuild.getEncoded();
        System.out.println("privateKey Length :" + privateKeyRebuild.getEncoded().length + " Data:"
                + DatatypeConverter.printHexBinary(privateKeyRebuild.getEncoded()));
        byte[] publicKeyRebuildEncoded = publicKeyRebuild.getEncoded();
        System.out.println("publicKey Length  :" + publicKeyRebuild.getEncoded().length + " Data:"
                + DatatypeConverter.printHexBinary(publicKeyRebuild.getEncoded()));
        // compare the keys
        System.out.println("private Keys Equal:" + Arrays.equals(privateKeyEncoded, privateKeyRebuildEncoded));
        System.out.println("public Keys Equal :" + Arrays.equals(publicKeyEncoded, publicKeyRebuildEncoded));
        // verify the signature with rebuild public key
        Signer verifierRebuild = new Ed25519Signer();
        verifierRebuild.init(false, publicKeyRebuild);
        verifierRebuild.update(message, 0, message.length);
        boolean shouldVerifyRebuild = verifierRebuild.verifySignature(signature);
        System.out.println("signature correct :" + shouldVerifyRebuild + " with rebuild public key");
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
