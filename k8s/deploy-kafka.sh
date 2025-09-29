helm upgrade --install kafka oci://registry-1.docker.io/bitnamicharts/kafka \
  -n kafka \
  -f k8s/kafka-values.yml \
  --set image.registry=docker.io \
  --set image.repository=bitnamilegacy/kafka \
  --set image.tag=4.0.0-debian-12-r10 \
  --set global.security.allowInsecureImages=true
