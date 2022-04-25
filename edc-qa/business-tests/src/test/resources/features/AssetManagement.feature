Feature: Management of Assets

  Scenario: Asset Creation
    Given asset 'asset-1'
    * has asset description 'foo'
    When  asset 'asset-1' is created for Connector 'A'
    Then Connector 'A' has asset 'asset-1'
    * with asset description 'foo'