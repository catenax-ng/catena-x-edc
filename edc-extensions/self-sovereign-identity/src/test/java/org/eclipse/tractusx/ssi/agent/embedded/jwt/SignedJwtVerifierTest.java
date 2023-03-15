package org.eclipse.tractusx.ssi.agent.embedded.jwt;

import lombok.SneakyThrows;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.agent.embedded.jsonld.DanubCredentialFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.test.utils.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SignedJwtVerifierTest {

  private SignedJwtVerifier signedJwtVerifier;
  private JsonLdSerializer jsonLdSerializer;
  private TestDidDocumentResolver didDocumentResolver;
  private TestIdentity credentialIssuer;

  // mocks
  private Monitor monitor;

  @BeforeEach
  public void setup() {
    monitor = Mockito.mock(Monitor.class);
    credentialIssuer = TestIdentityFactory.newIdentity();

    didDocumentResolver = new TestDidDocumentResolver();
    didDocumentResolver.register(credentialIssuer);

    signedJwtVerifier = new SignedJwtVerifier(didDocumentResolver.withRegistry(), monitor);
    jsonLdSerializer = new JsonLdSerializerImpl();
  }

  @Test
  @SneakyThrows
  public void verifyJwtSuccess() {
    didDocumentResolver.register(credentialIssuer);
    // given

    //        SignedJWT toTest = SignedJwtFactory.createTestJwt(
    //                testIdentity.getDid().toString(),
    //                "",
    //                "someAudience",
    //                getTestPresentation(),
    //                (ECPrivateKey) testIdentity.getKeyPair().getPrivate()
    //        );
    //
    //        // when
    //        boolean verify = signedJwtVerifier.verify(toTest);
    //        // then
    //        assertTrue(verify);
  }

  //    @SneakyThrows
  //    @Test
  //    public void verifTestParseException() {
  //        // given
  //        SignedJWT signedJWTMock = Mockito.mock(SignedJWT.class);
  //        assertNotNull(signedJWTMock);
  //        doThrow(ParseException.class).when(signedJWTMock).getJWTClaimsSet();
  //        String expectedMessage = "JOSEException";
  //        // when
  //        JOSEException exception = Assertions.assertThrows(JOSEException.class,
  //                () -> signedJwtVerifier.verify(signedJWTMock));
  //        // then
  //        Assertions.assertTrue(exception.toString().contains(expectedMessage));
  //    }
  //
  //    @Test
  //    @SneakyThrows
  //    public void verifTestJoseException() {
  //        // given
  //        String exceptionMessage = "com.nimbusds.jose.JOSEException";
  //        KeyPair keyPair = getKeyPair();
  //        List<PublicKey> publicKeys = getPublicKeyList(keyPair.getPublic());
  //        SignedJWT toTest = Mockito.mock(SignedJWT.class);
  //        JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
  //        didMock = Mockito.mock(Did.class);
  //        doReturn(didDocument).when(didDocumentResolverRegistry).resolve(any(Did.class));
  //        doReturn(publicKeys).when(didDocument).getPublicKeys();
  //        doThrow(JOSEException.class).when(toTest).verify(any(ECDSAVerifier.class));
  //        doReturn(claimsSetMock).when(toTest).getJWTClaimsSet();
  //        doReturn("").when(claimsSetMock).getIssuer();
  //        try (MockedStatic<DidParser> didParserMockedStatic =
  // Mockito.mockStatic(DidParser.class)) {
  //            didParserMockedStatic.when(() -> DidParser.parse(any(String.class)))
  //                    .thenReturn(didMock);
  //            // when
  //            JOSEException exception = Assertions.assertThrows(JOSEException.class,
  //                    () -> signedJwtVerifier.verify(toTest));
  //            // then
  //            Assertions.assertTrue(exception.toString().contains(exceptionMessage));
  //        }
  //    }
  //
  //    @SneakyThrows
  //    private KeyPair getKeyPair() {
  //        // Add BC as Provider
  //        Security.addProvider(new BouncyCastleProvider());
  //        // Generate Keypair
  //        ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
  //        KeyPairGenerator keyPairGenerator = null;
  //        keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
  //        keyPairGenerator.initialize(ecGenSpec, new SecureRandom());
  //
  //        KeyPair pair = keyPairGenerator.generateKeyPair();
  //
  //        ECPrivateKey privateKey = (ECPrivateKey) pair.getPrivate();
  //        ECPublicKey publicKey = (ECPublicKey) pair.getPublic();
  //
  //        return pair;
  //    }
  //
  //    @SneakyThrows
  //    private List<PublicKey> getPublicKeyList(java.security.@NotNull PublicKey publicKey) {
  //        List<PublicKey> pks = new ArrayList<>();
  //        byte[] encodedPublicKey = null;
  //        if ("X.509".equals(publicKey.getFormat())) {
  //            encodedPublicKey = publicKey.getEncoded();
  //        }
  //        String mbs = Base58Bitcoin.create(encodedPublicKey).getEncoded();
  //        org.eclipse.tractusx.ssi.spi.did.PublicKey pk = Ed25519VerificationKey2020
  //                .builder()
  //                .publicKeyMultibase(mbs)
  //                .id(new URI("someweburl.com"))
  //                .controller(new URI("somecontrolleruri.com"))
  //                .build();
  //        pks.add(pk);
  //        return pks;
  //    }
  //
  private SerializedVerifiablePresentation getTestPresentation() {
    SerializedVerifiablePresentation presentation;
    com.danubetech.verifiablecredentials.VerifiablePresentation dtVp =
        DanubCredentialFactory.getTestDanubVP();
    VerifiablePresentation cxPresentation = DanubTechMapper.map(dtVp);
    presentation = jsonLdSerializer.serializePresentation(cxPresentation);
    return presentation;
  }
}
