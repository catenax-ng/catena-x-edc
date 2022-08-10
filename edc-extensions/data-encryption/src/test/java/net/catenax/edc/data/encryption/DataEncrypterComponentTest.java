package net.catenax.edc.data.encryption;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.aggregator.ArgumentAccessException;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DataEncrypterComponentTest {

    private final List<String> keys = List.of("743677397A244326462948404D635166", "442A472D4B614E645267556B58703273", "6251655368566D597133743677397A24");

    private DataEncrypter dataEncrypter;
    private EncryptionStrategy encryptionStrategy;
    private DataEnveloper dataEnveloper;

    // mocks
    private Monitor monitor;

    @BeforeEach
    public void setup() {
        monitor = Mockito.mock(Monitor.class);
        encryptionStrategy = new AesEncryptionStrategy();
        dataEnveloper = new DataEnveloper();

        dataEncrypter = new DataEncrypterImpl(monitor, encryptionStrategy, dataEnveloper, keys);
    }

    @Test
    public void throwsOnNoKeysProvided() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DataEncrypterImpl(monitor, encryptionStrategy, dataEnveloper, List.of()));
    }

    @Test
    public void testKeyRotation() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        final String expectedResult = "hello";
        final byte[] envelopedResult = dataEnveloper.pack(expectedResult);
        final byte[] encryptionKey = keys.get(keys.size() - 1).getBytes(StandardCharsets.UTF_8);
        final byte[] encryptedResult = encryptionStrategy.encrypt(envelopedResult, encryptionKey);

        var encryptedResult = dataEncrypter.encrypt(expectedResult);
        var result = dataEncrypter.decrypt(new String(encryptedResult, StandardCharsets.UTF_8));

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testSuccess() {
        final String expectedResult = "hello world!";

        var encryptedResult = dataEncrypter.encrypt(expectedResult);
        var result = dataEncrypter.decrypt(encryptedResult);

        Assertions.assertEquals(expectedResult, result);
    }

    public void testWarningOnInvalidKey() {

    }
}
