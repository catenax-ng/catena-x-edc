package net.catenax.edc.data.encryption;

import java.time.Duration;
import lombok.NonNull;
import lombok.Value;

@Value
public class DataEncryptionConfiguration {
  @NonNull String encryptionStrategy;
  @NonNull String keySetAlias;
  boolean cachingEnabled;
  Duration cachingDuration;
}
