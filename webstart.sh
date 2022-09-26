podman run \
  --name tomcat-cas \
  --privileged \
  --mount type=bind,source=./webapps,target=/usr/local/tomcat/webapps \
  -p 8080:8080 \
  -d tomcat:9.0
