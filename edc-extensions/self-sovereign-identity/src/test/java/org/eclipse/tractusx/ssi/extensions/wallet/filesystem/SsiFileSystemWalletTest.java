package org.eclipse.tractusx.ssi.extensions.wallet.filesystem;

import java.net.URL;
import java.nio.file.Path;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.extensions.wallet.fileSystem.SsiFileSystemWallet;
import org.eclipse.tractusx.ssi.test.utils.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class SsiFileSystemWalletTest {

  private SsiFileSystemWallet wallet;

  private LinkedDataProofValidation linkedDataProofValidation;

  // fake
  private TestIdentity testIdentity;
  private TestDidDocumentResolver testDidDocumentResolver;

  // mocks
  private Monitor monitor;

  @BeforeEach
  public void setup() {

    testIdentity = TestIdentityFactory.newIdentity();
    testDidDocumentResolver = new TestDidDocumentResolver();
    testDidDocumentResolver.register(testIdentity);

    monitor = Mockito.mock(Monitor.class);

    linkedDataProofValidation =
        LinkedDataProofValidation.create(testDidDocumentResolver.withRegistry(), monitor);

    final URL resource =
        getClass().getClassLoader().getResource("core/wallet/membership-credential.json");
    if (resource == null) {
      throw new RuntimeException("Resource not found: webdid.json");
    }
    final Path path = Path.of(resource.getPath());

    wallet = new SsiFileSystemWallet(path);
  }

  /**
   * TODO Test fails, discuss if this class is needed @Test @SneakyThrows public void
   * proofGenerator() { KeyPair keyPair = getKeyPair(); byte[] privateKey =
   * keyPair.getPrivate().getEncoded(); byte[] publicKey = keyPair.getPublic().getEncoded();
   *
   * <p>Did verificationMethod = new Did(new DidMethod("test"), new DidMethodIdentifier("myKey"));
   * var credential = wallet.getMembershipCredential();
   *
   * <p>didDocumentResolver.registerVerificationMethod( TestDidDocumentResolver.DID_TEST_OPERATOR,
   * verificationMethod, publicKey);
   *
   * <p>final Ed25519Proof proof = linkedDataProofValidation.createProof(credential,
   * verificationMethod, privateKey);
   *
   * <p>System.out.println(proof); } @Test public void test() { var credential =
   * wallet.getMembershipCredential(); System.out.println(credential); } @SneakyThrows private
   * KeyPair getKeyPair(){ // Add BC as Provider Security.addProvider(new BouncyCastleProvider());
   * // Generate Keypair ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
   * KeyPairGenerator keyPairGenerator = null; keyPairGenerator =
   * KeyPairGenerator.getInstance("ECDSA", "BC"); keyPairGenerator.initialize(ecGenSpec, new
   * SecureRandom());
   *
   * <p>KeyPair pair = keyPairGenerator.generateKeyPair();
   *
   * <p>ECPrivateKey privateKey = (ECPrivateKey) pair.getPrivate(); ECPublicKey publicKey =
   * (ECPublicKey) pair.getPublic();
   *
   * <p>return pair; } @SneakyThrows private byte[] loadKey(String name) { try (final InputStream
   * stream = getClass().getClassLoader().getResourceAsStream(name)) { PemReader reader = new
   * PemReader(new InputStreamReader(stream)); return reader.readPemObject().getContent(); } }
   */
}
