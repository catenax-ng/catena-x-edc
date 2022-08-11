package net.catenax.edc.data.encryption;

import lombok.NonNull;
import lombok.Value;

import java.time.Duration;

@Value
public class DataEncryptionConfiguration {
    @NonNull String encryptionStrategy;
    @NonNull String keySetAlias;
    boolean cachingEnabled;
    Duration cachingDuration;
}
