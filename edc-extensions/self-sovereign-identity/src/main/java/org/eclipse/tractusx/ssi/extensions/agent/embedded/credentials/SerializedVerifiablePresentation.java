package org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials;

public class SerializedVerifiablePresentation {

  private final String json;

  public SerializedVerifiablePresentation(String json) {
    this.json = json;
  }

  public String getJson() {
    return json;
  }
}
