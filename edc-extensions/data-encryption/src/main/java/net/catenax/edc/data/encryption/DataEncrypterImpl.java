package net.catenax.edc.data.encryption;

import lombok.NonNull;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DataEncrypterImpl implements DataEncrypter {

    private final List<String> keys;
    private final EncryptionStrategy encryptionStrategy;
    private final DataEnveloper dataEnveloper;
    private final Monitor monitor;

    public DataEncrypterImpl(@NonNull Monitor monitor, @NonNull EncryptionStrategy encryptionStrategy, @NonNull DataEnveloper dataEnveloper, @NonNull List<String> keys) {
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("RotatingDataEncrypter requires at least one key");
        }

        this.monitor = monitor;
        this.keys = keys;
        this.encryptionStrategy = encryptionStrategy;
        this.dataEnveloper = dataEnveloper;
    }

    @Override
    public String encrypt(String value) {
        try {
            byte[] valueData = dataEnveloper.pack(value);
            byte[] keyData = keys.get(0).getBytes(StandardCharsets.UTF_8);
            return new String(encryptionStrategy.encrypt(valueData, keyData));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            throw new EdcException(e);
        }
    }

    @Override
    public String decrypt(String value) {
        return keys.stream()
                .map(key -> decrypt(value, key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(dataEnveloper::tryUnpack)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new EdcException("Failed to decrypt data. This can happen if the decryption key is not configured in the key set anymore, or because the data was originally not encrypted by this extension."));
    }

    private Optional<byte[]> decrypt(String value, String key) {
        try {
            byte[] valueData = value.getBytes(StandardCharsets.UTF_8);
            byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
            return Optional.ofNullable(encryptionStrategy.decrypt(valueData, keyData));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            monitor.warning(String.format("Unusable key in rotating key set. %s", e.getMessage()));
            return Optional.empty();
        }
    }
}
