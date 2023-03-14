package org.eclipse.tractusx.ssi.extensions.wallet.vaultStorage;

import lombok.SneakyThrows;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.test.utils.TestCredentialFactory;
import org.eclipse.tractusx.ssi.test.utils.TestIdentity;
import org.eclipse.tractusx.ssi.test.utils.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SsiVaultStorageWalletTest {

  private SsiVaultStorageWallet ssiVaultStorageWallet;

  private SsiSettings ssiSettings;
  private Vault vault;

  @BeforeEach
  public void setUp() {
    ssiSettings = Mockito.mock(SsiSettings.class);
    vault = Mockito.mock(Vault.class);
    ssiVaultStorageWallet = new SsiVaultStorageWallet(vault, ssiSettings);
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
    Mockito.when(ssiSettings.getMembershipVerifiableCredentialAlias()).thenReturn(vaultSecretAlias);
    Mockito.when(vault.resolveSecret(vaultSecretAlias)).thenReturn(serializedVerifiableCredential);

    // when
    VerifiableCredential result = ssiVaultStorageWallet.getMembershipCredential();

    System.out.println(verifiableCredential);
    System.out.println(result);

    // then
    Assertions.assertEquals(verifiableCredential, result);
  }
}
