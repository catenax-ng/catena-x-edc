# Self-Sovereign Identity

The **Self-Sovereign Identity Extension** may be used instead of the IDS DAPS Extension. Instead of the DAPS token
itself and its attributes it will check the identity of other connectors using Verifiable Credentials.


**Table of Contents**
1. Setup Self-Sovereign Connector
    2. Create DID Document


## Setup Self-Sovereign Connector

A SSI Connectors needs a validate


### Create DID Document

#### Generate Keys

Generate Private key
```shell
ssh-keygen -t ed25519 -f ./ed25519.pem
```

```shell
openssl genpkey -algorithm ed25519 -out ed25519.pem
openssl pkey -in ed25519.pem -pubout -out ed25519.pem.pub
```

Generate a valid ES256 Private and PublicKey

```shell
openssl ecparam -name prime256v1 -genkey -noout -out ed25519.pem
openssl ec -in ed25519.pem -pubout -out ed25519.pem.pub
# Upgrade to newest version needed https://github.com/auth0/java-jwt/issues/270
openssl pkcs8 -topk8 -inform pem -in ssi-private-key.pem -outform pem -nocrypt -out file.pem
```
