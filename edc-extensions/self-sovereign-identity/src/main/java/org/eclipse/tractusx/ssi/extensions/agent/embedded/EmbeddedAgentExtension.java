package org.eclipse.tractusx.ssi.extensions.agent.embedded;

import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.credentials.SerializedJwtPresentationFactoryImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.key.SigningKeyResolver;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.key.SigningMethod;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.JsonLdValidatorImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtValidator;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.setting.SsiAgentSettings;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.setting.SsiAgentSettingsFactory;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.VerifiableCredentialWalletRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgentRegistry;

@Requires({SsiAgentRegistry.class})
// This class does not really provide an SSI agent via ServiceExtensionContext,
// this is a workaround to prevent race conditions when the SsiAgentRegistry is being initialized.
// The extensions, that fill the registry, are "providing" an agent, the classes that use the
// registry are "requiring" an agent.
@Provides({SsiAgent.class})
public class EmbeddedAgentExtension implements ServiceExtension {

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
    // TODO Check whether configured wallet was registered during initialize phase
    // TODO Check whether verifiable presentation signing key is supported / valid
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();

    final Vault vault = context.getService(Vault.class);
    final SsiAgentSettingsFactory settingsFactory = new SsiAgentSettingsFactory(monitor, context);
    final SsiAgentSettings settings = settingsFactory.createSettings();

    final VerifiableCredentialWalletRegistry walletRegistry =
        context.getService(VerifiableCredentialWalletRegistry.class);
    final DidDocumentResolverRegistry didDocumentResolverRegistry =
        context.getService(DidDocumentResolverRegistry.class);

    final VerifiableCredentialWallet credentialWallet =
        walletRegistry.get(settings.getWalletIdentifier());
    final JsonLdSerializer jsonLdSerializer = new JsonLdSerializerImpl();
    final SignedJwtVerifier jwtVerifier =
        new SignedJwtVerifier(didDocumentResolverRegistry, monitor);
    final SignedJwtValidator jwtValidator = new SignedJwtValidator(settings);
    final LinkedDataProofValidation linkedDataProofValidation =
        LinkedDataProofValidation.create(didDocumentResolverRegistry, monitor);
    final JsonLdValidatorImpl jsonLdValidator = new JsonLdValidatorImpl();
    final SigningKeyResolver signingKeyResolver = new SigningKeyResolver(vault, settings);
    final SignedJwtFactory signedJwtFactory = new SignedJwtFactory(settings, signingKeyResolver);
    final SerializedJwtPresentationFactory serializedJwtPresentationFactory =
        new SerializedJwtPresentationFactoryImpl(signedJwtFactory, jsonLdSerializer);

    final SsiAgent agent =
        new EmbeddedAgent(
            serializedJwtPresentationFactory,
            jsonLdSerializer,
            jwtVerifier,
            jwtValidator,
            credentialWallet,
            jsonLdValidator,
            linkedDataProofValidation);
    final SsiAgentRegistry agentRegistry = context.getService(SsiAgentRegistry.class);
    agentRegistry.registerAgent(agent);
  }
}
