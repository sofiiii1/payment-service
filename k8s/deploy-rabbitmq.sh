helm upgrade --install my-rabbit oci://registry-1.docker.io/bitnamicharts/rabbitmq \
  -n rabbit \
  --set auth.username=admin \
  --set auth.password=admin \
  --set auth.erlangCookie=secretcookie \
  --set image.registry=docker.io \
  --set image.repository=bitnamilegacy/rabbitmq \
  --set image.tag=4.1.3-debian-12-r1 \
  --set global.security.allowInsecureImages=true
