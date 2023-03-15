package org.eclipse.tractusx.ssi.agent.embedded.resolver.key;

import static org.mockito.Mockito.doReturn;

import jakarta.xml.bind.DatatypeConverter;
import java.security.PrivateKey;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SigningKeyResolvingException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.key.SigningKeyResolver;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.setting.SsiAgentSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SigningKeyResolverTest {

  private SigningKeyResolver signingKeyResolver;
  private Vault vaultMock;
  private SsiAgentSettings settingsMock;

  @BeforeEach
  public void setUp() {
    settingsMock = Mockito.mock(SsiAgentSettings.class);
    vaultMock = Mockito.mock(Vault.class);
    signingKeyResolver = new SigningKeyResolver(vaultMock, settingsMock);
  }

  @Test
  public void testGetSigningKeyFullPemSuccess() {
    // given
    // Use of right key needed https://github.com/auth0/java-jwt/issues/270
    String testSigningKey =
        "-----BEGIN PRIVATE KEY-----\n"
            + "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQge7US5V/VZNhFLghqG1zwZKuaC2DQ59xDjBnAguI+NxmhRANCAATyTmY/OMBCRKiGcnwQQMlvKWthOzB4XWZ5xIP6f+VVALv4Q5lcQyoNS3Ea0EibJjfkBAKk35t6gox3zdxI1y7f\n"
            + "-----END PRIVATE KEY-----";
    String signingMethod = "ES256";
    String keyAlias = "testKey";
    doReturn(keyAlias).when(settingsMock).getVerifiablePresentationSigningKeyAlias();
    doReturn(testSigningKey).when(vaultMock).resolveSecret(keyAlias);
    // when
    PrivateKey res = signingKeyResolver.getSigningKey(signingMethod);
    // then
    String keypem =
        "-----BEGIN PRIVATE KEY-----\n"
            + DatatypeConverter.printBase64Binary(res.getEncoded())
            + "\n-----END PRIVATE KEY-----\n";
    Assertions.assertNotNull(res);
    Assertions.assertTrue(keypem.contains(testSigningKey));
  }

  @Test
  public void testGetSigningKeySuccess() {
    // given
    // Use of right key needed https://github.com/auth0/java-jwt/issues/270
    String testSigningKey =
        "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQge7US5V/VZNhFLghqG1zwZKuaC2DQ59xDjBnAguI+NxmhRANCAATyTmY/OMBCRKiGcnwQQMlvKWthOzB4XWZ5xIP6f+VVALv4Q5lcQyoNS3Ea0EibJjfkBAKk35t6gox3zdxI1y7f";
    String signingMethod = "ES256";
    String keyAlias = "testKey";
    doReturn(keyAlias).when(settingsMock).getVerifiablePresentationSigningKeyAlias();
    doReturn(testSigningKey).when(vaultMock).resolveSecret(keyAlias);
    // when
    PrivateKey res = signingKeyResolver.getSigningKey(signingMethod);
    // then
    String keypem = DatatypeConverter.printBase64Binary(res.getEncoded());
    Assertions.assertNotNull(res);
    Assertions.assertTrue(keypem.contains(testSigningKey));
  }

  @Test
  public void testGetSigningKeyNotFound() {
    // given
    // Use of right key needed https://github.com/auth0/java-jwt/issues/270
    String signingMethod = "ES256";
    String keyAlias = "testKey";
    doReturn(keyAlias).when(settingsMock).getVerifiablePresentationSigningKeyAlias();
    doReturn(null).when(vaultMock).resolveSecret(keyAlias);
    // when
    Assertions.assertThrows(
        SigningKeyResolvingException.class, () -> signingKeyResolver.getSigningKey(signingMethod));
  }

  @Test
  public void testGetSigningKeyNotSupported() {
    // given
    // Use of right key needed https://github.com/auth0/java-jwt/issues/270
    String signingMethod = "ES257";
    String expectedMessage = "not supported";
    // when
    SigningKeyResolvingException res =
        Assertions.assertThrows(
            SigningKeyResolvingException.class,
            () -> signingKeyResolver.getSigningKey(signingMethod));
    // then
    Assertions.assertTrue(res.toString().contains(expectedMessage));
  }

  @Test
  public void testGetSigningKeyInvalidKeyStored() {
    // given
    String testSigningKey = "MIGHFLghqG1zwZKu35t6gox3zdxI1y7f";
    String signingMethod = "ES256";
    String keyAlias = "testKey";
    doReturn(keyAlias).when(settingsMock).getVerifiablePresentationSigningKeyAlias();
    doReturn(testSigningKey).when(vaultMock).resolveSecret(keyAlias);
    // when
    Assertions.assertThrows(
        SigningKeyResolvingException.class, () -> signingKeyResolver.getSigningKey(signingMethod));
  }
}
