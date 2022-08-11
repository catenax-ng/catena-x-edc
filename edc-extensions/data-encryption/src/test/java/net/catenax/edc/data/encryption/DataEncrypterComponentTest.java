package net.catenax.edc.data.encryption;

import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.provider.SymmetricKeyProvider;
import net.catenax.edc.data.encryption.strategies.AesEncryptionStrategy;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@SuppressWarnings("FieldCanBeLocal")
public class DataEncrypterComponentTest {

    private final String Key1 = "8y/B?E(H+MbQeThVmYq3t6w9z$C&F)J@";
    private final String Key2 = "dRgUkXp2s5v8y/A?D(G+KbPeShVmYq3t";
    private final String Key3 = "@NcRfUjXn2r5u8x/A%D*G-KaPdSgVkYp";
    private final String Key4 = "6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-";


    private DataEncrypter dataEncrypter;
    private EncryptionStrategy encryptionStrategy;
    private DataEnveloper dataEnveloper;
    private KeyProvider keyProvider;

    // mocks
    private Monitor monitor;
    private Vault vault;

    @BeforeEach
    public void setup() {
        monitor = Mockito.mock(Monitor.class);
        vault = Mockito.mock(Vault.class);

        encryptionStrategy = new AesEncryptionStrategy();
        dataEnveloper = new DataEnveloper();
        keyProvider = new SymmetricKeyProvider(vault, "foo");

        dataEncrypter = new DataEncrypterImpl(monitor, encryptionStrategy, dataEnveloper, keyProvider);
    }

    @Test
    public void testKeyRotation() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Mockito.when(vault.resolveSecret(Mockito.anyString())).thenReturn(
                String.format("%s, %s, %s, %s", Key1, Key2, Key3, Key4));

        final String expectedResult = "hello";
        final byte[] envelopedResult = dataEnveloper.pack(expectedResult);
        final byte[] encryptionKey = Key4.getBytes(StandardCharsets.UTF_8);
        final byte[] encryptedResult = encryptionStrategy.encrypt(envelopedResult, encryptionKey);

        var result = dataEncrypter.decrypt(new String(encryptedResult, StandardCharsets.UTF_8));

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testSuccess() {
        Mockito.when(vault.resolveSecret(Mockito.anyString())).thenReturn(Key1);

        final String expectedResult = "hello world!";

        var encryptedResult = dataEncrypter.encrypt(expectedResult);
        var result = dataEncrypter.decrypt(encryptedResult);

        Assertions.assertEquals(expectedResult, result);
    }

    public void testWarningOnInvalidKey() {

    }
}
