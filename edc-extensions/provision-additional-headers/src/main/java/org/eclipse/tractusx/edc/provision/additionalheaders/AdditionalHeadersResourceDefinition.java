package org.eclipse.tractusx.edc.provision.additionalheaders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.spi.types.domain.DataAddress;

@JsonPOJOBuilder(withPrefix = "")
class AdditionalHeadersResourceDefinition extends ResourceDefinition {

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
