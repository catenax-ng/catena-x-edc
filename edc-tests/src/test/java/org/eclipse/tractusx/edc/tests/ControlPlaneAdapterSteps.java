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
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class ControlPlaneAdapterSteps {

  private EndpointDataReference endpointDataReference;

  Map<String, String> propertiesMap =
      new HashMap<>() {
        {
          put("cid", "1:b2367617-5f51-48c5-9f25-e30a7299235c");
        }
      };
  private final EndpointDataReference comparingEndpoint =
      EndpointDataReference.Builder.newInstance()
          .id("ab9420a4-f05a-49eb-8a1f-e6004b593aff")
          .endpoint("endpoint")
          .authKey("key")
          .authCode("code")
          .properties(propertiesMap)
          .build();

  @When("'{connector}' gets a request Endpoint from '{connector}'")
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

  @Then("'{connector}' has sent the correct endpoint connector")
  public void receiveEndpoint(Connector provider) {

    log.info(
        "Id: "
            + endpointDataReference.getId()
            + "\nEndpoint: "
            + endpointDataReference.getEndpoint()
            + "\nAuthCode: "
            + endpointDataReference.getAuthCode()
            + " \nAuthKey: "
            + endpointDataReference.getAuthKey());

    Assertions.assertEquals(endpointDataReference.getId(), comparingEndpoint.getId());
    Assertions.assertEquals(endpointDataReference.getEndpoint(), comparingEndpoint.getEndpoint());
    Assertions.assertEquals(endpointDataReference.getAuthCode(), comparingEndpoint.getAuthCode());
    Assertions.assertEquals(endpointDataReference.getAuthKey(), comparingEndpoint.getAuthKey());
    Assertions.assertEquals(
        endpointDataReference.getProperties().get("cid"),
        comparingEndpoint.getProperties().get("cid"));
  }
}
