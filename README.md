# ARENA36 - Services

## Executing example scenario 1 [^1] [^2]

1. Run `mvn clean package -DskipTests` to generate the docker and image and push to the local Docker repository
2. Execute [scenario-demo-1/docker-compose](example/scenario-demo-1/docker-compose.yml) via `docker compose up`

The services are available at:
  - http://localhost:8110 (ProcessFactory)
  - http://localhost:8111 (WorkerManager)
  - A list of the exposed endpoints is available at http://localhost:8110/swagger-ui/index.html
  - A list of the exposed endpoints is available at http://localhost:8111/swagger-ui/index.html

3. Sending a message to the topic `test_topic` (broker available at port 1884) automatically triggers the deployment of the operations in the configured OperationSM.
4. Executing the Operation in the OperationSM deploys and instantiate the process in the Zeebe server

[^1]: Tested under Ubuntu 22.04 + Adoptium JDK21
[^2]: `localhost` is assumed as host.

## Testing

1. Make sure the `ci/docker-compose.yml` is up and running
2. Execute `mvn test` or `mvn verify` for the IT tests.

## Configuring

Please refer to the README of the individual components:

- [processfactory/README](processfactory/README.md)
- [workermanager/README](workermanager/README.md)

## Appendix

### Camunda 

Relevant UI endpoints:

- [Operate](http://localhost:8081)
- [Tasklist](http://localhost:8082)

Username/Password: demo/demo