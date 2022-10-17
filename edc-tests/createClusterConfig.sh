export MAVEN_REPOSITORY=$(./mvnw help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
cat << EOF > kind.config.yaml
---
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  extraMounts:
    - hostPath: ${PWD}
      containerPath: /srv/product-edc
    - hostPath: ${MAVEN_REPOSITORY}
      containerPath: /srv/m2-repository
    - hostPath: /var/lib/containerd/io.containerd.snapshotter.v1.overlayfs
      containerPath: /var/lib/containerd/io.containerd.snapshotter.v1.overlayfs
EOF
