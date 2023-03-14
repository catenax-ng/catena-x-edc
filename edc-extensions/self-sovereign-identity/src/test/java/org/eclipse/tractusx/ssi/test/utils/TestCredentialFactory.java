package org.eclipse.tractusx.ssi.test.utils;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredentialType;

public class TestCredentialFactory {

  public static VerifiableCredential generateCredential(
      TestIdentity issuer, String credentialType) {
    return VerifiableCredential.builder()
        .id(URI.create(TestDidFactory.createRandom() + "#credential"))
        .issuer(issuer.getDid().toUri())
        .issuanceDate(
            Instant.now()
                .truncatedTo(
                    ChronoUnit.SECONDS)) // Truncate to seconds. Otherwise, equals will fail after
        // serialization and deserialization
        .expirationDate(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(3600))
        .types(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL, credentialType))
        .build();
  }
}
