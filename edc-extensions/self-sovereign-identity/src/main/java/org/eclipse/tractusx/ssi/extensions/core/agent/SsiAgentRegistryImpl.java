package org.eclipse.tractusx.ssi.extensions.core.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.extensions.core.exceptions.SsiAgentAlreadyExistsException;
import org.eclipse.tractusx.ssi.extensions.core.exceptions.SsiAgentNotFoundException;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.extensions.core.spi.agent.SsiAgentRegistry;

public class SsiAgentRegistryImpl implements SsiAgentRegistry {

  private final List<SsiAgent> agents = new ArrayList<>();

  @Override
  public SsiAgent getAgent(String agentIdentifier) {
    for (SsiAgent agent : agents) {
      if (agent.getIdentifier().equals(agentIdentifier)) {
        return agent;
      }
    }
    final List<String> agentIdentifiers =
        agents.stream().map(SsiAgent::getIdentifier).collect(Collectors.toList());
    throw new SsiAgentNotFoundException(agentIdentifier, agentIdentifiers);
  }

  @Override
  public void registerAgent(SsiAgent agent) {
    if (agents.contains(agent)) {
      throw new SsiAgentAlreadyExistsException(agent.getIdentifier());
    }

    agents.add(agent);
  }
}
