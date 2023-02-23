package org.eclipse.tractusx.ssi.test.utils;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.spi.did.*;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.Objects;

public class TestIdentityFactory {

    private static final String KEY_ALGORITHM = "Ed25519";

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

        System.out.println("PRIVATE KEY " + keyPair.getPrivate().getEncoded());
        System.out.println("PUBLIC KEY " + keyPair.getPublic().getEncoded());

        return new TestIdentity(did, didDocument, keyPair);
    }

    @SneakyThrows
    private static KeyPair generateKeyPair() {
        return new KeyPair(readPublicKey(), readPrivateKey());
    }

    @SneakyThrows
    private static PrivateKey readPrivateKey() {
        final String resourceName = "ed25519/ed25519.pem";

        final InputStream is = TestIdentityFactory.class.getClassLoader().getResourceAsStream(resourceName);
        Objects.requireNonNull(is);

        final PemReader reader = new PemReader(new InputStreamReader(is));
        final byte[] decoded = reader.readPemObject().getContent();
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        final Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM, provider);
        return kf.generatePrivate(spec);
    }

    @SneakyThrows
    private static PublicKey readPublicKey() {
        final String resourceName = "ed25519/ed25519.pub.pem";

        final InputStream is = TestIdentityFactory.class.getClassLoader().getResourceAsStream(resourceName);
        Objects.requireNonNull(is);

        final PemReader reader = new PemReader(new InputStreamReader(is));
        final byte[] decoded = reader.readPemObject().getContent();
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

        final Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM, provider);
        return kf.generatePublic(spec);
    }

//    @SneakyThrows
//    private static PublicKey readPublicKey() {
//        final String resourceName = "ed25519/ed25519.pub.pem";
//        final InputStream is = TestIdentityFactory.class.getClassLoader().getResourceAsStream(resourceName);
//        Objects.requireNonNull(is);
//
//        final X509EncodedKeySpec spec = new X509EncodedKeySpec(is.readAllBytes());
//
//        final Provider provider = new BouncyCastleProvider();
//        Security.addProvider(provider);
//        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM, provider);
//        return kf.generatePublic(spec);
//    }


}
