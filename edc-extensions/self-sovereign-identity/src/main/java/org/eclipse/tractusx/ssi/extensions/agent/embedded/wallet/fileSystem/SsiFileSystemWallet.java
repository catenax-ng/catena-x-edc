package org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.fileSystem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredentialType;

@RequiredArgsConstructor
public class SsiFileSystemWallet implements VerifiableCredentialWallet {

  /**
   * Unique identifier of the Wallet. Changing the Identifier of the Wallet is a breaking change and
   * must be documented accordingly.
   */
  private static final String Identifier = "FileSystemWallet";

  private final Path credentialDirectory;

  @Getter(lazy = true)
  private final VerifiableCredential membershipCredential = loadMembershipCredential();

  @Override
  public String getIdentifier() {
    return Identifier;
  }

  @Override
  public VerifiableCredential getCredential(String credentialType) {
    return null;
  }

  private VerifiableCredential loadMembershipCredential() {
    return readCredentials().stream()
        .filter(
            c ->
                c.getTypes().stream()
                    .anyMatch(VerifiableCredentialType.MEMBERSHIP_CREDENTIAL::equals))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("FileSystemWallet: Membership Credential not found"));
  }

  @SneakyThrows
  private List<VerifiableCredential> readCredentials() {
    List<Path> paths = new LinkedList<>();

    if (Files.isDirectory(credentialDirectory)) {
      try (var filesStream = Files.list(credentialDirectory)) {
        filesStream.forEach(paths::add);
      }
    }

    return paths.stream().map(this::readCredentialFile).collect(Collectors.toList());
  }

  @SneakyThrows // TODO Handle and log all invalid files
  private VerifiableCredential readCredentialFile(Path path) {
    try (InputStream is = Files.newInputStream(path)) {
      try (InputStreamReader reader = new InputStreamReader(is)) {

        var dtCredential =
            com.danubetech.verifiablecredentials.VerifiableCredential.fromJson(reader);
        return DanubTechMapper.map(dtCredential);
      }
    }
  }
}