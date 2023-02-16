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

import com.google.gson.Gson;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.dataspaceconnector.spi.system.health.HealthStatus;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
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

  @Then("'{connector}' asks for the asset from the endpoint")
  public void receiveEndpoint(Connector consumer) throws IOException {

    var requestUrl = endpointDataReference.getEndpoint();
    var key = endpointDataReference.getAuthKey();
    var value = endpointDataReference.getAuthCode();
    var httpClient = HttpClientBuilder.create().build();
    var get = new HttpGet(requestUrl);
    get.addHeader(key, value);
    log.info("before getting method");
    final CloseableHttpResponse response = httpClient.execute(get);
    log.info("the get request was executed " + response.toString());
    var bytes = response.getEntity().getContent().readAllBytes();
    log.info("the response was converted into bytes " + bytes.toString());
    var result = new String(bytes);
    log.info("Result of receiveEndpoint Method: " + result);
    var resultTransformed = new Gson().fromJson(result, HealthStatus.class);

    Assertions.assertTrue(resultTransformed.isHealthy());
    Assertions.assertFalse(resultTransformed.getComponentResults().isEmpty());
  }
}
