package net.catenax.edc.data.encryption;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.transfer.dataplane.spi.security.DataEncrypter;

import java.util.ArrayList;
import java.util.List;

@Provides({DataEncrypter.class})
@Requires({Vault.class})
public class DataEncryptionExtension implements ServiceExtension {

    @EdcSetting
    public static final String ENCRYPTION_KEY_SET = "edc.data.encryption.keys";

    @EdcSetting
    public static final String ENCRYPTION_STRATEGY = "edc.data.encryption.strategy";

    @Override
    public String name() {
        return "Data Encryption Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        final Vault vault = context.getService(Vault.class);
        final String keySetAlias = context.getSetting(ENCRYPTION_KEY_SET, null);
        if (keySetAlias == null) {
            throw new EdcException("TODO");
        }
        final List<String> keys = getKeys(vault, keySetAlias);

        final String strategyName = context.getSetting(ENCRYPTION_STRATEGY, AesEncryptionStrategy.AES);
        final EncryptionStrategy strategy = getStrategy(strategyName);
        final DataEnveloper enveloper = new DataEnveloper();
        final DataEncrypter dataEncrypter = new DataEncrypterImpl(monitor, strategy, enveloper, keys);

        context.registerService(DataEncrypter.class, dataEncrypter);
    }

    // TODO GET VAULT KEYS EVERY 5 MINUTES
    // TODO MAKE CACHING CONFIGURABLE
    private List<String> getKeys(Vault vault, String keySetAlias) {
        final String serializedKeys = vault.resolveSecret(keySetAlias);
        if (serializedKeys == null) {
            throw new EdcException("TODO");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(serializedKeys, new TypeReference<ArrayList<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private EncryptionStrategy getStrategy(String strategyName) {
        if (AesEncryptionStrategy.AES.equals(strategyName)) {
            return new AesEncryptionStrategy();
        }

        final String msg = String.format("Unsupported encryption strategy. Supported strategies are '%s'.",
                AesEncryptionStrategy.AES);
        throw new EdcException(msg);
    }
}
