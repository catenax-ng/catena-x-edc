package org.eclipse.tractusx.ssi.extensions.wallet.vaultStorage;

import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWallet;

public class SsiVaultStorageWallet implements VerifiableCredentialWallet {

  private final String Identifier = "VaultStorage";

  private final Vault vault;
  private final SsiSettings settings;

  public SsiVaultStorageWallet(Vault vault, SsiSettings settings) {
    this.vault = vault;
    this.settings = settings;
  }

  @Override
  public String getIdentifier() {
    return Identifier;
  }

  @Override
  public VerifiableCredential getMembershipCredential() {
    String membershipVc = vault.resolveSecret(settings.getMembershipVerifiableCredentialAlias());

    var dtCredential =
        com.danubetech.verifiablecredentials.VerifiableCredential.fromJson(membershipVc);
    return DanubTechMapper.map(dtCredential);
  }

  @Override
  public VerifiableCredential getCredential(String credentialType) {

    return null;
  }
}
