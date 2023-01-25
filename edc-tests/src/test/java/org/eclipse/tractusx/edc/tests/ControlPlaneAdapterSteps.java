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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.edc.tests.data.Asset;
import org.eclipse.tractusx.edc.tests.data.Endpoint;

import java.io.IOException;

@Slf4j
public class ControlPlaneAdapterSteps {

    private static final String ASSET_ID = "asset id";

    private Endpoint endpoint;

    @When("'{connector}' gets a request Endpoint from '{connector}'")
    public void getEndPointFromGetRequest(
            Connector consumer, String UrlProvider) throws IOException {
        final var api = consumer.getDataManagementAPI();
        endpoint = api.getEdcEndpoint(ASSET_ID, UrlProvider);
        System.out.println("id" + endpoint.getId() + "" + endpoint.getEndpoint());

    }

    @Then("'{connector}' has received the endpoint connector")
    public void receiveEndpointConnector(Connector consumer) {
        final BackendServiceBackendAPI api = consumer.getBackendServiceBackendAPI();
    }
}
