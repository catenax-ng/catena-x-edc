package net.catenax.edc.data.encryption.provider;

import lombok.RequiredArgsConstructor;
import net.catenax.edc.data.encryption.DataEncryptionExtension;
import org.eclipse.dataspaceconnector.spi.security.Vault;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class SymmetricKeyProvider implements KeyProvider {

    private static final String KEY_SEPARATOR = ",";

    private final Vault vault;
    private final String vaultKeyAlias;

    @Override
    public Stream<byte[]> getDecryptionKeySet() {
        return getKeysStream();
    }

    @Override
    public byte[] getEncryptionKey() {
        return getKeysStream().findFirst()
                .orElseThrow(() -> new RuntimeException(DataEncryptionExtension.NAME
                        + ": Vault must contain at least one key."));
    }

    public Stream<byte[]> getKeysStream() {
        return Arrays.stream(getKeys().split(KEY_SEPARATOR))
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(String::getBytes);
    }

    public String getKeys() {
        String keys = vault.resolveSecret(vaultKeyAlias);
        return keys == null ? "" : keys;
    }

}
