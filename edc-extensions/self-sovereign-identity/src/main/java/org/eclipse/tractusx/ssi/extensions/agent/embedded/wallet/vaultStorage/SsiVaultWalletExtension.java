package org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.vaultStorage;

import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.VerifiableCredentialWalletRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;

@Requires({Vault.class, VerifiableCredentialWalletRegistry.class})
public class SsiVaultWalletExtension implements ServiceExtension {

  public static final String EXTENSION_NAME = "Vault Wallet Extension";

  public static final String SETTINGS_WALLET_VAULT_ALIAS = "edc.ssi.wallet.vault.credential.alias";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Vault vault = context.getService(Vault.class);

    final String vaultAlias = context.getSetting(SETTINGS_WALLET_VAULT_ALIAS, null);
    if (vaultAlias == null) {
      throw new EdcException("Mandatory setting not provided: " + SETTINGS_WALLET_VAULT_ALIAS);
    }

    final VerifiableCredentialWallet wallet = new SsiVaultStorageWallet(vault, vaultAlias);

    VerifiableCredentialWalletRegistry walletRegistry =
        context.getService(VerifiableCredentialWalletRegistry.class);
    walletRegistry.register(wallet);
  }
}
