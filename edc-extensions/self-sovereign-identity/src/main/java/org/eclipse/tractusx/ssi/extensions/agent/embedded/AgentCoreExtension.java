package org.eclipse.tractusx.ssi.extensions.agent.embedded;

import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.did.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.key.SigningMethod;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.VerifiableCredentialWalletRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWalletRegistryImpl;

@Provides({DidDocumentResolverRegistry.class, VerifiableCredentialWalletRegistry.class})
public class AgentCoreExtension implements ServiceExtension {

  public static final String EXTENSION_NAME = "SSI Core Extension";
  public static final String SETTINGS_WALLET = "edc.ssi.wallet";
  public static final String SETTING_DID_CONNECTOR = "edc.ssi.did.connector";
  public static final String SETTING_DID_OPERATOR = "edc.ssi.did.operator";
  public static final String SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD =
      "edc.ssi.verifiable.presentation.signing.method";
  public static final String DID_PRIVATE_KEY_ALIAS = "edc.ssi.did.private.key.alias";

  public static final String SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD_DEFAULT =
      SigningMethod.SIGNING_METHOD_ES256;
  public static final String SETTING_DID_DEFAULT = "did:null:connector";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void start() {
    // TODO Check whether configured wallet was registered during initialize phase
    // TODO Check whether verifiable presentation signing key is supported / valid
    // TODO Check if credentials from settings are in the wallet
  }

  public void start2() {
    // TODO Check whether configured wallet was registered during initialize phase
    // TODO Check whether verifiable presentation signing key is supported / valid
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();

    final DidDocumentResolverRegistry documentResolverRegistry =
        new DidDocumentResolverRegistryImpl();

    final VerifiableCredentialWalletRegistry walletRegistry =
        new VerifiableCredentialWalletRegistryImpl();

    context.registerService(DidDocumentResolverRegistry.class, documentResolverRegistry);
    context.registerService(VerifiableCredentialWalletRegistry.class, walletRegistry);
  }
}
