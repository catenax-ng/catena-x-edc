package org.eclipse.tractusx.edc.provision.additionalheaders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.transfer.spi.provision.ProvisionManager;
import org.eclipse.edc.connector.transfer.spi.provision.ResourceManifestGenerator;
import org.eclipse.edc.connector.transfer.spi.types.*;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

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

}
