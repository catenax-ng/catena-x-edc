package org.eclipse.tractusx.ssi.test.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

public class TestIdentityFactory {

  public static TestIdentity newIdentity() {

    final Did did = TestDidFactory.createRandom();
    final byte[] publicKey = readPublicKey();
    final byte[] privateKey = readPrivateKey();
    final MultibaseString publicKeyMultiBase = MultibaseFactory.create(publicKey);
    final Ed25519VerificationKey2020 verificationMethod =
        Ed25519VerificationKey2020.builder()
            .id(URI.create(did + "#key-1"))
            .controller(URI.create(did + "#controller"))
            .multibase(publicKeyMultiBase)
            .build();

    final DidDocument didDocument =
        DidDocument.builder()
            .id(did.toUri())
            .verificationMethods(List.of(verificationMethod))
            .build();

    return new TestIdentity(did, didDocument, publicKey, privateKey);
  }

  @SneakyThrows
  private static byte[] readPrivateKey() {
    final String resourceName = "ed25519/ed25519.pem";

    final InputStream is =
        TestIdentityFactory.class.getClassLoader().getResourceAsStream(resourceName);
    Objects.requireNonNull(is);
    final PemReader reader = new PemReader(new InputStreamReader(is));
    return reader.readPemObject().getContent();
  }

  @SneakyThrows
  private static byte[] readPublicKey() {
    final String resourceName = "ed25519/ed25519.pem.pub";

    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
    Objects.requireNonNull(inputStream);

    var key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split(" ")[1];
    return Base64.getDecoder().decode(key);
  }
}
