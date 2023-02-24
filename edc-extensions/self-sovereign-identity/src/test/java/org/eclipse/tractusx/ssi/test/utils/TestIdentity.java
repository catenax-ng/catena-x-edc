package org.eclipse.tractusx.ssi.test.utils;

import lombok.Value;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;

@Value
public class TestIdentity {
    Did did;
    DidDocument didDocument;
    byte[] publicKey;
    byte[] privateKey;
}
