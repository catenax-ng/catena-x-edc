package org.eclipse.tractusx.ssi.extensions.did.web;

import okhttp3.OkHttpClient;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.tractusx.ssi.extensions.did.web.controller.DidWebDocumentController;
import org.eclipse.tractusx.ssi.extensions.did.web.resolver.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.extensions.did.web.settings.DidWebSettings;
import org.eclipse.tractusx.ssi.extensions.did.web.settings.DidWebSettingsFactory;
import org.eclipse.tractusx.ssi.extensions.did.web.settings.DidWebSettingsFactoryImpl;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;

public class SsiDidWebExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI DID Web Extension";

  public static final String API_DID_WEB_CONTEXT = "didweb";
  public static final String SETTING_DID_WEB_DOCUMENT_STORAGE_PATH = "edc.ssi.did.web.document.storage.path";
  public static final String SETTING_DID_WEB_DOCUMENT_STORAGE_TYPE = "edc.ssi.did.web.document.storage.type";

  @Inject private WebService webService;
  @Inject private Vault vault;

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void start() {}

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();

    final DidWebSettingsFactory settingsFactory = new DidWebSettingsFactoryImpl(monitor, context);
    final DidWebSettings settings = settingsFactory.createSettings();

    final DidWebDocumentController didWebDocumentController =
        new DidWebDocumentController(settings, monitor, vault);
    webService.registerResource(API_DID_WEB_CONTEXT, didWebDocumentController);

    final OkHttpClient client = new OkHttpClient();
    final DidDocumentResolver didWebDocumentResolver = new DidWebDocumentResolver(client, monitor);
    final DidDocumentResolverRegistry didDocumentResolverRegistry =
        context.getService(DidDocumentResolverRegistry.class);
    didDocumentResolverRegistry.register(didWebDocumentResolver);
  }
}
