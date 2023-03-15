package org.eclipse.tractusx.ssi.agent.embedded.wallet.vaultStorage;

import lombok.SneakyThrows;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.vaultStorage.SsiVaultStorageWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.test.utils.TestCredentialFactory;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SsiVaultStorageWalletTest {

  private SsiVaultStorageWallet ssiVaultStorageWallet;

  private Vault vault;

  @BeforeEach
  public void setUp() {
    vault = Mockito.mock(Vault.class);
    ssiVaultStorageWallet = new SsiVaultStorageWallet(vault, "foo");
  }

  @SneakyThrows
  @Test
  public void getMembershipCredentialSuccess() {

    // given
    final TestIdentity issuer = TestIdentityFactory.newIdentity();

    final VerifiableCredential verifiableCredential =
        TestCredentialFactory.generateCredential(
            issuer, VerifiableCredentialType.MEMBERSHIP_CREDENTIAL);
    final String serializedVerifiableCredential =
        DanubTechMapper.map(verifiableCredential).toJson();

    final String vaultSecretAlias = "foo";
    Mockito.when(vault.resolveSecret(vaultSecretAlias)).thenReturn(serializedVerifiableCredential);

    // when
    VerifiableCredential result = ssiVaultStorageWallet.getMembershipCredential();

    System.out.println(verifiableCredential);
    System.out.println(result);

    // then
    Assertions.assertEquals(verifiableCredential, result);
  }
}
