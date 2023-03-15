package org.eclipse.tractusx.ssi.extensions.agent.embedded.spi;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;

public interface VerifiableCredentialWalletRegistry {

  VerifiableCredentialWallet get(String walletIdentifier);

  void register(VerifiableCredentialWallet wallet);
}
