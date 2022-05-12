/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.core.security.hashicorp;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.testcontainers.shaded.org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class X509TestCertificateGenerator {
    private static final String SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";
    private final Provider PROVIDER = new BouncyCastleProvider();
    private final JcaX509CertificateConverter JCA_X509_CERTIFICATE_CONVERTER = new JcaX509CertificateConverter().setProvider(PROVIDER);

    X509Certificate generateCertificate(int validity, String cn) throws CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException {

        KeyPair keyPair = generateKeyPair();

        Instant now = Instant.now();
        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(keyPair.getPrivate());
        X500Name issuer = new X500Name(String.format("CN=%s", Optional.ofNullable(cn).map(String::trim).filter(s -> !s.isEmpty()).orElse("rootCA")));
        BigInteger serial = BigInteger.valueOf(now.toEpochMilli());
        Date notBefore = Date.from(now);
        Date notAfter = Date.from(now.plus(Duration.ofDays(validity)));
        PublicKey publicKey = keyPair.getPublic();
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                issuer,
                publicKey
        );
        certificateBuilder = certificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(publicKey));
        certificateBuilder = certificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(publicKey));
        certificateBuilder = certificateBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        return JCA_X509_CERTIFICATE_CONVERTER.getCertificate(certificateBuilder.build(contentSigner));
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", PROVIDER);
        keyPairGenerator.initialize(1024, new SecureRandom());

        return keyPairGenerator.generateKeyPair();
    }

    private SubjectKeyIdentifier createSubjectKeyId(PublicKey publicKey) throws OperatorCreationException {
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
    }

    private AuthorityKeyIdentifier createAuthorityKeyId(PublicKey publicKey) throws OperatorCreationException {
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
    }

    @SneakyThrows
    String convertToPem (X509Certificate certificate) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
                pemWriter.writeObject(certificate);
                pemWriter.flush();
            }
            return stream.toString(StandardCharsets.UTF_8);
        }
    }
}
