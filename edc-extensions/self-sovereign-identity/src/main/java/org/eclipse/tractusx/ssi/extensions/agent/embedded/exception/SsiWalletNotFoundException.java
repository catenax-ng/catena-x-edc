package org.eclipse.tractusx.ssi.extensions.agent.embedded.exception;

import java.util.List;

public class SsiWalletNotFoundException extends SsiException {

  public SsiWalletNotFoundException(
      String notFoundWalletIdentifier, List<String> foundWalletIdentifier) {

    super(
        String.format(
            "Wallet not found. Requested identifier: %s. Supported identifier: %s",
            notFoundWalletIdentifier, String.join(", ", foundWalletIdentifier)));
  }
}
