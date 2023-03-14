#!/bin/bash

#set -x -o xtrace

minikube delete
minikube start

# CREATE NEW IMAGE
docker image rm edc-dataplane-hashicorp-vault:latest
docker image rm edc-controlplane-postgresql-hashicorp-vault:latest
./mvnw spotless:apply clean package -Pwith-docker-image -DskipTests

minikube image rm edc-controlplane-postgresql-hashicorp-vault:latest
minikube image load edc-controlplane-postgresql-hashicorp-vault:latest
minikube image rm edc-dataplane-hashicorp-vault:latest
minikube image load edc-dataplane-hashicorp-vault:latest
minikube image ls | grep edc

# INFRA
rm -rf edc-tests/src/main/resources/deployment/helm/supporting-infrastructure/charts
helm uninstall cx-infra --namespace cx
helm dependency update edc-tests/src/main/resources/deployment/helm/supporting-infrastructure
helm install cx-infra --namespace cx --create-namespace edc-tests/src/main/resources/deployment/helm/supporting-infrastructure \
    --set install.minio=false

# PLATO
helm uninstall cx-plato --namespace cx
helm install cx-plato --namespace cx --create-namespace charts/tractusx-connector \
    --set fullnameOverride=plato \
    --set controlplane.service.type=NodePort \
    --set controlplane.endpoints.data.authKey=password \
    --set controlplane.image.tag=latest \
    --set controlplane.image.pullPolicy=Never \
    --set controlplane.image.repository=docker.io/library/edc-controlplane-postgresql-hashicorp-vault \
    --set controlplane.debug.enabled=true \
    --set controlplane.debug.suspendOnStart=true \
    --set controlplane.livenessProbe.enabled=false \
    --set controlplane.readinessProbe.enabled=false \
    --set dataplane.image.tag=latest \
    --set dataplane.image.pullPolicy=Never \
    --set dataplane.image.repository=docker.io/library/edc-dataplane-hashicorp-vault \
    --set dataplane.debug.enabled=true \
    --set dataplane.debug.suspendOnStart=false \
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
    --set backendService.httpProxyTokenReceiverUrl=http://backend:8080

# SOKRATES
helm uninstall cx-sokrates --namespace cx
helm install cx-sokrates --namespace cx --create-namespace charts/tractusx-connector \
    --set fullnameOverride=sokrates \
    --set controlplane.service.type=NodePort \
    --set controlplane.endpoints.data.authKey=password \
    --set controlplane.image.tag=latest \
    --set controlplane.image.pullPolicy=Never \
    --set controlplane.image.repository=docker.io/library/edc-controlplane-postgresql-hashicorp-vault \
    --set controlplane.debug.enabled=true \
    --set controlplane.debug.suspendOnStart=false \
    --set dataplane.image.tag=latest \
    --set dataplane.image.pullPolicy=Never \
    --set dataplane.image.repository=docker.io/library/edc-dataplane-hashicorp-vault \
    --set dataplane.debug.enabled=true \
    --set dataplane.debug.suspendOnStart=false \
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
    --set backendService.httpProxyTokenReceiverUrl=http://backend:8080

#export EDC_AWS_ENDPOINT_OVERRIDE=$(minikube service minio -n cx --url)

export PLATO_DATA_MANAGEMENT_URL=$(minikube service plato-controlplane -n cx --url | sed -n 3p)
export PLATO_IDS_URL="http://plato-controlplane:8084"
export BACKEND=$(minikube service backend -n cx --url | sed -n 2p)
export SOKRATES_DATA_MANAGEMENT_URL=$(minikube service sokrates-controlplane -n cx --url | sed -n 3p)

echo "PLATO_DATA_MANAGEMENT_URL $PLATO_DATA_MANAGEMENT_URL"
echo "PLATO_IDS_URL $PLATO_IDS_URL"
echo "SOKRATES_DATA_MANAGEMENT_URL $SOKRATES_DATA_MANAGEMENT_URL"
echo "BACKEND $BACKEND"
