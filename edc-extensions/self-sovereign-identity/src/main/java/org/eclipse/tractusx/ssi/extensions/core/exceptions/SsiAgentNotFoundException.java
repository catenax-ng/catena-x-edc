package org.eclipse.tractusx.ssi.extensions.core.exceptions;

import java.util.List;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiException;

public class SsiAgentNotFoundException extends SsiException {

  public SsiAgentNotFoundException(
      String notFoundWalletIdentifier, List<String> foundWalletIdentifier) {

    super(
        String.format(
            "Agent not found. Requested identifier: %s. Supported identifier: [%s]",
            notFoundWalletIdentifier, String.join(", ", foundWalletIdentifier)));
  }
}
