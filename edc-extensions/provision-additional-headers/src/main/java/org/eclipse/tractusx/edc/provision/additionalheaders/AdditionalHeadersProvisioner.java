package org.eclipse.tractusx.edc.provision.additionalheaders;

import org.eclipse.edc.connector.transfer.spi.provision.Provisioner;
import org.eclipse.edc.connector.transfer.spi.types.DeprovisionedResource;
import org.eclipse.edc.connector.transfer.spi.types.ProvisionResponse;
import org.eclipse.edc.connector.transfer.spi.types.ProvisionedResource;
import org.eclipse.edc.connector.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.HttpDataAddress;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AdditionalHeadersProvisioner implements Provisioner<AdditionalHeadersResourceDefinition, AdditionalHeadersProvisionedResource> {

    @Override
    public boolean canProvision(ResourceDefinition resourceDefinition) {
        return resourceDefinition instanceof AdditionalHeadersResourceDefinition;
    }

    @Override
    public boolean canDeprovision(ProvisionedResource provisionedResource) {
        return false; // nothing to deprovision
    }

    @Override
    public CompletableFuture<StatusResult<ProvisionResponse>> provision(AdditionalHeadersResourceDefinition resourceDefinition, Policy policy) {

        var address = HttpDataAddress.Builder.newInstance()
                .copyFrom(resourceDefinition.getDataAddress())
                .addAdditionalHeader("Edc-Contract-Id", resourceDefinition.getContractId())
                .build();

        var provisioned = AdditionalHeadersProvisionedResource.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .resourceDefinitionId(resourceDefinition.getId())
                .transferProcessId(resourceDefinition.getTransferProcessId())
                .dataAddress(address)
                .resourceName(UUID.randomUUID().toString()) // TODO: why is this mandatory?
                .hasToken(false)
                .build();

        var response = ProvisionResponse.Builder.newInstance()
                .resource(provisioned)
                .build();
        var result = StatusResult.success(response);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<StatusResult<DeprovisionedResource>> deprovision(AdditionalHeadersProvisionedResource additionalHeadersProvisionedResource, Policy policy) {
        return null; //nothing to deprovision
    }
}
