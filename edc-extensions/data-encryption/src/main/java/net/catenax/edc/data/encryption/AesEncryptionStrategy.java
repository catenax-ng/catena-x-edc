package net.catenax.edc.data.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptionStrategy implements EncryptionStrategy {

    public static final String AES = "AES";

    @Override
    public byte[] encrypt(byte[] value, byte[] key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(AES, new BouncyCastleProvider());
        final SecretKeySpec keySpec = getKeySpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(value);
        return Base64.encode(encrypted);
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(AES);
        final SecretKeySpec keySpec = getKeySpec(key);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.decode(data);
        return cipher.doFinal(decodedBytes);
    }

    private SecretKeySpec getKeySpec(byte[] key) {
        return new SecretKeySpec(key, AES);
    }
}
