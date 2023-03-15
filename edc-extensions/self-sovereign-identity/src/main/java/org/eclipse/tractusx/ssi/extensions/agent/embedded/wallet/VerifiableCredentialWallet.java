package org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet;

import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

public interface VerifiableCredentialWallet {
  String getIdentifier();

  VerifiableCredential getMembershipCredential();

  VerifiableCredential getCredential(String credentialType);
}
