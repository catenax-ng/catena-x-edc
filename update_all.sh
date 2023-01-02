#!/bin/bash

#set -x -o xtrace

minikube delete
minikube start

# CREATE NEW IMAGE
./mvnw spotless:apply package -Pwith-docker-image

minikube image load edc-controlplane-postgresql-hashicorp-vault:latest
minikube image load edc-dataplane-hashicorp-vault:latest
minikube image ls | grep edc

# INFRA
rm -rf edc-tests/src/main/resources/deployment/helm/supporting-infrastructure/charts
helm dependency update edc-tests/src/main/resources/deployment/helm/supporting-infrastructure
helm install cx-infra --namespace cx --create-namespace edc-tests/src/main/resources/deployment/helm/supporting-infrastructure

# PLATO
helm install cx-plato --namespace cx --create-namespace charts/tractusx-connector \
    --set fullnameOverride=plato \
    --set controlplane.service.type=NodePort \
    --set controlplane.endpoints.data.authKey=password \
    --set controlplane.image.tag=latest \
    --set controlplane.image.pullPolicy=Never \
    --set controlplane.image.repository=docker.io/library/edc-controlplane-postgresql-hashicorp-vault \
    --set controlplane.debug.enabled=true \
    --set controlplane.suspendOnStart=false \
    --set dataplane.image.tag=latest \
    --set dataplane.image.pullPolicy=Never \
    --set dataplane.image.repository=docker.io/library/edc-dataplane-hashicorp-vault \
    --set dataplane.debug.enabled=true \
    --set dataplane.suspendOnStart=false \
    --set dataplane.aws.endpointOverride=http://minio:9000 \
    --set dataplane.aws.secretAccessKey=platoqwerty123 \
    --set dataplane.aws.accessKeyId=platoqwerty123 \
    --set backendService.service.type=NodePort \
    --set postgresql.enabled=true \
    --set postgresql.username=user \
    --set postgresql.password=password \
    --set postgresql.jdbcUrl=jdbc:postgresql://plato-postgresql:5432/edc \
    --set vault.hashicorp.enabled=true \
    --set vault.hashicorp.url=http://vault:8200 \
    --set vault.hashicorp.token=root \
    --set vault.secretNames.transferProxyTokenSignerPublicKey=plato/daps/my-plato-daps-crt \
    --set vault.secretNames.transferProxyTokenSignerPrivateKey=plato/daps/my-plato-daps-key \
    --set vault.secretNames.transferProxyTokenEncryptionAesKey=plato/data-encryption-aes-keys \
    --set vault.secretNames.dapsPrivateKey=plato/daps/my-plato-daps-key \
    --set vault.secretNames.dapsPublicKey=plato/daps/my-plato-daps-crt \
    --set daps.url=http://ids-daps:4567 \
    --set daps.clientId=99:83:A7:17:86:FF:98:93:CE:A0:DD:A1:F1:36:FA:F6:0F:75:0A:23:keyid:99:83:A7:17:86:FF:98:93:CE:A0:DD:A1:F1:36:FA:F6:0F:75:0A:23 \
    --set backendService.httpProxyTokenReceiverUrl=http://backend:8080

# SOKRATES
helm install cx-sokrates --namespace cx --create-namespace charts/tractusx-connector \
    --set fullnameOverride=sokrates \
    --set controlplane.service.type=NodePort \
    --set controlplane.endpoints.data.authKey=password \
    --set controlplane.image.tag=latest \
    --set controlplane.image.pullPolicy=Never \
    --set controlplane.image.repository=docker.io/library/edc-controlplane-postgresql-hashicorp-vault \
    --set controlplane.debug.enabled=true \
    --set controlplane.suspendOnStart=false \
    --set dataplane.image.tag=latest \
    --set dataplane.image.pullPolicy=Never \
    --set dataplane.image.repository=docker.io/library/edc-dataplane-hashicorp-vault \
    --set dataplane.debug.enabled=true \
    --set dataplane.suspendOnStart=false \
    --set dataplane.aws.endpointOverride=http://minio:9000 \
    --set dataplane.aws.secretAccessKey=platoqwerty123 \
    --set dataplane.aws.accessKeyId=platoqwerty123 \
    --set backendService.service.type=NodePort \
    --set postgresql.enabled=true \
    --set postgresql.username=user \
    --set postgresql.password=password \
    --set postgresql.jdbcUrl=jdbc:postgresql://sokrates-postgresql:5432/edc \
    --set vault.hashicorp.enabled=true \
    --set vault.hashicorp.url=http://vault:8200 \
    --set vault.hashicorp.token=root \
    --set vault.secretNames.transferProxyTokenSignerPublicKey=sokrates/daps/my-sokrates-daps-crt \
    --set vault.secretNames.transferProxyTokenSignerPrivateKey=sokrates/daps/my-sokrates-daps-key \
    --set vault.secretNames.transferProxyTokenEncryptionAesKey=sokrates/data-encryption-aes-keys \
    --set vault.secretNames.dapsPrivateKey=sokrates/daps/my-sokrates-daps-key \
    --set vault.secretNames.dapsPublicKey=sokrates/daps/my-sokrates-daps-crt \
    --set daps.url=http://ids-daps:4567 \
    --set daps.clientId=E7:07:2D:74:56:66:31:F0:7B:10:EA:B6:03:06:4C:23:7F:ED:A6:65:keyid:E7:07:2D:74:56:66:31:F0:7B:10:EA:B6:03:06:4C:23:7F:ED:A6:65 \
    --set backendService.httpProxyTokenReceiverUrl=http://backend:8080

export EDC_AWS_ENDPOINT_OVERRIDE=$(minikube service minio -n cx --url)

export PLATO_DATA_MANAGEMENT_URL=$(minikube service plato-controlplane -n cx --url | sed -n 3p)
export PLATO_IDS_URL="http://plato-controlplane:8084"
export BACKEND=$(minikube service backend -n cx --url | sed -n 2p)

export SOKRATES_DATA_MANAGEMENT_URL=$(minikube service sokrates-controlplane -n cx --url | sed -n 3p)

echo "PLATO_DATA_MANAGEMENT_URL $PLATO_DATA_MANAGEMENT_URL"
echo "PLATO_IDS_URL $PLATO_IDS_URL"
echo "SOKRATES_DATA_MANAGEMENT_URL $SOKRATES_DATA_MANAGEMENT_URL"
echo "BACKEND $BACKEND"