package org.eclipse.tractusx.ssi.extensions.agent.embedded.exception;

public class SigningKeyResolvingException extends SsiException {
  public SigningKeyResolvingException(String message) {
    super(message);
  }

  public SigningKeyResolvingException(String message, Throwable cause) {
    super(message, cause);
  }
}
