# Arena - ProcessFactory

- Listens to the MQTT Event 'E1'; when it arrives, do:
  - Reads the BPMN process template from AAS/SM 'Process/CurrentProcess' (attachment of a fileSE)
  - Generates the operation 'startProcess' in AAS/SM/SMC 'Process/CurrentProcess/Operations'
    - When the operation is executed, the BPMN process should be submitted to the BPMN Engine (Camunda)

## CI

### Camunda 

Relevant UI endpoints:

- (Operate)[http://localhost:8081]
- (Tasklist)[http://localhost:8082]

Username/Password: demo/demo