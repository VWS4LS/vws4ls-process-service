# Arena - ProcessFactory

1. Listens to any message in the topic `topic`; when it arrives, do:
  1. Reads the BPMN process template from SM/SE 'ubmodel/ProcessFileSE' (attachment of a fileSE)
  2. Generates the operation 'deployProcess' in 'OperationsSubmodel'
  3. When the operation is executed in BaSyx, the BPMN process is deployed and instantiated to the BPMN Engine
  
```mermaid
sequenceDiagram
    participant MQTT-Broker
    participant ProcessFactory
    participant BaSyx
    participant BPMNEngine as BPMN Engine
    actor User
    
    MQTT-Broker ->> ProcessFactory: Message Arrives at topic `topic`
    activate ProcessFactory
    ProcessFactory ->> BaSyx: Read BPMN process template
    activate BaSyx
    BaSyx -->> ProcessFactory: Return Process Template
    deactivate BaSyx
    ProcessFactory ->> BaSyx: Generate 'deployProcess' operation
    activate BaSyx
    deactivate BaSyx
    deactivate ProcessFactory
    User ->> BaSyx: Execute 'deployProcess' operation
    activate BaSyx 
    BaSyx ->> ProcessFactory: Delegate operation
    activate ProcessFactory 
    ProcessFactory ->> BPMNEngine: Deploy and instantiate BPMN process
    activate BPMNEngine
    deactivate BPMNEngine
    ProcessFactory -->> BaSyx: return
    deactivate ProcessFactory
    BaSyx-->>User: return
    deactivate BaSyx
```

## Configuring

An example configuration file is found at the [example/scenario-demo-1](../example/scenario-demo-1/config/processfactory.properties). All relevant properties can be easily changed there, e.g. MQTT, Zeebe, BaSyx. 

| property | defaultValue | description |
| -- | -- | -- |
| server.port | 8110 | Port to start the service | 
| server.externalUrl | http://localhost:8110 | URL for external services accessing this service | 
| mqtt.hostname | - | MQTT Broker hostname |
| mqtt.port | - | MQTT Broker port |
| mqtt.username | - | MQTT Broker username |
| mqtt.password | - | MQTT Broker password |
| mqtt.topic | - | MQTT topic to listen to |
| basyx.processSmUrl | - | Base URL pointing to the ProcessSubmodel  |
| basyx.processFileSEIdShort | - | IdShort from the SE, from which the BPMN file is read |
| basyx.operationsSmUrl | - | Base URL pointing to the OperationsSubmodel. This is where this service's operations are going to be deployed |
| camunda.zeebeGateway | - | Zeebe gRPC endpoint |
| camunda.managedProcessPath | /tmp | Server path where BPMN files are stored |