package org.eclipse.tractusx.ssi.agent.embedded.proof;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkedDataTransformerTest {

  private LinkedDataTransformer linkedDataTransformer = new LinkedDataTransformer();

  @Test
  @SneakyThrows
  public void test() {
    final VerifiableCredential credential =
        VerifiableCredential.builder()
            .id(URI.create("did:test:id"))
            .types(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .proof(null)
            .credentialStatus(null)
            .build();

    var result = linkedDataTransformer.transform(credential);

    System.out.println(result.getValue());
  }

  @Test
  public void testDidEquals() {
    Did did1 = new Did(new DidMethod("test"), new DidMethodIdentifier("myKey"));
    Did did2 = new Did(new DidMethod("test"), new DidMethodIdentifier("myKey"));

    Assertions.assertEquals(did1, did2);
  }
}
