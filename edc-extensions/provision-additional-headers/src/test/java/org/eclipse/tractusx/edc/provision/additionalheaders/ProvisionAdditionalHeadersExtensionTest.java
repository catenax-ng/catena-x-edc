package org.eclipse.tractusx.edc.provision.additionalheaders;

import org.awaitility.Awaitility;
import org.eclipse.edc.connector.transfer.spi.TransferProcessManager;
import org.eclipse.edc.connector.transfer.spi.flow.DataFlowController;
import org.eclipse.edc.connector.transfer.spi.flow.DataFlowManager;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(EdcExtension.class)
class ProvisionAdditionalHeadersExtensionTest {

    private final DataFlowController dataFlowController = mock(DataFlowController.class);

    @BeforeEach
    void setUp() {
        when(dataFlowController.canHandle(any(), any())).thenReturn(true);
        when(dataFlowController.initiateFlow(any(), any(), any())).thenReturn(StatusResult.success());
      }

    @Test
    void shouldPutContractIdAsHeaderInDataAddress(TransferProcessManager transferProcessManager, AssetIndex assetIndex, DataFlowManager dataFlowManager) {
        dataFlowManager.register(dataFlowController);
        var asset = Asset.Builder.newInstance().id("assetId").build();
        var dataAddress = DataAddress.Builder.newInstance().type("HttpData").build();
        assetIndex.accept(asset, dataAddress);

        var dataRequest = DataRequest.Builder.newInstance()
                .contractId("aContractId")
                .assetId("assetId")
                .destinationType("HttpProxy")
                .build();

        var result = transferProcessManager.initiateProviderRequest(dataRequest);

        assertThat(result).matches(StatusResult::succeeded);

        await().untilAsserted(() -> {
            // TODO: change contractId header name to something better
            verify(dataFlowController).initiateFlow(any(), argThat(it -> it.hasProperty("header:contractId")), any());
        });
    }
}
