helm upgrade --install my-pg oci://registry-1.docker.io/bitnamicharts/postgresql \
  -n db \
  --set auth.postgresPassword=secret \
  --set auth.database=payment-db \
  --set primary.persistence.size=8Gi \
  --set image.registry=docker.io \
  --set image.repository=bitnamilegacy/postgresql \
  --set image.tag=17.6.0-debian-12-r4 \
  --set global.security.allowInsecureImages=true

sleep 10

kubectl describe po my-pg-postgresql-0 -n db
