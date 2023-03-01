package com.example.digitaltwin;

import kalix.javasdk.action.Action;
import kalix.springsdk.KalixClient;
import kalix.springsdk.annotations.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Subscribe.Topic("iot-input")
public class InputTopicSubscriberAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(InputTopicSubscriberAction.class);
    private final KalixClient client;

    public InputTopicSubscriberAction(KalixClient client) {
        this.client = client;
    }

    public Effect<String> onMetricAddMessage(Model.MetricAddMessage message){
        logger.info("onMetricAddMessage: {}",message);
        var call = client.post("/dt/%s/add-metric".formatted(message.dtId()),new Model.MetricAddRequest(message.metricValue()),String.class).execute();
        return effects().asyncReply(call);
    }
}
