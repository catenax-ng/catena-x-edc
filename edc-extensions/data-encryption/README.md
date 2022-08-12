# Data Encryption Extension

The Eclipse Dataspace Connector encrypts sensitive information inside a token, it sends to other applications (from other companies). This extension does the actual encryption of this data and should be used with secure keys and algorithms at all times.

## Configuration

| Key                                         | Description                                                                                                      | Mandatory | Default          |
|:--------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|-----------|------------------|
| edc.data.encryption.keys.alias              | Keys for encryption and decryption of the data must be stored in the Vault under the configured alias.           | X         |                  |
| edc.data.encryption.algorithm               | Algorithm for encryption and decryption. Must be ether 'AES' or 'NONE'.                                          |           | AES              |
| edc.data.encryption.caching.enabled         | If caching is disabled the extension will always request the latest keys from the vault for each encryption/decryption.                             |           | false            |
| edc.data.encryption.caching.seconds         | Duration in seconds until the cache expires.                                                                     |           | 3600             |

## Strategies

### AES

The Advanced Encryption Standard (AES) is the default encryption algorithm. For Authenticated Encryption with Associated Data (AEAD) it uses the Galois/Counter Mode or GCM.

When using AES-GCM the key length must be ether 128-, 196- or 256bit. Keys are stored Base64 encoded in the Vault and separated by a comma.

It's possible to generate Keys using OpenSLL
```bash
# 128 Bit
openssl rand -base64 16

# 196 Bit
openssl rand -base64 24

#256 Bit
openssl rand -base64 32
```


### NONE

This strategy does apply no encryption at all and should only be used for debugging purposes.