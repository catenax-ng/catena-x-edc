package org.eclipse.tractusx.ssi.spi.did;

import lombok.*;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Value
@Builder
public class Ed25519VerificationKey2020 {
    public static final String TYPE = "Ed25519VerificationKey2020";

    // The id of the verification method SHOULD be the JWK thumbprint calculated from the publicKeyMultibase property
    @NonNull URI id;

    // The controller of the verification method SHOULD be a URI.
    @NonNull URI controller;
    @Builder.Default
    @NonNull String didVerificationMethodType = TYPE;
    @NonNull String publicKeyMultibase;

    @SneakyThrows
    public PublicKey getKey() {
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(getMultibaseString().getDecoded());
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        return kf.generatePublic(spec);
    }

    public MultibaseString getMultibaseString() {
        return MultibaseFactory.create(getPublicKeyMultibase());
    }
}
