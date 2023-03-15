package org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiWalletAlreadyExistsException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiWalletNotFoundException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.VerifiableCredentialWalletRegistry;

public class VerifiableCredentialWalletRegistryImpl implements VerifiableCredentialWalletRegistry {

  private final Map<String, VerifiableCredentialWallet> wallets = new HashMap<>();

  @Override
  public VerifiableCredentialWallet get(String walletIdentifier) {

    if (!wallets.containsKey(walletIdentifier))
      throw new SsiWalletNotFoundException(walletIdentifier, new ArrayList<>(wallets.keySet()));

    return wallets.get(walletIdentifier);
  }

  @Override
  public void register(VerifiableCredentialWallet wallet) {
    if (wallets.containsKey(wallet.getIdentifier())) {
      throw new SsiWalletAlreadyExistsException(wallet.getIdentifier());
    }

    wallets.put(wallet.getIdentifier(), wallet);
  }
}
