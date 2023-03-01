package com.example.digitaltwin;

import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.eventsourcedentity.EventSourcedEntityContext;
import kalix.springsdk.annotations.EntityKey;
import kalix.springsdk.annotations.EntityType;
import kalix.springsdk.annotations.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@EntityKey("dtId")
@EntityType("digitaltwin")
@RequestMapping("/dt/{dtId}")
public class DigitalTwinEntity extends EventSourcedEntity<Model.InstanceState> {

    private static final Logger logger = LoggerFactory.getLogger(DigitalTwinEntity.class);
    private final String dtId;

    public DigitalTwinEntity(EventSourcedEntityContext context) {
        this.dtId = context.entityId();
        logger.info(Model.MetricAddMessage.class.getSimpleName());
    }

    @Override
    public Model.InstanceState emptyState() {
        return new Model.InstanceState(null,0,false);
    }

    @PostMapping("/provision")
    public Effect<String> provision(@RequestBody Model.ProvisionRequest request){
        logger.info("provision: {}/{}",dtId,request);
        if(currentState().name() == null){
            var event = new Model.ProvisionedEvent(dtId, request.name(), request.metricValueAlertThreshold(), Instant.now());
            return effects().emitEvent(event).thenReply(updatedState -> Model.OK_RESPONSE);
        } else {
            return effects().reply(Model.OK_RESPONSE);
        }
    }

    @PostMapping("/add-metric")
    public Effect<String> addMetric(@RequestBody Model.MetricAddRequest request){
        logger.info("addMetric: {}/{}",dtId,request);
        if(!currentState().alertActive() && request.metricValue() > currentState().metricValueAlertThreshold()){
            var event = new Model.AlertTriggeredEvent(dtId, request.metricValue(), Instant.now());
            return effects().emitEvent(event).thenReply(updatedState -> Model.OK_RESPONSE);
        } else if(currentState().alertActive() && request.metricValue() < currentState().metricValueAlertThreshold()){
            var event = new Model.AlertCanceledEvent(dtId, Instant.now());
            return effects().emitEvent(event).thenReply(updatedState -> Model.OK_RESPONSE);
        }
        return effects().reply(Model.OK_RESPONSE);
    }
    @GetMapping
    public Effect<Model.InstanceState> get(){
        return effects().reply(currentState());
    }

    @EventHandler
    public Model.InstanceState onProvisionedEvent(Model.ProvisionedEvent event){
        return new Model.InstanceState(event.name(), event.metricValueAlertThreshold(), false);
    }
    @EventHandler
    public Model.InstanceState onAlertTriggeredEvent(Model.AlertTriggeredEvent event){
        return new Model.InstanceState(currentState().name(), currentState().metricValueAlertThreshold(), true);
    }
    @EventHandler
    public Model.InstanceState onAlertCanceledEvent(Model.AlertCanceledEvent event){
        return new Model.InstanceState(currentState().name(), currentState().metricValueAlertThreshold(), false);
    }
}
