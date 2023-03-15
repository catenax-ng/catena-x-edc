package org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.vaultStorage;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

@RequiredArgsConstructor
public class SsiVaultStorageWallet implements VerifiableCredentialWallet {

  /**
   * Unique identifier of the Wallet. Changing the Identifier of the Wallet is a breaking change and
   * must be documented accordingly.
   */
  private static final String Identifier = "VaultWallet";

  private final Vault vault;
  private final String credentialAlias;

  @Override
  public String getIdentifier() {
    return Identifier;
  }

  @Override
  public VerifiableCredential getMembershipCredential() {
    String membershipVc = vault.resolveSecret(credentialAlias);

    // TODO this is not only one credential per secret
    var dtCredential =
        com.danubetech.verifiablecredentials.VerifiableCredential.fromJson(membershipVc);
    return DanubTechMapper.map(dtCredential);
  }

  @Override
  public VerifiableCredential getCredential(String credentialType) {

    return null;
  }
}
