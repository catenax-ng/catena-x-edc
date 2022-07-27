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
 *       Mercedes-Benz Tech Innovation GmbH - Initial Test
 *
 */

package net.catenax.edc.hashicorpvault;

import static net.catenax.edc.hashicorpvault.HashicorpVaultClient.VAULT_DATA_ENTRY_NAME;
import static net.catenax.edc.hashicorpvault.HashicorpVaultExtension.VAULT_TOKEN;
import static net.catenax.edc.hashicorpvault.HashicorpVaultExtension.VAULT_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.eclipse.dataspaceconnector.junit.extensions.EdcExtension;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckResult;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckService;
import org.eclipse.dataspaceconnector.spi.system.health.HealthStatus;
import org.eclipse.dataspaceconnector.spi.system.health.LivenessProvider;
import org.eclipse.dataspaceconnector.spi.system.health.ReadinessProvider;
import org.eclipse.dataspaceconnector.spi.system.health.StartupStatusProvider;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;

@Testcontainers
@ExtendWith(EdcExtension.class)
class AbstractHashicorpIT {
  static final String DOCKER_IMAGE_NAME = "vault:1.9.6";
  static final String VAULT_ENTRY_KEY = "testing";
  static final String VAULT_ENTRY_VALUE = UUID.randomUUID().toString();
  static final String TOKEN = UUID.randomUUID().toString();

  private final TestExtension testExtension = new TestExtension();

  protected Vault getVault() {
    return testExtension.getVault();
  }

  protected CertificateResolver getCertificateResolver() {
    return testExtension.getCertificateResolver();
  }

  @Container @ClassRule
  private static final VaultContainer<?> vaultContainer =
      new VaultContainer<>(DockerImageName.parse(DOCKER_IMAGE_NAME))
          .withVaultToken(TOKEN)
          .withSecretInVault(
              "secret/" + VAULT_ENTRY_KEY,
              String.format("%s=%s", VAULT_DATA_ENTRY_NAME, VAULT_ENTRY_VALUE));

  @BeforeEach
  final void beforeEach(EdcExtension extension) {
    extension.setConfiguration(getConfig());
    extension.registerServiceMock(HealthCheckService.class, new MyHealthCheckService());
    extension.registerSystemExtension(ServiceExtension.class, testExtension);
  }

  protected Map<String, String> getConfig() {
    return new HashMap<>() {
      {
        put(
            VAULT_URL,
            String.format(
                "http://%s:%s", vaultContainer.getHost(), vaultContainer.getFirstMappedPort()));
        put(VAULT_TOKEN, TOKEN);
      }
    };
  }

  @Getter
  private static class TestExtension implements ServiceExtension {
    private Vault vault;
    private CertificateResolver certificateResolver;

    @Override
    public void initialize(ServiceExtensionContext context) {
      vault = context.getService(Vault.class);
      certificateResolver = context.getService(CertificateResolver.class);
    }
  }

  private static class MyHealthCheckService implements HealthCheckService {
    private final List<LivenessProvider> livenessProviders = new ArrayList<>();
    private final List<ReadinessProvider> readinessProviders = new ArrayList<>();
    private final List<StartupStatusProvider> startupStatusProviders = new ArrayList<>();

    @Override
    public void addLivenessProvider(LivenessProvider provider) {
      livenessProviders.add(provider);
    }

    @Override
    public void addReadinessProvider(ReadinessProvider provider) {
      readinessProviders.add(provider);
    }

    @Override
    public void addStartupStatusProvider(StartupStatusProvider provider) {
      startupStatusProviders.add(provider);
    }

    @Override
    public HealthStatus isLive() {
      return new HealthStatus(
          livenessProviders.stream()
              .map(
                  p ->
                      p.get().failed() ? HealthCheckResult.failed("") : HealthCheckResult.success())
              .collect(Collectors.toList()));
    }

    @Override
    public HealthStatus isReady() {
      return new HealthStatus(
          readinessProviders.stream()
              .map(
                  p ->
                      p.get().failed() ? HealthCheckResult.failed("") : HealthCheckResult.success())
              .collect(Collectors.toList()));
    }

    @Override
    public HealthStatus getStartupStatus() {
      return new HealthStatus(
          startupStatusProviders.stream()
              .map(
                  p ->
                      p.get().failed() ? HealthCheckResult.failed("") : HealthCheckResult.success())
              .collect(Collectors.toList()));
    }

    @Override
    public void refresh() {
      // why?
    }
  }
}
