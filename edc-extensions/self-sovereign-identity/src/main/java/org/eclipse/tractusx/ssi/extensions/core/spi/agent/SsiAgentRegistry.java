package org.eclipse.tractusx.ssi.extensions.core.spi.agent;

public interface SsiAgentRegistry {

  SsiAgent getAgent(String agentIdentifier);

  void registerAgent(SsiAgent agent);
}
