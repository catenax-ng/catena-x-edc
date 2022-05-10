/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.core.security.hashicorp;

import java.util.Objects;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.security.VaultPrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.VaultExtension;

public class HashicorpVaultExtension implements VaultExtension {

  @EdcSetting public static final String VAULT_URL = "edc.vault.url";

  @EdcSetting public static final String VAULT_TOKEN = "edc.vault.token";

  private Vault vault;

  @Override
  public String name() {
    return "Hashicorp Vault";
  }

  @Override
  public void initialize(Monitor monitor) {
    monitor.debug("HashicorpVaultExtension: general initialization complete");
  }

  @Override
  public Vault getVault() {
    return vault;
  }

  @Override
  public PrivateKeyResolver getPrivateKeyResolver() {
    return new VaultPrivateKeyResolver(vault);
  }

  @Override
  public CertificateResolver getCertificateResolver() {
    return new HashicorpCertificateResolver(vault);
  }

  @Override
  public void initializeVault(ServiceExtensionContext context) {
    String vaultUrl = Objects.requireNonNull(context.getSetting(VAULT_URL, null));
    String vaultToken = Objects.requireNonNull(context.getSetting(VAULT_TOKEN, null));

    // TODO: check where the OkHttpClient comes from
    OkHttpClient httpClient = new OkHttpClient.Builder().build();
    HashicorpVaultClientConfig config =
        HashicorpVaultClientConfig.builder().vaultUrl(vaultUrl).vaultToken(vaultToken).build();
    HashicorpVaultClient client =
        new HashicorpVaultClient(config, httpClient, context.getTypeManager().getMapper());
    vault = new HashicorpVault(client, context.getMonitor());

    context.getMonitor().info("HashicorpVaultExtension: authentication/initialization complete.");
  }
}