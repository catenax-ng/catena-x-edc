package org.eclipse.tractusx.ssi.extensions.agent.embedded.web.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.web.settings.DidWebSettings;

@Produces({MediaType.APPLICATION_JSON})
@Path("./well-known/did.json")
public class DidWebDocumentController {

  private final DidWebSettings settings;
  private final Monitor monitor;
  private final Vault vault;

  private String didDocument;

  public DidWebDocumentController(DidWebSettings settings, Monitor monitor, Vault vault) {
    this.settings = settings;
    this.monitor = monitor;
    this.vault = vault;
  }

  @GET
  public String request() {
    return getDidDoc();
  }

  private String getDidDoc() {
    if (this.didDocument != null) {
      return this.didDocument;
    }
    String didDocumentVaultAlias = settings.getDidDocumentAlias();
    this.didDocument = vault.resolveSecret(didDocumentVaultAlias);
    return this.didDocument;
  }
}
