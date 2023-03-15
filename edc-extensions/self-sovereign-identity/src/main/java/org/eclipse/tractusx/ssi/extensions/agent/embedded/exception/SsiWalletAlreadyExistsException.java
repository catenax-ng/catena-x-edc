package org.eclipse.tractusx.ssi.extensions.agent.embedded.exception;

public class SsiWalletAlreadyExistsException extends SsiException {

  public SsiWalletAlreadyExistsException(String walletIdentifier) {
    super(String.format("Wallet with identifier %s is already registered", walletIdentifier));
  }
}