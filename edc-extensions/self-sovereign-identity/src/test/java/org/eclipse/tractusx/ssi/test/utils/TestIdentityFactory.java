package org.eclipse.tractusx.ssi.test.utils;

import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.spi.did.PublicKey;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.spec.EdECPoint;
import java.security.spec.EdECPublicKeySpec;
import java.security.spec.NamedParameterSpec;
import java.util.Base64;
import java.util.List;

public class TestIdentityFactory {

    @SneakyThrows
    public static TestIdentity newIdentity() {

        final Did did = TestDidFactory.createRandom();

        var privateKeyBytes = Base64.getUrlDecoder().decode("nWGxne_9WmC6hEr0kuwsxERJxWl7MmkZcDusAxyuf2A");
        var publicKeyBytes = Base64.getUrlDecoder().decode("11qYAYKxCrfVS_7TyWQHOg7hcvPapiMlrwIaaPcHURo");

        final MultibaseString publicKeyMultiBase = MultibaseFactory.create(publicKeyBytes);
        final PublicKey publicKey = Ed25519VerificationKey2020.builder()
                .id(URI.create(did + "#key-1"))
                .controller(URI.create(did + "#controller"))
                .multibase(publicKeyMultiBase)
                .build();

        final DidDocument didDocument = DidDocument.builder()
                .id(did.toUri())
                .publicKeys(List.of(publicKey))
                .build();

        return new TestIdentity(did, didDocument, MultibaseFactory.create(publicKeyBytes), MultibaseFactory.create(privateKeyBytes));
    }

    @SneakyThrows
    public static PublicKey getPublicKey(byte[] pk) {
        // key is already converted from hex string to a byte array.
        KeyFactory kf = KeyFactory.getInstance("Ed25519");
        // determine if x was odd.
        boolean xisodd = false;
        int lastbyteInt = pk[pk.length - 1];
        if ((lastbyteInt & 255) >> 7 == 1) {
            xisodd = true;
        }
        // make sure most significant bit will be 0 - after reversing.
        pk[pk.length - 1] &= 127;
        // apparently we must reverse the byte array...
        pk = ReverseBytes(pk);
        BigInteger y = new BigInteger(1, pk);

        NamedParameterSpec paramSpec = new NamedParameterSpec("Ed25519");
        EdECPoint ep = new EdECPoint(xisodd, y);
        EdECPublicKeySpec pubSpec = new EdECPublicKeySpec(paramSpec, ep);
        PublicKey pub = kf.generatePublic(pubSpec);
        return pub;
    }
}
