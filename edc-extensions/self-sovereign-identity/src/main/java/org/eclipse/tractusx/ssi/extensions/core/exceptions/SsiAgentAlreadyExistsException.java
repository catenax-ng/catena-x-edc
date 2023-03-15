package org.eclipse.tractusx.ssi.extensions.core.exceptions;

import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiException;

public class SsiAgentAlreadyExistsException extends SsiException {

  public SsiAgentAlreadyExistsException(String walletIdentifier) {
    super(String.format("Agent with identifier %s is already registered", walletIdentifier));
  }
}
