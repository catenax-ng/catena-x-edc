package org.eclipse.tractusx.ssi.extensions.agent.embedded.setting;

import lombok.Value;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;

@Value
public class SsiAgentSettings {
  String verifiablePresentationSigningMethod;
  String walletIdentifier;
  Did didConnector;
  String verifiablePresentationSigningKeyAlias;
}
