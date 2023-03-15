package org.eclipse.tractusx.ssi.extensions.core;

import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.agent.SsiAgentRegistryImpl;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgentRegistry;

@Provides({SsiAgentRegistry.class})
public class SsiCoreExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Core Extension";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final SsiAgentRegistry ssiAgentRegistry = new SsiAgentRegistryImpl();
    context.registerService(SsiAgentRegistry.class, ssiAgentRegistry);
  }
}
