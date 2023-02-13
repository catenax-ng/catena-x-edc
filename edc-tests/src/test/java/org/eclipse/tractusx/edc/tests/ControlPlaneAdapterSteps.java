/*
 *  Copyright (c) 2023 ZF Friedrichshafen AG
 *  Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.tractusx.edc.tests;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.edc.tests.data.Asset;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class ControlPlaneAdapterSteps {

  private EndpointDataReference endpointDataReference;

  @When("'{connector}' gets a request endpoint from '{connector}'")
  public void getEndPointFromGetRequest(Connector consumer, Connector receiver, DataTable table)
      throws IOException {

    final DataManagementAPI dataManagementAPI = consumer.getDataManagementAPI();
    final String receiverIdsUrl = receiver.getEnvironment().getIdsUrl() + "/data";

    for (Map<String, String> map : table.asMaps()) {
      final String assetId = map.get("asset id");

      endpointDataReference = dataManagementAPI.getEdcEndpoint(assetId, receiverIdsUrl);

      log.debug("endpointDataReference in controlplane" + endpointDataReference.toString());
    }
  }

  @Then("'{connector}' can send '{connector}' the asset")
  public void receiveEndpoint(Connector provider, Connector consumer) throws IOException {

    String providerUrl = provider.getEnvironment().getIdsUrl();
    String requestUrl =
        endpointDataReference.getEndpoint() + "?provider-connector-url=" + providerUrl;

    final DataManagementAPI dataManagementAPI = consumer.getDataManagementAPI();

    log.info("url: " + requestUrl);

    Asset asset =
        dataManagementAPI.initiateTransferProcess(
            requestUrl, endpointDataReference.getAuthKey(), endpointDataReference.getAuthCode());

    Assertions.assertNotEquals(null, asset.getId());
    Assertions.assertNotEquals(null, asset.getDescription());
    Assertions.assertNotEquals(null, asset.getDataAddress());
  }
}
