package org.eclipse.tractusx.ssi.extensions.core.jsonLd;

import com.danubetech.verifiablecredentials.CredentialSubject;
import info.weboftrust.ldsignatures.LdProof;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.PackagePrivate;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiException;
import org.eclipse.tractusx.ssi.spi.verifiable.Ed25519Proof;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.spi.verifiable.presentation.VerifiablePresentation;

@PackagePrivate
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DanubTechMapper {

  @NonNull
  public static com.danubetech.verifiablecredentials.VerifiablePresentation map(
      VerifiablePresentation presentation) {

    final List<com.danubetech.verifiablecredentials.VerifiableCredential> dtCredentials =
        presentation.getVerifiableCredentials().stream()
            .map(DanubTechMapper::map)
            .collect(Collectors.toList());

    // TODO Throw Exception if more or less than one

    if (!presentation.getTypes().get(0).equals("VerifiablePresentation")) {
      throw new SsiException("Type: VerifiablePresentation missing");
    }
    // VerifiablePresentation Type is automatically added in Builder
    List<String> types = new ArrayList<>(presentation.getTypes());
    types.remove(0);

    com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<
            ? extends com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<?>>
        builder = com.danubetech.verifiablecredentials.VerifiablePresentation.builder();

    return builder
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(presentation.getId())
        .types(types)
        .holder(presentation.getHolder())
        .verifiableCredential(dtCredentials.get(0))
        .ldProof(null) // set to null, as presentation will be used within JWT
        .build();
  }

  @NonNull
  @SneakyThrows
  public static VerifiablePresentation map(
      com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation) {

    Objects.requireNonNull(dtPresentation);
    Objects.requireNonNull(dtPresentation.getVerifiableCredential());

    List<VerifiableCredential> credentials = List.of(map(dtPresentation.getVerifiableCredential()));

    return VerifiablePresentation.builder()
        .id(dtPresentation.getId())
        .types(dtPresentation.getTypes())
        .verifiableCredentials(credentials)
        .holder(dtPresentation.getHolder())
        .proof(null) // dtPresentation.getProof() is always null
        .build();
  }

  @NonNull
  @SneakyThrows
  public static com.danubetech.verifiablecredentials.VerifiableCredential map(
      VerifiableCredential credential) {

    if (!credential.getTypes().stream()
        .anyMatch(x -> x.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))) {
      throw new SsiException("Type: VerifiableCredential missing");
    }
    // Verifiable Credential Type is automatically added in Builder
    final List<String> types =
        credential.getTypes().stream()
            .filter(t -> !t.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .collect(Collectors.toList());

    final CredentialSubject subject =
        CredentialSubject.builder().properties(credential.getCredentialSubject()).build();

    return com.danubetech.verifiablecredentials.VerifiableCredential.builder()
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(credential.getId())
        .types(types)
        .issuer(credential.getIssuer())
        .issuanceDate(Date.from(credential.getIssuanceDate()))
        .expirationDate(Date.from(credential.getExpirationDate()))
        .credentialSubject(subject)
        .build();
    // .credentialStatus(credential.getStatus())
  }

  @NonNull
  @SneakyThrows
  public static VerifiableCredential map(
      com.danubetech.verifiablecredentials.VerifiableCredential dtCredential) throws SsiException {
    try {
      VerifiableCredential vc =
          VerifiableCredential.builder()
              .id(dtCredential.getId())
              .types(dtCredential.getTypes())
              .issuer(dtCredential.getIssuer())
              .issuanceDate(dtCredential.getIssuanceDate().toInstant())
              .expirationDate(dtCredential.getExpirationDate().toInstant())
              .proof(map(dtCredential.getLdProof()))
              .build();
      return vc;
    } catch (Exception e) {
      throw new SsiException(e.getMessage());
    }
  }

  private static Ed25519Proof map(LdProof dtProof) {
    if (dtProof == null) return null;

    if (!Ed25519Proof.TYPE.equals(dtProof.getType())) {
      throw new RuntimeException(
          String.format(
              "Proof not supported: %s. Supported Proofs: %s",
              dtProof.getType(), Ed25519Proof.TYPE));
    }

    return Ed25519Proof.builder()
        .created(dtProof.getCreated().toInstant())
        .type(dtProof.getType())
        .proofPurpose(dtProof.getProofPurpose())
        .proofValue(MultibaseFactory.create(dtProof.getProofValue()))
        .verificationMethod(dtProof.getVerificationMethod())
        .build();
  }
}
