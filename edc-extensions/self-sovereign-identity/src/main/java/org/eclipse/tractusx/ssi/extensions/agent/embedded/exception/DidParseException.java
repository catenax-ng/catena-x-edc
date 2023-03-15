package org.eclipse.tractusx.ssi.extensions.agent.embedded.exception;

public class DidParseException extends SsiException {
  public DidParseException(String message) {
    super(message);
  }

  public DidParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
