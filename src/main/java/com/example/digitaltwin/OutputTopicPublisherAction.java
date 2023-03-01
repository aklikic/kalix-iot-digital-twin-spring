package com.example.digitaltwin;

import kalix.javasdk.action.Action;
import kalix.springsdk.annotations.Publish;
import kalix.springsdk.annotations.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Subscribe.EventSourcedEntity(value = DigitalTwinEntity.class,ignoreUnknown = true)
public class OutputTopicPublisherAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(OutputTopicPublisherAction.class);
    @Publish.Topic("iot-output")
    public Effect<Model.AlertTriggeredMessage> onAlertTriggeredEvent(Model.AlertTriggeredEvent event){
        logger.info("onAlertTriggeredEvent: {}",event);
        return effects().reply(new Model.AlertTriggeredMessage(event.dtId(),event.metricValue(),event.timestamp()));
    }
    @Publish.Topic("iot-output")
    public Effect<Model.AlertCanceledMessage> onAlertCanceledEvent(Model.AlertCanceledEvent event){
        logger.info("onAlertCanceledEvent: {}",event);
        return effects().reply(new Model.AlertCanceledMessage(event.dtId(),event.timestamp()));
    }
}
