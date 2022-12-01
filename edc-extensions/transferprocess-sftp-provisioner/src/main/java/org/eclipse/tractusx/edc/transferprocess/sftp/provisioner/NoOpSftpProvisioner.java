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
 *
 */

package org.eclipse.tractusx.edc.transferprocess.sftp.provisioner;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.policy.engine.PolicyEngine;
import org.eclipse.dataspaceconnector.spi.response.ResponseStatus;
import org.eclipse.dataspaceconnector.spi.response.StatusResult;
import org.eclipse.dataspaceconnector.spi.transfer.provision.Provisioner;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DeprovisionedResource;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ProvisionResponse;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ProvisionedResource;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ResourceDefinition;
import org.eclipse.tractusx.edc.transferprocess.sftp.common.SftpLocation;
import org.eclipse.tractusx.edc.transferprocess.sftp.common.SftpUser;

@RequiredArgsConstructor
public class NoOpSftpProvisioner
    implements Provisioner<SftpProviderResourceDefinition, SftpProvisionedContentResource> {
  static final String DATA_ADDRESS_TYPE = "sftp";
  static final String PROVIDER_TYPE = "NoOp";

  @NonNull private final PolicyEngine policyEngine;

  @NonNull private final NoOpSftpProvider sftpProvider;

  @Override
  public boolean canProvision(@NonNull ResourceDefinition resourceDefinition) {
    return resourceDefinition instanceof SftpProviderResourceDefinition
        && DATA_ADDRESS_TYPE.equals(
            ((SftpProviderResourceDefinition) resourceDefinition).getDataAddressType())
        && PROVIDER_TYPE.equals(
            ((SftpProviderResourceDefinition) resourceDefinition).getProviderType());
  }

  @Override
  public boolean canDeprovision(@NonNull ProvisionedResource provisionedResource) {
    if (!(provisionedResource instanceof SftpProvisionedContentResource)) {
      return false;
    }
    SftpProvisionedContentResource resource = (SftpProvisionedContentResource) provisionedResource;
    DataAddress dataAddress = resource.getDataAddress();
    if (dataAddress == null) {
      return false;
    }
    return DATA_ADDRESS_TYPE.equals(dataAddress.getType())
        && PROVIDER_TYPE.equals(
            ((SftpProvisionedContentResource) provisionedResource).getProviderType());
  }

  @Override
  public CompletableFuture<StatusResult<ProvisionResponse>> provision(
      SftpProviderResourceDefinition sftpProviderResourceDefinition, Policy policy) {

    return CompletableFuture.supplyAsync(
        () -> {
          SftpLocation location;
          SftpUser user;
          try {
            // TODO: policyEngine.filter()
            location = Objects.requireNonNull(sftpProviderResourceDefinition.getSftpLocation());
            user = Objects.requireNonNull(sftpProviderResourceDefinition.getSftpUser());
            sftpProvider.createLocation(location);
            sftpProvider.createUser(user);
          } catch (Exception e) {
            return StatusResult.failure(ResponseStatus.FATAL_ERROR, e.getMessage());
          }

          SftpProvisionedContentResource sftpProvisionedContentResource =
              new SftpProvisionedContentResource(
                  user,
                  location,
                  sftpProviderResourceDefinition.getTransferProcessId(),
                  PROVIDER_TYPE);

          return StatusResult.success(
              ProvisionResponse.Builder.newInstance()
                  .resource(sftpProvisionedContentResource)
                  .build());
        });
  }

  @Override
  public CompletableFuture<StatusResult<DeprovisionedResource>> deprovision(
      SftpProvisionedContentResource sftpProvisionedContentResource, Policy policy) {
    return CompletableFuture.supplyAsync(
        () -> {
          SftpLocation location;
          SftpUser user;
          try {
            // TODO: policyEngine.filter()
            location = Objects.requireNonNull(sftpProvisionedContentResource.getSftpLocation());
            user = Objects.requireNonNull(sftpProvisionedContentResource.getSftpUser());
            sftpProvider.deleteLocation(location);
            sftpProvider.deleteUser(user);
          } catch (Exception e) {
            return StatusResult.failure(ResponseStatus.FATAL_ERROR, e.getMessage());
          }

          DeprovisionedResource deprovisionedResource =
              DeprovisionedResource.Builder.newInstance()
                  .provisionedResourceId(sftpProvisionedContentResource.getTransferProcessId())
                  .inProcess(true)
                  .build();

          return StatusResult.success(deprovisionedResource);
        });
  }
}
