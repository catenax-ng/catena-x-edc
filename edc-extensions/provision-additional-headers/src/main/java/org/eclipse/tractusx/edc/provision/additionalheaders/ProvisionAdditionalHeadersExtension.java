package org.eclipse.tractusx.edc.provision.additionalheaders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.transfer.spi.provision.ProviderResourceDefinitionGenerator;
import org.eclipse.edc.connector.transfer.spi.provision.ProvisionManager;
import org.eclipse.edc.connector.transfer.spi.provision.Provisioner;
import org.eclipse.edc.connector.transfer.spi.provision.ResourceManifestGenerator;
import org.eclipse.edc.connector.transfer.spi.types.*;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.HttpDataAddress;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProvisionAdditionalHeadersExtension implements ServiceExtension {

    @Inject
    private ResourceManifestGenerator resourceManifestGenerator;

    @Inject
    private ProvisionManager provisionManager;

    @Override
    public void initialize(ServiceExtensionContext context) {
        resourceManifestGenerator.registerGenerator(new AdditionalHeadersResourceDefinitionGenerator());
        provisionManager.register(new AdditionalHeadersProvisioner());
    }

    @JsonPOJOBuilder(withPrefix = "")
    private static class AdditionalHeadersResourceDefinition extends ResourceDefinition {

        private String contractId;
        private DataAddress dataAddress;

        @Override
        public Builder toBuilder() {
            return initializeBuilder(new Builder());
        }

        public DataAddress getDataAddress() {
            return dataAddress;
        }

        public String getContractId() {
            return contractId;
        }

        public static class Builder extends ResourceDefinition.Builder<AdditionalHeadersResourceDefinition, Builder> {

            protected Builder() {
                super(new AdditionalHeadersResourceDefinition());
            }

            @JsonCreator
            public static Builder newInstance() {
                return new Builder();
            }

            public Builder contractId(String contractId) {
                this.resourceDefinition.contractId = contractId;
                return this;
            }

            public Builder dataAddress(DataAddress dataAddress) {
                this.resourceDefinition.dataAddress = dataAddress;
                return this;
            }
        }
    }

    private static class AdditionalHeadersResourceDefinitionGenerator implements ProviderResourceDefinitionGenerator {
        @Override
        public @Nullable ResourceDefinition generate(DataRequest dataRequest, DataAddress dataAddress, Policy policy) {
            return AdditionalHeadersResourceDefinition.Builder.newInstance()
                    .id(UUID.randomUUID().toString())
                    .dataAddress(dataAddress)
                    .contractId(dataRequest.getContractId())
                    .build();
        }

        @Override
        public boolean canGenerate(DataRequest dataRequest, DataAddress dataAddress, Policy policy) {
            return "HttpData".equals(dataAddress.getType());
        }
    }

    private class AdditionalHeadersProvisioner implements Provisioner<AdditionalHeadersResourceDefinition, AdditionalHeadersProvisionedResource> {
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
                    .addAdditionalHeader("contractId", resourceDefinition.getContractId())
                    .build();

            var provisioned = AdditionalHeadersProvisionedResource.Builder.newInstance()
                    .id(UUID.randomUUID().toString())
                    .resourceDefinitionId(resourceDefinition.getId())
                    .transferProcessId(resourceDefinition.getTransferProcessId())
                    .dataAddress(address)
                    .resourceName(UUID.randomUUID().toString()) // TODO: why this is mandatory?
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

    @JsonDeserialize(builder = AdditionalHeadersProvisionedResource.Builder.class)
    private static class AdditionalHeadersProvisionedResource extends ProvisionedContentResource {

        @JsonPOJOBuilder(withPrefix = "")
        public static class Builder extends ProvisionedContentResource.Builder<AdditionalHeadersProvisionedResource, Builder> {

            private Builder() {
                super(new AdditionalHeadersProvisionedResource());
            }

            @JsonCreator
            public static Builder newInstance() {
                return new Builder();
            }

        }
    }
}
