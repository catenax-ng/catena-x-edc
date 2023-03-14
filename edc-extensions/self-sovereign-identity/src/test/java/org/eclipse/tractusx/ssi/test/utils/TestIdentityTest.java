package org.eclipse.tractusx.ssi.test.utils;

import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.junit.jupiter.api.Test;

public class TestIdentityTest {

  @Test
  @SneakyThrows
  public void testPublicPrivateKey() {

    byte[] message = "Json String".getBytes(StandardCharsets.UTF_8);

    // Load public key
    var identity = TestIdentityFactory.newIdentity();

    // Sign
    AsymmetricKeyParameter privateKeyParameters;
    privateKeyParameters = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(identity.getPrivateKey());
    Signer signer = new Ed25519Signer();
    signer.init(true, privateKeyParameters);
    signer.update(message, 0, message.length);
    byte[] signature = signer.generateSignature();

    // Verify
    AsymmetricKeyParameter publicKeyParameters =
        OpenSSHPublicKeyUtil.parsePublicKey(identity.getPublicKey());
    Signer verifier = new Ed25519Signer();
    verifier.init(false, publicKeyParameters);
    verifier.update(message, 0, message.length);
    boolean verified = verifier.verifySignature(signature);

    System.out.println("Verification: " + verified); // Verification: true
  }
}
