package net.catenax.edc.oauth2;

import java.net.URI;
import lombok.NonNull;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.iam.oauth2.spi.Oauth2JwtDecoratorRegistry;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.jwt.TokenGenerationService;
import org.eclipse.dataspaceconnector.spi.jwt.TokenValidationService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

@Provides(IdentityService.class)
@Requires({
  OkHttpClient.class,
  Oauth2JwtDecoratorRegistry.class,
  TokenGenerationService.class,
  TokenValidationService.class
})
public class OAuth2Extension implements ServiceExtension {

  @EdcSetting private static final String TOKEN_URL = "edc.oauth.token.url";

  @Inject @Setter private OkHttpClient okHttpClient;

  @Inject @Setter private Oauth2JwtDecoratorRegistry jwtDecoratorRegistry;

  @Inject @Setter private TokenGenerationService tokenGenerationService;

  @Inject @Setter private TokenValidationService tokenValidationService;

  @Override
  public void initialize(@NonNull final ServiceExtensionContext serviceExtensionContext) {
    final URI tokenUrl = URI.create(serviceExtensionContext.getConfig().getString(TOKEN_URL));

    final OAuth2IdentityService oAuth2IdentityService =
        new OAuth2IdentityService(
            tokenUrl,
            okHttpClient,
            serviceExtensionContext.getTypeManager(),
            jwtDecoratorRegistry,
            tokenGenerationService,
            tokenValidationService);

    serviceExtensionContext.registerService(IdentityService.class, oAuth2IdentityService);
  }
}
