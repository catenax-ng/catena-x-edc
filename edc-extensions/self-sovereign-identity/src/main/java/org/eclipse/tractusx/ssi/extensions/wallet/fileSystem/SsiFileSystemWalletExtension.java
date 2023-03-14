package org.eclipse.tractusx.ssi.extensions.wallet.fileSystem;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiException;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiSettingException;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.spi.wallet.VerifiableCredentialWalletRegistry;

@Requires({VerifiableCredentialWalletRegistry.class})
public class SsiFileSystemWalletExtension implements ServiceExtension {

  public static final String EXTENSION_NAME = "File System Wallet Extension";

  public static final String SETTINGS_WALLET_PATH = "edc.ssi.wallet.credential.filepath";

  private ServiceExtensionContext context;

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void start() {

    final Path credentialsPath = getCredentialsPath();
    final boolean isDirectory = Files.isDirectory(credentialsPath);
    final boolean isFile = Files.isRegularFile(credentialsPath);

    if (!isDirectory && !isFile) {
      throw new SsiSettingException(
          String.format(
              "Setting '%s' does not point an existing director or file.", SETTINGS_WALLET_PATH));
    }
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    this.context = context;

    final Path credentialsPath = getCredentialsPath();
    final VerifiableCredentialWallet wallet = new SsiFileSystemWallet(credentialsPath);

    VerifiableCredentialWalletRegistry walletRegistry =
        context.getService(VerifiableCredentialWalletRegistry.class);
    walletRegistry.register(wallet);
  }

  private Path getCredentialsPath() {

    final String credentialFilePath = context.getSetting(SETTINGS_WALLET_PATH, null);
    if (credentialFilePath == null) {
      throw new IllegalArgumentException("Mandatory setting not provided: " + SETTINGS_WALLET_PATH);
    }
    try {
      return Path.of(credentialFilePath);
    } catch (InvalidPathException e) {
      throw new SsiException(
          String.format("Setting '%s' does not contain a valid path.", SETTINGS_WALLET_PATH), e);
    }
  }
}
