package org.eclipse.tractusx.ssi.iam;

import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.tractusx.ssi.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.credentials.SerializedJwtPresentationFactoryImpl;
import org.eclipse.tractusx.ssi.jwt.JwtUtils;
import org.eclipse.tractusx.ssi.util.KeyResourceLoader;
import org.eclipse.tractusx.ssi.util.TestDidHandler;
import org.eclipse.tractusx.ssi.util.VerifiableCredentialStoreFake;
import org.eclipse.tractusx.ssi.resolver.DidPublicKeyResolverHandler;
import org.eclipse.tractusx.ssi.resolver.DidPublicKeyResolverImpl;
import org.eclipse.tractusx.ssi.setting.SsiSettings;
import org.eclipse.tractusx.ssi.verification.VerifiableCredentialVerificationImpl;
import org.eclipse.tractusx.ssi.verification.VerifiablePresentationVerificationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SsiIdentityServiceComponentTest {

    private static final String AUDIENCE = "http://localhost";

    private SsiIdentityService ssiIdentityService;

    // fakes
    private VerifiableCredentialStoreFake credentialStore;

    @BeforeEach
    public void setup() {
        final byte[] privateKey = KeyResourceLoader.readPrivateKey();
        final byte[] publicKey = KeyResourceLoader.readPublicKey();
        final SsiSettings settings = new SsiSettings(TestDidHandler.DID_TEST_OPERATOR, TestDidHandler.DID_TEST_OPERATOR, privateKey);
        final DidPublicKeyResolverHandler publicKeyHandler = new TestDidHandler();
        final DidPublicKeyResolverImpl publicKeyResolver = new DidPublicKeyResolverImpl();
        publicKeyResolver.registerHandler(publicKeyHandler);
        final JwtUtils jwtUtils = new JwtUtils();

        final SerializedJwtPresentationFactory serializedJwtPresentationFactory = new SerializedJwtPresentationFactoryImpl(settings, jwtUtils);
        credentialStore = new VerifiableCredentialStoreFake(settings);
        ssiIdentityService = new SsiIdentityService(serializedJwtPresentationFactory, credentialStore,
                VerifiableCredentialVerificationImpl.withAllHandlers(settings),
                VerifiablePresentationVerificationImpl.withAllHandlers(publicKeyResolver));
    }

    @Test
    public void test() {

        // prepare
        credentialStore.prepareMembershipCredential();

        final TokenParameters tokenParameters = TokenParameters.Builder.newInstance().audience(AUDIENCE).build();
        final Result<TokenRepresentation> tokenRepresentationResult = ssiIdentityService.obtainClientCredentials(tokenParameters);
        final TokenRepresentation tokenRepresentation = tokenRepresentationResult.getContent();

        final Result<ClaimToken> claimTokenResult = ssiIdentityService.verifyJwtToken(tokenRepresentation, AUDIENCE);
        final ClaimToken claimToken = claimTokenResult.getContent();

        for (var keyValue : claimToken.getClaims().entrySet()) {
            System.out.printf("Key: %s, Value: %s%n", keyValue.getKey(), keyValue.getValue());
        }
    }
}