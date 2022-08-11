package net.catenax.edc.data.encryption;

import net.catenax.edc.data.encryption.provider.CachingKeyProvider;
import net.catenax.edc.data.encryption.provider.KeyProvider;
import net.catenax.edc.data.encryption.provider.SymmetricKeyProvider;
import net.catenax.edc.data.encryption.strategies.AesEncryptionStrategy;
import net.catenax.edc.data.encryption.strategies.EncryptionStrategy;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

import java.time.Duration;

@Provides({DataEncrypter.class})
@Requires({Vault.class})
public class DataEncryptionExtension implements ServiceExtension {

    public static final String NAME = "Data Encryption Extension";

    @EdcSetting
    public static final String ENCRYPTION_KEY_SET = "edc.data.encryption.keys";

    @EdcSetting
    public static final String ENCRYPTION_STRATEGY = "edc.data.encryption.strategy";
    public static final String ENCRYPTION_STRATEGY_DEFAULT = AesEncryptionStrategy.AES;

    @EdcSetting
    public static final String CACHING_ENABLED = "edc.data.encryption.caching.enabled";
    public static final boolean CACHING_ENABLED_DEFAULT = false;

    @EdcSetting
    public static final String CACHING_SECONDS = "edc.data.encryption.caching.seconds";
    public static final int CACHING_SECONDS_DEFAULT = 3600;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        final Vault vault = context.getService(Vault.class);
        final DataEncryptionConfiguration configuration = getConfiguration(context);
        final KeyProvider keyProvider = getKeyProvider(vault, configuration);
        final EncryptionStrategy strategy = getStrategy(configuration);
        final DataEnveloper enveloper = new DataEnveloper();
        final DataEncrypter dataEncrypter = new DataEncrypterImpl(monitor, strategy, enveloper, keyProvider);

        context.registerService(DataEncrypter.class, dataEncrypter);
    }

    private DataEncryptionConfiguration getConfiguration(ServiceExtensionContext context) {
        final String keySetAlias = context.getSetting(ENCRYPTION_KEY_SET, null);
        if (keySetAlias == null) {
            throw new EdcException("TODO");
        }

        final String encryptionStrategy = context.getSetting(ENCRYPTION_STRATEGY, ENCRYPTION_STRATEGY_DEFAULT);
        final boolean cachingEnabled = context.getSetting(CACHING_ENABLED, CACHING_ENABLED_DEFAULT);
        final int cachingSeconds = context.getSetting(CACHING_SECONDS, CACHING_SECONDS_DEFAULT);

        return new DataEncryptionConfiguration(encryptionStrategy, keySetAlias, cachingEnabled, Duration.ofSeconds(cachingSeconds));
    }

    private KeyProvider getKeyProvider(Vault vault, DataEncryptionConfiguration configuration) {

        final KeyProvider keyProvider = new SymmetricKeyProvider(vault, configuration.getKeySetAlias());

        return configuration.isCachingEnabled() ?
                new CachingKeyProvider(keyProvider, configuration.getCachingDuration()) :
                keyProvider;
    }

    private EncryptionStrategy getStrategy(DataEncryptionConfiguration configuration) {
        if (AesEncryptionStrategy.AES
                .equalsIgnoreCase(configuration.getEncryptionStrategy())) {
            return new AesEncryptionStrategy();
        }

        final String msg = String.format(NAME + ": Unsupported encryption strategy. Supported strategies are '%s'.",
                AesEncryptionStrategy.AES);
        throw new EdcException(msg);
    }

}
