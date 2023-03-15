# Self-Sovereign Embedded Agent Extension

The embedded SSI Agent extension does not come with the capability of a normal SSI agent and should only be used for
demonstration purposes.

# Settings

| Name                                           | Description                                                                                                                                                                     | Mandatory |
|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| edc.ssi.wallet                                 | Identifier of the SSI wallet type. Base supported wallets are `FileSystemWallet` and `VaultWallet`. Each wallet comes with its own settings. Default Wallet: `FileSystemWallet` |           |
| edc.ssi.did.private.key.alias                  | Vault alias of the DID private key. Used to sign Verifiable Presentations.                                                                                                      | X         |
| edc.ssi.did                                    | Decentralized Identifier (DID) of the connector. By default 'did:null:connector'. If not configured the connector cannot communicate with other connectors.                     |           |
| edc.ssi.verifiable.presentation.signing.method | Signing Method of the Verifiable Presentation JWT. Supported Methods: [`ES256`], Default Method: `ES256`                                                                        ||

# Extension Points

## DID Methods

## Wallet

## Supported Default DID Methods

### DID Web

https://w3c-ccg.github.io/did-method-web/

## Basic Wallets

### File System Wallet

| Name                                | Description                                                 | Mandatory |
|:------------------------------------|:------------------------------------------------------------|-----------|
| edc.ssi.wallet.credential.directory | Path to verifiable credential directory. Default `/tmp/vc/` |           |

### Vault Wallet

| Name                                  | Description                                             | Mandatory |
|:--------------------------------------|:--------------------------------------------------------|-----------|
| edc.ssi.wallet.vault.credential.alias | Secret name where the verifiable credentials are stored | X         |