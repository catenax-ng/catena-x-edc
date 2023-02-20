package org.eclipse.tractusx.ssi.test.utils;

import lombok.Value;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.verifiable.MultibaseString;

@Value
public class TestIdentity {
    Did did;
    DidDocument didDocument;
    MultibaseString publicKey;
    MultibaseString privateKey;
}
