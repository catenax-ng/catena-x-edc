#
#  Copyright (c) 2022 ZF Friedrichshafen AG
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       ZF Friedrichshafen AG - initial implementation
#

Feature: S3 File Transfer

  Background: The Connector State
    Given 'Plato' has an empty database
    Given 'Sokrates' has an empty database
    Given 'Sokrates' has an empty storage bucket called 'destination-bucket'
    Given 'Plato' has a storage bucket called 'source-bucket' with the file called 'testfile'

  Scenario: Request file transfer via S3
    Given 'Plato' has the following S3 assets
      | id      | description   | data_address_type | data_address_s3_bucket_name | data_address_s3_key_name | data_address_s3_region |
      | asset-1 | Example Asset | AmazonS3          | source-bucket               | testfile             | us-east-1              |
    And 'Plato' has the following policies
      | id            | action | payMe |
      | policy-1      | USE    |       |
    And 'Plato' has the following contract definitions
      | id                    | access policy | contract policy | asset   |
      | contract-definition-1 | policy-1      | policy-1        | asset-1 |
    When 'Sokrates' requests the catalog from 'Plato'
    Then the catalog contains the following offers
      | source definition     | asset   |
      | contract-definition-1 | asset-1 |
    Then 'Sokrates' negotiates the contract successfully with 'Plato'
      | contract offer id     | asset id | policy id |
      | contract-definition-1 | asset-1  | policy-1  |
    Then 'Sokrates' initiate transfer process from 'Plato'
      | data_address_type | data_address_s3_bucket_name | data_address_s3_key_name | data_address_s3_region |
      | AmazonS3          | destination-bucket          | testfile                | us-east-1              |
    Then 'Sokrates' has a storage bucket called 'destination-bucket' with transferred file called 'testfile'