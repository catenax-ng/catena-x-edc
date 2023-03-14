package org.eclipse.tractusx.ssi.extensions.core;

import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedJwtPresentationFactoryImpl;
import org.eclipse.tractusx.ssi.extensions.core.iam.SsiIdentityService;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdValidatorImpl;
import org.eclipse.tractusx.ssi.extensions.core.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.extensions.core.jwt.SignedJwtValidator;
import org.eclipse.tractusx.ssi.extensions.core.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.extensions.core.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.extensions.core.resolver.key.SigningKeyResolver;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettingsFactory;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettingsFactoryImpl;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWalletRegistry;

@Requires({
  Vault.class,
  VerifiableCredentialWalletRegistry.class,
  DidDocumentResolverRegistry.class
})
@Provides({IdentityService.class})
public class SsiIdentityServiceExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Identity Service Extension";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void start() {
    // TODO Check whether configured wallet was registered during initialize phase
    // TODO Check whether verifiable presentation signing key is supported / valid
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();
    final Vault vault = context.getService(Vault.class);
    final SsiSettingsFactory settingsFactory = new SsiSettingsFactoryImpl(monitor, context);
    final SsiSettings settings = settingsFactory.createSettings();

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

    final IdentityService identityService =
        new SsiIdentityService(
            serializedJwtPresentationFactory,
            credentialWallet,
            jsonLdSerializer,
            jwtVerifier,
            jwtValidator,
            linkedDataProofValidation,
            jsonLdValidator);

    context.registerService(IdentityService.class, identityService);
  }
}
