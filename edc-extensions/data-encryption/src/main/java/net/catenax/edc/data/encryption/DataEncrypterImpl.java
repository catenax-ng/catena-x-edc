package net.catenax.edc.data.encryption;

import lombok.NonNull;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DataEncrypterImpl implements DataEncrypter {

    private final KeyProvider keyProvider;
    private final EncryptionStrategy encryptionStrategy;
    private final DataEnveloper dataEnveloper;
    private final Monitor monitor;

    public DataEncrypterImpl(@NonNull Monitor monitor, @NonNull EncryptionStrategy encryptionStrategy, @NonNull DataEnveloper dataEnveloper, @NonNull KeyProvider keyProvider) {
        this.monitor = monitor;
        this.keyProvider = keyProvider;
        this.encryptionStrategy = encryptionStrategy;
        this.dataEnveloper = dataEnveloper;
    }

    @Override
    public String encrypt(String value) {
        try {
            byte[] packedData = dataEnveloper.pack(value);
            byte[] key = keyProvider.getEncryptionKey();
            return new String(encryptionStrategy.encrypt(packedData, key));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            throw new EdcException(e);
        }
    }

    @Override
    public String decrypt(String value) {
        return keyProvider.getDecryptionKeySet()
                .map(key -> decrypt(value, key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(dataEnveloper::tryUnpack)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new EdcException(DataEncryptionExtension.NAME + ": Failed to decrypt data. This can happen if the key set is empty, contains invalid keys, the decryption key rotated out of the key set or because the data was originally not encrypted by this extension."));
    }

    private Optional<byte[]> decrypt(String value, byte[] key) {
        try {
            byte[] valueData = value.getBytes(StandardCharsets.UTF_8);
            return Optional.ofNullable(encryptionStrategy.decrypt(valueData, key));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            monitor.warning(String.format(DataEncryptionExtension.NAME + ": Unusable key in rotating key set. %s", e.getMessage()));
            return Optional.empty();
        }
    }
}
