package net.catenax.edc.data.encryption.strategies;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class EncryptionStrategyTest {

  private final byte[] key = "This is a super safe key".getBytes(StandardCharsets.UTF_8);

  @ParameterizedTest
  @ArgumentsSource(StrategyArgumentsProvider.class)
  public void testSuccess(EncryptionStrategy strategy)
      throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException,
          NoSuchAlgorithmException, InvalidKeyException {

    final String expected = "I will be encrypted";
    final byte[] encryptedResult = strategy.encrypt(expected.getBytes(StandardCharsets.UTF_8), key);
    final String result = new String(strategy.decrypt(encryptedResult, key));

    Assertions.assertEquals(expected, result);
  }

  static class StrategyArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(Arguments.of(new AesEncryptionStrategy()));
    }
  }
}
