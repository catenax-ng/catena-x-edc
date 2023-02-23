package org.eclipse.tractusx.ssi.test.utils;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.spi.did.*;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.List;

public class TestIdentityFactory {


    public static TestIdentity newIdentity() {

        final Did did = TestDidFactory.createRandom();
        final KeyPair keyPair = generateKeyPair();
        final MultibaseString publicKeyMultiBase = MultibaseFactory.create(keyPair.getPublic().getEncoded());
        final Ed25519VerificationKey2020 verificationMethod = Ed25519VerificationKey2020.builder()
                .id(URI.create(did + "#key-1"))
                .controller(URI.create(did + "#controller"))
                .publicKeyMultibase(publicKeyMultiBase.getEncoded())
                .build();

        final DidDocument didDocument = DidDocument.builder()
                .id(did.toUri())
                .verificationMethods(List.of(verificationMethod))
                .build();

        return new TestIdentity(did, didDocument, keyPair);
    }

    @SneakyThrows
    private static KeyPair generateKeyPair() {
        // Add BC as Provider
        Security.addProvider(new BouncyCastleProvider());
        // Generate Keypair
        ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator keyPairGenerator = null;
        keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyPairGenerator.initialize(ecGenSpec, new SecureRandom());

        KeyPair pair = keyPairGenerator.generateKeyPair();

        ECPrivateKey privateKey = (ECPrivateKey) pair.getPrivate();
        ECPublicKey publicKey = (ECPublicKey) pair.getPublic();

        return pair;
    }

}
