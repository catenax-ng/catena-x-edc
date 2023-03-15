package org.eclipse.tractusx.ssi.extensions.core;

import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.EmbeddedAgent;
import org.eclipse.tractusx.ssi.extensions.core.iam.SsiIdentityService;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgentRegistry;

// This class does not really require an SSI agent via ServiceExtensionContext,
// this is a workaround to prevent race conditions when the SsiAgentRegistry is being initialized.
// The extensions, that fill the registry, are "providing" an agent, the classes that use the
// registry are "requiring" an agent.
@Requires({
  SsiAgent.class,
  SsiAgentRegistry.class,
})
@Provides({IdentityService.class})
public class SsiIdentityServiceExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Identity Service Extension";

  public static final String SETTING_AGENT_IDENTIFIER = "edc.ssi.agent";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();
    final SsiAgentRegistry agentRegistry = context.getService(SsiAgentRegistry.class);

    final SsiAgent agent;
    final String agentIdentifier = context.getSetting(SETTING_AGENT_IDENTIFIER, null);
    if (agentIdentifier != null) {
      agent = agentRegistry.getAgent(agentIdentifier);
    } else {
      monitor.warning(
          String.format(
              "No agent identifier configured in setting '%s'. Using default agent '%s'.",
              SETTING_AGENT_IDENTIFIER, EmbeddedAgent.Identifier));
      agent = agentRegistry.getAgent(EmbeddedAgent.Identifier);
    }

    final IdentityService identityService = new SsiIdentityService(agent);

    context.registerService(IdentityService.class, identityService);
  }
}
