/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *
 */

package net.catenax.edc.data.encryption.provider;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.Value;

public class CachingKeyProvider implements KeyProvider {

  @NonNull private final KeyProvider decoratedProvider;
  @NonNull private final Clock clock;
  @NonNull private final Duration cacheExpiration;

  private CachedKeys cachedKeys;

  public CachingKeyProvider(KeyProvider keyProvider, Duration cacheExpiration) {
    this(keyProvider, cacheExpiration, Clock.systemUTC());
  }

  public CachingKeyProvider(KeyProvider keyProvider, Duration cacheExpiration, Clock clock) {

    this.decoratedProvider = keyProvider;
    this.cacheExpiration = cacheExpiration;
    this.clock = clock;
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
