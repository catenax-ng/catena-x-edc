package org.eclipse.tractusx.ssi.extensions.core.proof.verify;

import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.eclipse.tractusx.ssi.extensions.core.proof.hash.HashedLinkedData;

public class LinkedDataSigner {

  public byte[] sign(HashedLinkedData hashedLinkedData, byte[] signingKey) {

    final byte[] signature = new byte[64];

    System.out.println("PRIVATE KEY " + signingKey);

    Ed25519.sign(
        signingKey,
        0,
        hashedLinkedData.getValue(),
        0,
        hashedLinkedData.getValue().length,
        signature,
        0);

    return signature;
  }
}
