package org.eclipse.tractusx.ssi.extensions.core.resolver.key;

import io.ipfs.multibase.binary.Base64;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.core.exception.SigningKeyResolvingException;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;

public class SigningKeyResolver {

  private final Vault vault;
  private final SsiSettings settings;

  public SigningKeyResolver(Vault vault, SsiSettings settings) {
    this.vault = vault;
    this.settings = settings;
  }

  public PrivateKey getSigningKey(String signingMethod) {

    if (!signingMethod.equals(SigningMethod.SIGNING_METHOD_ES256)) {
      throw new SigningKeyResolvingException(
          "Signing method not supported. Supported signing methods: ES256");
    }

    final String signingKey =
        vault.resolveSecret(settings.getVerifiablePresentationSigningKeyAlias());

    if (signingKey == null) {
      throw new SigningKeyResolvingException(
          "Signing key not found. Vault Alias: "
              + settings.getVerifiablePresentationSigningKeyAlias());
    }

    String privateKeyPEM =
        signingKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PRIVATE KEY-----", "");

    final byte[] signingKeyBytes = Base64.decodeBase64(privateKeyPEM);

    try {
      final KeyFactory kf = KeyFactory.getInstance("EC");
      return kf.generatePrivate(new PKCS8EncodedKeySpec(signingKeyBytes));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new SigningKeyResolvingException("exception generating private key", e);
    }
  }
}
