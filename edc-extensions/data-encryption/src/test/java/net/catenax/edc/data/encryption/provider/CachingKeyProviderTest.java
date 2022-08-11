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
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CachingKeyProviderTest {

  private CachingKeyProvider cachingKeyProvider;

  // mocks
  private KeyProvider decoratedProvider;
  private Duration cacheExpiration;
  private Clock clock;

  @BeforeEach
  void setup() {
    decoratedProvider = Mockito.mock(KeyProvider.class);
    cacheExpiration = Duration.ofSeconds(2);
    clock = Mockito.mock(Clock.class);

    cachingKeyProvider = new CachingKeyProvider(decoratedProvider, cacheExpiration, clock);

    Mockito.when(decoratedProvider.getEncryptionKey()).thenReturn(new byte[] {1, 2, 3});
    Mockito.when(decoratedProvider.getDecryptionKeySet())
        .thenAnswer((i) -> Stream.of(new byte[] {4, 5, 6}));
  }

  @Test
  void testCaching() {

    Mockito.when(clock.instant()).thenAnswer((i) -> Instant.now());

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    Mockito.verify(decoratedProvider, Mockito.times(1)).getDecryptionKeySet();
    Mockito.verify(decoratedProvider, Mockito.times(1)).getEncryptionKey();
  }

  @Test
  void testCacheUpdate() {

    Mockito.when(clock.instant()).thenAnswer((i) -> Instant.now());

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    Mockito.when(clock.instant())
        .thenAnswer((i) -> Instant.now().plus(cacheExpiration.plusSeconds(1)));

    cachingKeyProvider.getDecryptionKeySet();
    cachingKeyProvider.getEncryptionKey();

    Mockito.verify(decoratedProvider, Mockito.times(2)).getDecryptionKeySet();
    Mockito.verify(decoratedProvider, Mockito.times(2)).getEncryptionKey();
  }
}
