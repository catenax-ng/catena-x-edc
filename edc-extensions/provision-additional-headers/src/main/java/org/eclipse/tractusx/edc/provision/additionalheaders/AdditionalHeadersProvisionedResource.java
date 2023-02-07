package org.eclipse.tractusx.edc.provision.additionalheaders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.transfer.spi.types.ProvisionedContentResource;

@JsonDeserialize(builder = Builder.class)
class AdditionalHeadersProvisionedResource extends ProvisionedContentResource {

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
