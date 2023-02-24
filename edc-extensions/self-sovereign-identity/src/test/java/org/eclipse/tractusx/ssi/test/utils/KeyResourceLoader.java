package org.eclipse.tractusx.ssi.test.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemReader;

public class KeyResourceLoader {

  private static final String PRIVATE_KEY = "ed25519.pem";
  private static final String PUBLIC_KEY = "ed25519.pub.pem";

  private KeyResourceLoader() {}

  @SneakyThrows
  public static byte @NonNull [] readPrivateKey() {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(PRIVATE_KEY);
    if (inputStream == null) {
      throw new IllegalArgumentException(PRIVATE_KEY + " not found!");
    }

    final PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
    // final String base64Content = new String(pemReader.readPemObject().getContent());

    var bytes = pemReader.readPemObject().getContent();
    Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(bytes, 0);
    bytes = Hex.encode(privateKey.getEncoded());

    return bytes;
  }

  @SneakyThrows
  public static byte[] readPublicKey() {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(PUBLIC_KEY);
    if (inputStream == null) {
      throw new IllegalArgumentException(PUBLIC_KEY + " not found!");
    }

    String publicKeyBody =
        new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split(" ")[1];
    byte[] encoded = Base64.getDecoder().decode(publicKeyBody);
    Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(encoded, 0);
    byte[] bytes = publicKey.getEncoded();
    if (bytes.length != 32) {
      throw new Exception("Invalid PK Length should be 32");
    }
    return bytes;
  }
}
