package org.eclipse.tractusx.ssi.extensions.agent.embedded.setting;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.EmbeddedAgentExtension;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.DidParseException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiSettingException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidParser;

public class SsiAgentSettingsFactory {

  private static final String EXCEPTION_NO_VALID_DID =
      "SSI Settings: No valid DID in configured for %s. Was %s";
  private static final String EXCEPTION_SIGNING_METHOD_NOT_SUPPORTED =
      "SSI Settings: Verifiable Presentation Signing Method '%s' from setting '%s' is not supported. Please use supported signing method: %s";
  private static final String EXCEPTION_MANDATORY_SETTINGS_MISSING =
      "SSI Settings: Configuration of %s is mandatory";
  private static final String EXCEPTION_CANNOT_DECODE_PRIVATE_KEY =
      "SSI Settings: No valid private key configured.";
  private static final String WARNING_NO_DID_CONFIGURED =
      "SSI Settings: No DID configured. 22Using (invalid) default DID: %s"; // TODO split for
  // operator
  // and connector

  private final Monitor monitor;
  private final ServiceExtensionContext context;

  public SsiAgentSettingsFactory(Monitor monitor, ServiceExtensionContext context) {
    this.monitor = monitor;
    this.context = context;
  }

  public SsiAgentSettings createSettings() {

    final String didConnectorString =
        context.getSetting(
            EmbeddedAgentExtension.SETTING_DID_CONNECTOR,
            EmbeddedAgentExtension.SETTING_DID_DEFAULT);
    if (didConnectorString.equals(EmbeddedAgentExtension.SETTING_DID_DEFAULT)) {
      monitor.warning(
          String.format(WARNING_NO_DID_CONFIGURED, EmbeddedAgentExtension.SETTING_DID_DEFAULT));
    }

    final Did didConnector;
    try {
      didConnector = DidParser.parse(didConnectorString);
    } catch (DidParseException e) {
      throw new SsiSettingException(
          String.format(
              EXCEPTION_NO_VALID_DID,
              EmbeddedAgentExtension.SETTING_DID_CONNECTOR,
              didConnectorString),
          e);
    }

    final String didOperatorString =
        context.getSetting(
            EmbeddedAgentExtension.SETTING_DID_OPERATOR,
            EmbeddedAgentExtension.SETTING_DID_DEFAULT);
    if (didOperatorString.equals(EmbeddedAgentExtension.SETTING_DID_DEFAULT)) {
      monitor.warning(
          String.format(WARNING_NO_DID_CONFIGURED, EmbeddedAgentExtension.SETTING_DID_DEFAULT));
    }

    String verifiablePresentationSigningKeyAlias =
        context.getSetting(EmbeddedAgentExtension.DID_PRIVATE_KEY_ALIAS, null);
    if (verifiablePresentationSigningKeyAlias == null) {
      throw new SsiSettingException(
          String.format(
              EXCEPTION_MANDATORY_SETTINGS_MISSING, EmbeddedAgentExtension.DID_PRIVATE_KEY_ALIAS));
    }

    final String walletIdentifier =
        context.getSetting(EmbeddedAgentExtension.SETTINGS_WALLET, null);
    if (walletIdentifier == null) {
      throw new SsiSettingException(
          String.format(
              EXCEPTION_MANDATORY_SETTINGS_MISSING, EmbeddedAgentExtension.SETTINGS_WALLET));
    }

    final String verifiablePresentationSigningMethod =
        context.getSetting(
            EmbeddedAgentExtension.SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD,
            EmbeddedAgentExtension.SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD_DEFAULT);
    if (!SignedJwtFactory.SUPPORTED_SIGNING_METHODS.contains(verifiablePresentationSigningMethod)) {
      throw new SsiSettingException(
          String.format(
              EXCEPTION_SIGNING_METHOD_NOT_SUPPORTED,
              verifiablePresentationSigningMethod,
              EmbeddedAgentExtension.SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD,
              String.join(", ", SignedJwtFactory.SUPPORTED_SIGNING_METHODS)));
    }

    return new SsiAgentSettings(
        verifiablePresentationSigningMethod,
        walletIdentifier,
        didConnector,
        verifiablePresentationSigningKeyAlias);
  }
}
