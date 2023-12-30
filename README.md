# Development

```bash
mvn package
docker build . -t fractalwoodstories/gateway-service:latest
docker push fractalwoodstories/gateway-service:latest
helm upgrade --install gateway-service ./helm/gateway-service
```