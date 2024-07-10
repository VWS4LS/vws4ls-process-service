# ARENA2036 - Services

![](assets/basyx-arena-cd.drawio.svg)

## Executing example scenario 1 [^*] [^**]

1. Run `mvn clean package -DskipTests` to generate the docker and image and push to the local Docker repository
2. Execute [scenario-demo-1/docker-compose](example/scenario-demo-1/docker-compose.yml) via `docker compose up`

The services are available at:

- http://localhost:8110 (ProcessFactory)
  - A list of the exposed endpoints is available at http://localhost:8110/swagger-ui/index.html
- http://localhost:8111 (WorkerManager)
  - A list of the exposed endpoints is available at http://localhost:8111/swagger-ui/index.html

- Sending a message to the topic `deploy_operation` (broker available at port 1884) automatically triggers the deployment of the operations in the configured OperationSM.
- Executing the Operation in the OperationSM deploys and instantiate the process in the Zeebe server
- Sending a message to the topic `update_skills` (re)deploys all Zeebe Job workers based on all operations found with the qualifier::type="skill-provider". The Zeebe Job Worker is associated with the type of the corresponding qualifier::value.

[^*]: Tested under Ubuntu 22.04 + Adoptium JDK21
[^**]: `localhost` is assumed as host.

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