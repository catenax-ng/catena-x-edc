# Self-Sovereign Identity Extension

Experimental development of a self-sovereign connector.

# Settings

| Name                                               | Description                                        |
|----------------------------------------------------|----------------------------------------------------|
| edc.ssi.wallet                                     | Settings for Wallet                                |
| edc.ssi.verifiable.presentation.signing.key.alias  | SETTING_VERIFIABLE_PRESENTATION_SIGNING_KEY_ALIAS  |
| edc.ssi.did.operator                               | SETTING_DID_OPERATOR                               |
| edc.ssi.verifiable.presentation.signing.method     | SETTING_VERIFIABLE_PRESENTATION_SIGNING_METHOD     |
| edc.ssi.did.connector                              | SETTING_DID_CONNECTOR                              |
| edc.ssi.wallet.storage.membership.credential.alias | SETTING_WALLET_STORAGE_MEMBERSHIP_CREDENTIAL_ALIAS |
| edc.ssi.wallet.storage.credential.alias.list       | SETTING_WALLET_STORAGE_CREDENTIAL_ALIAS_LIST       |

# Extension Points

## DID Methods

## Wallet

## Supported Default DID Methods

### DID Web

https://w3c-ccg.github.io/did-method-web/

## Supported Default Wallets

### File System Wallet

### Vault Wallet

| Name                             | Description                                  |
|:---------------------------------|:---------------------------------------------|
| edc.ssi.did.document.vault.alias | Secret name where the DID document is stored |