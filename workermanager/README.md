# Arena - WorkerManager 

- Listens to any MQTT message on the topic `${mqtt.topic}` from BaSyx, when it arrives, do:
  - Instantiates a new Camunda Worker for the specific device capability.
    - For each OperationSE with a qualifer (`matched-qualifier`) of type `${basyx.qualifier.skillprovider}` do:
      - Instantiate a REST camunda worker of id `matched-qualifier::value`

```mermaid
sequenceDiagram
    participant MQTT-Broker
    participant WorkerManager
    participant BaSyx
    participant BPMNEngine as BPMN Engine
    
    MQTT-Broker ->> WorkerManager: Message Arrives at topic ${mqtt.topic}
    activate WorkerManager
    WorkerManager ->> BaSyx: Query for OperationSE with a qualifier of type ${basyx.qualifier.skillprovider}   
    activate BaSyx
    BaSyx -->> WorkerManager: result
    deactivate BaSyx
    WorkerManager ->> WorkerManager: Filter for skillProviders
    activate WorkerManager
    WorkerManager ->> BPMNEngine: Instantiate workers based on matched-qualifier::value [if not present]
    activate BPMNEngine
    deactivate BPMNEngine
    deactivate WorkerManager 
    deactivate WorkerManager
```

# Requirements

- It shall react to changes in the AAS. E.g.:
  - If the target AAS is deleted, the camunda worker shall be stopped
  - If the job type changes, the worker shall be restarted with a new job


## Configuring

An example configuration file is found at the [example/scenario-demo-1](../example/scenario-demo-1/config/processfactory.properties). All relevant properties can be easily changed there, e.g. MQTT, Zeebe, BaSyx. 

| property | defaultValue | description |
| -- | -- | -- |
| mqtt.topic | testTopic | MQTT topic to listen to |
| basyx.aasrepositoryUrl | - | AasRepository to realize queries |
| basyx.qualifier.skillprovider | bpmn-service | OperationSE qualifier for skill providers |
| camunda.zeebeGateway | - | Zeebe gRPC endpoint |