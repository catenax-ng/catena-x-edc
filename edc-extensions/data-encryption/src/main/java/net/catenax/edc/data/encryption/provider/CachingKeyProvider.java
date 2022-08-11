package net.catenax.edc.data.encryption.provider;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class CachingKeyProvider implements KeyProvider {

  private final KeyProvider decoratedProvider;
  private final Duration cacheExpiration;
  private final Clock clock;
  private CachedKeys cachedKeys;

  public CachingKeyProvider(KeyProvider dKeyProvider, Duration cacheExpiration) {
    this(dKeyProvider, cacheExpiration, Clock.systemUTC());
  }

  @Override
  public byte[] getEncryptionKey() {
    checkCache();
    return cachedKeys.getEncryptionKey();
  }

  @Override
  public Stream<byte[]> getDecryptionKeySet() {
    checkCache();
    return cachedKeys.getDecryptionKeys().stream();
  }

  private void checkCache() {

    if (cachedKeys == null || cachedKeys.expiration.isBefore(clock.instant())) {
      byte[] encryptionKey = decoratedProvider.getEncryptionKey();
      List<byte[]> decryptionKeys =
          decoratedProvider.getDecryptionKeySet().collect(Collectors.toList());
      cachedKeys =
          new CachedKeys(encryptionKey, decryptionKeys, clock.instant().plus(cacheExpiration));
    }
  }

  @Value
  private static class CachedKeys {
    byte[] encryptionKey;
    List<byte[]> decryptionKeys;
    @NonNull Instant expiration;
  }
}
