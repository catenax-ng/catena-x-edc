package org.eclipse.tractusx.edc.provision.additionalheaders;

import org.eclipse.edc.connector.transfer.spi.provision.ProviderResourceDefinitionGenerator;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

class AdditionalHeadersResourceDefinitionGenerator implements ProviderResourceDefinitionGenerator {
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
