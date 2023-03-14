package org.eclipse.tractusx.ssi.extensions.wallet.vaultStorage;

import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettingsFactory;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettingsFactoryImpl;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWalletRegistry;

@Requires({Vault.class, VerifiableCredentialWalletRegistry.class})
public class SsiVaultWalletExtension implements ServiceExtension {

  public static final String EXTENSION_NAME = "Vault Wallet Extension";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();
    final Vault vault = context.getService(Vault.class);
    final SsiSettingsFactory settingsFactory = new SsiSettingsFactoryImpl(monitor, context);
    final SsiSettings settings = settingsFactory.createSettings();
    final VerifiableCredentialWallet wallet = new SsiVaultStorageWallet(vault, settings);

    VerifiableCredentialWalletRegistry walletRegistry =
        context.getService(VerifiableCredentialWalletRegistry.class);
    walletRegistry.register(wallet);
  }
}
