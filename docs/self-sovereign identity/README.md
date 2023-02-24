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
