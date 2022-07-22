/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *       Mercedes-Benz Tech Innovation GmbH - Make secret data & metadata paths configurable
 *
 */

package net.catenax.edc.hashicorpvault;

import java.time.Duration;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.security.VaultPrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.VaultExtension;

public class HashicorpVaultExtension implements VaultExtension {

  @EdcSetting(required = true)
  public static final String VAULT_URL = "edc.vault.hashicorp.url";

  @EdcSetting(required = true)
  public static final String VAULT_TOKEN = "edc.vault.hashicorp.token";

  @EdcSetting
  public static final String VAULT_API_SECRET_PATH = "edc.vault.hashicorp.api.secret.path";

  public static final String VAULT_API_SECRET_PATH_DEFAULT = "/v1/secret";

  @EdcSetting
  private static final String VAULT_TIMEOUT_SECONDS = "edc.vault.hashicorp.timeout.seconds";

  private Vault vault;
  private CertificateResolver certificateResolver;
  private PrivateKeyResolver privateKeyResolver;

  @Override
  public String name() {
    return "Hashicorp Vault";
  }

  @Override
  public Vault getVault() {
    return vault;
  }

  @Override
  public PrivateKeyResolver getPrivateKeyResolver() {
    return privateKeyResolver;
  }

  @Override
  public CertificateResolver getCertificateResolver() {
    return certificateResolver;
  }

  @Override
  public void initializeVault(ServiceExtensionContext context) {
    final HashicorpVaultClientConfig config = loadHashicorpVaultClientConfig(context);

    final OkHttpClient okHttpClient = createOkHttpClient(config);

    final HashicorpVaultClient client =
        new HashicorpVaultClient(config, okHttpClient, context.getTypeManager().getMapper());

    vault = new HashicorpVault(client, context.getMonitor());
    certificateResolver = new HashicorpCertificateResolver(vault, context.getMonitor());
    privateKeyResolver = new VaultPrivateKeyResolver(vault);

    context.getMonitor().info("HashicorpVaultExtension: authentication/initialization complete.");
  }

  private OkHttpClient createOkHttpClient(HashicorpVaultClientConfig config) {
    OkHttpClient.Builder builder =
        new OkHttpClient.Builder()
            .callTimeout(config.getTimeout())
            .readTimeout(config.getTimeout());

    return builder.build();
  }

  private HashicorpVaultClientConfig loadHashicorpVaultClientConfig(
      ServiceExtensionContext context) {

    final String vaultUrl = context.getSetting(VAULT_URL, null);
    if (vaultUrl == null) {
      throw new HashicorpVaultException(String.format("Vault URL (%s) must be defined", VAULT_URL));
    }

    final int vaultTimeoutSeconds = Math.max(0, context.getSetting(VAULT_TIMEOUT_SECONDS, 30));
    final Duration vaultTimeoutDuration = Duration.ofSeconds(vaultTimeoutSeconds);

    final String vaultToken = context.getSetting(VAULT_TOKEN, null);

    if (vaultToken == null) {
      throw new EdcException(
          String.format("For Vault authentication [%s] is required", VAULT_TOKEN));
    }

    final String apiSecretPath =
        context.getSetting(VAULT_API_SECRET_PATH, VAULT_API_SECRET_PATH_DEFAULT);

    return HashicorpVaultClientConfig.builder()
        .vaultUrl(vaultUrl)
        .vaultToken(vaultToken)
        .vaultApiPath(apiSecretPath)
        .timeout(vaultTimeoutDuration)
        .build();
  }
}
