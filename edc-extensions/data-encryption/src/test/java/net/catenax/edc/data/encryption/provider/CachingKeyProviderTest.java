package net.catenax.edc.data.encryption.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class CachingKeyProviderTest {

    private CachingKeyProvider cachingKeyProvider;

    // mocks
    private KeyProvider decoratedProvider;
    private Duration cacheExpiration;
    private Clock clock;

    @BeforeEach
    public void setup() {
        decoratedProvider = Mockito.mock(KeyProvider.class);
        cacheExpiration = Duration.ofSeconds(2);
        clock = Mockito.mock(Clock.class);

        cachingKeyProvider = new CachingKeyProvider(decoratedProvider, cacheExpiration, clock);

        Mockito.when(decoratedProvider.getEncryptionKey()).thenReturn(new byte[] { 1, 2, 3 });
        Mockito.when(decoratedProvider.getDecryptionKeySet()).thenAnswer((i) -> Stream.of(new byte[] { 4, 5, 6 }));
    }

    @Test
    public void testCaching() {
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
    public void testCacheUpdate() {

        Mockito.when(clock.instant()).thenAnswer((i) -> Instant.now());

        var test1 = clock.instant();

        Mockito.when(clock.instant()).thenAnswer((i) -> Instant.now().plus(cacheExpiration.plusSeconds(1)));

        var test2 = clock.instant();

        // TODO find problem

        cachingKeyProvider.getDecryptionKeySet();
        cachingKeyProvider.getEncryptionKey();

        cachingKeyProvider.getDecryptionKeySet();
        cachingKeyProvider.getEncryptionKey();

        Mockito.when(clock.instant()).thenAnswer((i) -> Instant.now().plus(cacheExpiration.plusSeconds(1)));

        cachingKeyProvider.getDecryptionKeySet();
        cachingKeyProvider.getEncryptionKey();

        Mockito.verify(decoratedProvider, Mockito.times(2)).getDecryptionKeySet();
        Mockito.verify(decoratedProvider, Mockito.times(2)).getEncryptionKey();
    }
}
