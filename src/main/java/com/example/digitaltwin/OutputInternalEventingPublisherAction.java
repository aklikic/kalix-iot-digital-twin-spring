package com.example.digitaltwin;

import kalix.javasdk.action.Action;
import kalix.springsdk.annotations.Publish;
import kalix.springsdk.annotations.Subscribe;

@Subscribe.EventSourcedEntity(value = DigitalTwinEntity.class,ignoreUnknown = true)
@Publish.Stream(id = "output-stream")
public class OutputInternalEventingPublisherAction extends Action {

    public Effect<Model.AlertTriggeredMessage> onAlertTriggeredEvent(Model.AlertTriggeredEvent event){
        return effects().reply(new Model.AlertTriggeredMessage(event.dtId(),event.metricValue(),event.timestamp()));
    }
    public Effect<Model.AlertCanceledMessage> onAlertCanceledEvent(Model.AlertCanceledEvent event){
        return effects().reply(new Model.AlertCanceledMessage(event.dtId(),event.timestamp()));
    }
}
