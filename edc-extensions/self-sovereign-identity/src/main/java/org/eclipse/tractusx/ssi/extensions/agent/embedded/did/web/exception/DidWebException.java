package org.eclipse.tractusx.ssi.extensions.agent.embedded.did.web.exception;

public class DidWebException extends RuntimeException {

  public DidWebException() {}

  public DidWebException(String message) {
    super(message);
  }

  public DidWebException(String message, Throwable cause) {
    super(message, cause);
  }

  public DidWebException(Throwable cause) {
    super(cause);
  }

  public DidWebException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
