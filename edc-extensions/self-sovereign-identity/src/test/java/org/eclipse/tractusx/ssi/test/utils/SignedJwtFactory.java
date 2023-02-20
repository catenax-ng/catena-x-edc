package org.eclipse.tractusx.ssi.test.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedVerifiablePresentation;

import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.UUID;

public class SignedJwtFactory {

    public static SignedJWT createTestJwt(String issuer,
                                   String subject,
                                   String audience,
                                   SerializedVerifiablePresentation serializedPresentation,
                                          PrivateKey privateKey){
        var claimsSet =
                new JWTClaimsSet.Builder()
                        .issuer(issuer)
                        .subject(subject)
                        .audience(audience)
                        .claim("vp", serializedPresentation.getJson())
                        .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                        .jwtID(UUID.randomUUID().toString())
                        .build();

        ECDSASigner signer = null;
        try {
            signer = new ECDSASigner(privateKey, Curve.Ed25519);
            if (!signer.supportedJWSAlgorithms().contains(JWSAlgorithm.ES256)) {
                throw new RuntimeException("Invalid Signing Algorithm");
            }
            var algorithm = JWSAlgorithm.ES256;
            var header = new JWSHeader(algorithm);
            var vc = new SignedJWT(header, claimsSet);
            vc.sign(signer);
            return vc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
