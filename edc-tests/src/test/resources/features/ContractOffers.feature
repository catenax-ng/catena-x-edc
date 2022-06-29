#
#  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
#

Feature: Contract Offers

  Background: The Connector State
    Given 'Plato' has no assets
    Given 'Plato' has no policies
    Given 'Plato' has no contract definitions

  Scenario: Catalog Request
    Given 'Plato' has the following asset/s
      | id      | description   |
      | asset-1 | Example Asset |
      | asset-2 | Example Asset |
    And 'Plato' has the following policy/s
      | id       |
      | policy-1 |
    And 'Plato' has the following contract definition/s
      | id                    | access policy | contract policy | asset   |
      | contract-definition-1 | policy-1      | policy-1        | asset-1 |
      | contract-definition-2 | policy-1      | policy-1        | asset-2 |
    When 'Sokrates' requests the catalog from 'Plato'
    Then the catalog contains the following offers
      | source definition | source contract policy | asset   |
      | contract-offer-1  | policy-1               | asset-1 |
      | contract-offer-2  | policy-1               | asset-2 |