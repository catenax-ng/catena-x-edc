package org.eclipse.tractusx.ssi.agent.embedded.resolver.did;

import lombok.SneakyThrows;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.resolver.did.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.exception.SsiException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.web.resolver.DidWebDocumentResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DidResolverRegistryTest {

  private DidDocumentResolverRegistry didDocumentResolverRegistry;

  // mocks
  private Monitor monitor;

  @BeforeEach
  public void setUp() {
    monitor = Mockito.mock(Monitor.class);
    didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
  }

  @Test
  @SneakyThrows
  public void registerTestSuccess() {
    // given
    DidDocumentResolver resolver = new DidWebDocumentResolver(null, monitor);
    // when
    didDocumentResolverRegistry.register(resolver);
    var res = didDocumentResolverRegistry.get(resolver.getSupportedMethod());
    // then
    Assertions.assertTrue(res.equals(resolver));
  }

  @Test
  public void registerTestMultipleDidFail() {
    // given
    DidDocumentResolver resolver = new DidWebDocumentResolver(null, monitor);
    String expectedText = "Resolver for method 'web' is already registered";
    // when
    didDocumentResolverRegistry.register(resolver);
    SsiException exception =
        Assertions.assertThrows(
            SsiException.class, () -> didDocumentResolverRegistry.register(resolver));
    // then
    Assertions.assertTrue(exception.getMessage().contains(expectedText));
  }

  @Test
  @SneakyThrows
  public void unregisterTestSuccess() {
    // given
    DidDocumentResolver resolver = new DidWebDocumentResolver(null, monitor);
    String expectedText = "Resolver for method 'web' not registered";
    // when
    didDocumentResolverRegistry.register(resolver);
    didDocumentResolverRegistry.unregister(resolver);
    SsiException exception =
        Assertions.assertThrows(
            SsiException.class, () -> didDocumentResolverRegistry.unregister(resolver));
    didDocumentResolverRegistry.register(resolver);
    var res = didDocumentResolverRegistry.get(resolver.getSupportedMethod());
    // then
    Assertions.assertTrue(exception.getMessage().contains(expectedText));
    Assertions.assertTrue(res.equals(resolver));
  }

  @Test
  public void unregisterTestMultipleDidFail() {
    // given
    DidDocumentResolver resolver = new DidWebDocumentResolver(null, monitor);
    String expectedText = "Resolver for method 'web' not registered";
    // when
    SsiException exception =
        Assertions.assertThrows(
            SsiException.class, () -> didDocumentResolverRegistry.unregister(resolver));
    // then
    Assertions.assertTrue(exception.getMessage().contains(expectedText));
  }
}
