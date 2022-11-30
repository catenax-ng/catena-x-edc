# Release Notes Version 0.1.3

30.11.2022

# 1. Product EDC

## 1.1 Business Partner Extension

**Removed support for Constraint with multiple BPNs**
The possibility to use multiple Business Partner Numbers inside of a single constraint has been removed. It looks like
this was only possible due to a missing feature and may lead to unexpected side
effects (https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/issues/2026)

Hence, this kind of policy is no longer supported!

```json
{
  "uid": "<PolicyId>",
  "prohibitions": [],
  "obligations": [],
  "permissions": [
    {
      "edctype": "dataspaceconnector:permission",
      "action": {
        "type": "USE"
      },
      "constraints": [
        {
          "edctype": "AtomicConstraint",
          "leftExpression": {
            "edctype": "dataspaceconnector:literalexpression",
            "value": "BusinessPartnerNumber"
          },
          "rightExpression": {
            "edctype": "dataspaceconnector:literalexpression",
            "value": [
              "<BPN1>",
              "<BPN2>"
            ]
          },
          "operator": "IN"
        }
      ]
    }
  ]
}
```

The BPN extension will now always decline BPN policies with 'IN' operators, when asked by the EDC to enforce it.

## 1.2 OAuth2 Extension

**Add official EDC OAuth2 Extension**
The EDC Oauth2 Extension has now the possibility to add the audience to the claim. So this extension is now again part
of the Control plane and most of the functionality of the CX Oauth2 Extension was removed.

TODO Write about config change