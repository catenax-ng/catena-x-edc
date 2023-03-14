package org.eclipse.tractusx.ssi.extensions.core.jwt;

import static org.eclipse.tractusx.ssi.extensions.core.resolver.key.SigningMethod.SIGNING_METHOD_ES256;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiSettingException;
import org.eclipse.tractusx.ssi.extensions.core.resolver.key.SigningKeyResolver;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtFactory {

  public static final List<String> SUPPORTED_SIGNING_METHODS = List.of(SIGNING_METHOD_ES256);

  private final SsiSettings settings;
  private final SigningKeyResolver signingKeyResolver;

  public SignedJwtFactory(SsiSettings settings, SigningKeyResolver signingKeyResolver) {
    this.settings = Objects.requireNonNull(settings);
    this.signingKeyResolver = Objects.requireNonNull(signingKeyResolver);
  }

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer. Although
   * all private key types are possible, in the context of Distributed Identity using an Elliptic
   * Curve key ({@code P-256}) is advisable.
   *
   * @param audience the value of the token audience claim, e.g. the IDS Webhook address.
   * @return a {@code SignedJWT} that is signed with the private key and contains all claims listed.
   */
  public SignedJWT create(
      String audience, SerializedVerifiablePresentation serializedPresentation) {

    final String issuer = settings.getDidConnector().toString();
    final String subject = settings.getDidConnector().toString();

    var claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .audience(audience)
            .claim("vp", serializedPresentation.getJson())
            .expirationTime(new Date(new Date().getTime() + 60 * 1000))
            .jwtID(UUID.randomUUID().toString())
            .build();

    if (SIGNING_METHOD_ES256.equalsIgnoreCase(settings.getVerifiablePresentationSigningMethod())) {
      final PrivateKey signingKey = signingKeyResolver.getSigningKey(SIGNING_METHOD_ES256);
      return createSignedES256Jwt((ECPrivateKey) signingKey, claimsSet);
    } else {
      throw new SsiSettingException(
          "Unsupported signing method " + settings.getVerifiablePresentationSigningMethod());
    }
  }

  @NotNull
  private static SignedJWT createSignedES256Jwt(ECPrivateKey privateKey, JWTClaimsSet claimsSet) {
    ECDSASigner signer = null;
    try {
      signer = new ECDSASigner(privateKey);
      if (!signer.supportedJWSAlgorithms().contains(JWSAlgorithm.ES256)) {
        throw new RuntimeException("Invalid Signing Algorithm");
      }
      var algorithm = JWSAlgorithm.ES256;
      var header = new JWSHeader(algorithm);
      var vc = new SignedJWT(header, claimsSet);
      vc.sign(signer);
      return vc;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
