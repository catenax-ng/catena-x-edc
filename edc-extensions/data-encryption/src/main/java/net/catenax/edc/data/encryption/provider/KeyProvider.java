package net.catenax.edc.data.encryption.provider;

import java.util.stream.Stream;

public interface KeyProvider {
    byte[] getEncryptionKey();
    Stream<byte[]> getDecryptionKeySet();
}
