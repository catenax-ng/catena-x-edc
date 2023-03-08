package org.eclipse.tractusx.ssi.extensions.did.web.settings;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.SsiCoreExtension;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiSettingException;
import org.eclipse.tractusx.ssi.extensions.did.web.SsiDidWebExtension;
import org.eclipse.tractusx.ssi.extensions.did.web.exception.DidWebException;

public class DidWebSettingsFactoryImpl implements DidWebSettingsFactory{

  private static final String EXCEPTION_MANDATORY_SETTINGS_MISSING =
          "SSI DID Web Settings: Configuration of %s is mandatory";
  private static final String WARNING_NOT_FOUND_IN_VAULT =
          "SSI DID Web Settings: No vault entry found for: %s"; // TODO split for operator and connector

  private final Monitor monitor;
  private final ServiceExtensionContext context;


  public DidWebSettingsFactoryImpl(Monitor monitor, ServiceExtensionContext context) {
    this.monitor = monitor;
    this.context = context;
  }

  @Override
  public DidWebSettings createSettings() {

    String didDocumentAlias =
            context.getSetting(
                    SsiDidWebExtension.SETTING_DID_DOCUMENT_VAULT_ALAS, null);
    if (didDocumentAlias == null) {
      throw new DidWebException(
              String.format(
                      EXCEPTION_MANDATORY_SETTINGS_MISSING,
                      SsiDidWebExtension.SETTING_DID_DOCUMENT_VAULT_ALAS));
    }

    return new DidWebSettings(didDocumentAlias);
  }
}
