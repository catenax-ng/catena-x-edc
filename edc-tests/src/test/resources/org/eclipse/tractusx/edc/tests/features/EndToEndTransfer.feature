#
#  Copyright (c) 2023 ZF Friedrichshafen AG
#  Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#

Feature: API-Wrapper Extension

  Background: The Connector State
    Given 'Plato' has an empty database
    Given 'Sokrates' has an empty database

  Scenario: Connector asks for an endpoint connector from another one
    Given 'Plato' has the following assets
      | id      | description               | baseUrl                          |
      | asset-1 | http proxy transfer asset | http://localhost:8081/api/health |
    And 'Plato' has the following policies
      | id       | action |
      | policy-1 | USE    |
    And 'Plato' has the following contract definitions
      | id                    | access policy | contract policy | asset   |
      | contract-definition-1 | policy-1      | policy-1        | asset-1 |
    When 'Sokrates' gets a request Endpoint from 'Plato'
      | asset id | url provider                                                                                               |
      | asset-1  | http://localhost:9193/api/v1/data/adapter/asset/sync/123?providerUrl=http://localhost:8182/api/v1/ids/data |
    Then 'Sokrates' has received the endpoint connector