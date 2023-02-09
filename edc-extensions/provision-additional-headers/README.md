# Provision: additional headers

The goal of this extension is to provide additional headers to the request to the backend service done by the provider
to retrieve the data to be given to the consumer.

This is done to give the possibility to the provider backend service to audit the data requests.

The following headers are added to the `HttpDataAddress`:
- `Edc-Contract-Id`: the id of the contract agreement
