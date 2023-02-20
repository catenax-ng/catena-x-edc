package org.eclipse.tractusx.ssi.extensions.core.proof.verify;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.eclipse.tractusx.ssi.extensions.core.proof.hash.HashedLinkedData;

import java.security.PrivateKey;

public class LinkedDataSigner {

    @SneakyThrows
    public byte[] sign(HashedLinkedData message, byte[] privateKey) {
        final Signer verifier = new Ed25519Signer();

        final Ed25519PrivateKeyParameters ed25519PrivateKeyParameters = new Ed25519PrivateKeyParameters(privateKey, 0);
        verifier.init(true, ed25519PrivateKeyParameters);
        verifier.update(message.getValue(), 0, message.getValue().length);

        var sig = verifier.generateSignature();
        System.out.println("SIGNATURE " +  sig);

        return sig;
    }
}
