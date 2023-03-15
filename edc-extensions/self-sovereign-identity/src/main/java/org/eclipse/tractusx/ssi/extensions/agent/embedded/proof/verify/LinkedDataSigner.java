package org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.verify;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.hash.HashedLinkedData;

public class LinkedDataSigner {

  @SneakyThrows
  public byte[] sign(HashedLinkedData hashedLinkedData, byte[] signingKey) {

    final byte[] message = hashedLinkedData.getValue();
    AsymmetricKeyParameter privateKeyParameters;
    privateKeyParameters = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(signingKey);
    Signer signer = new Ed25519Signer();
    signer.init(true, privateKeyParameters);
    signer.update(message, 0, message.length);
    byte[] signature = new byte[0];
    try {
      signature = signer.generateSignature();
    } catch (CryptoException e) {
      throw new RuntimeException(e); // TODO
    }

    return signature;
  }
}
