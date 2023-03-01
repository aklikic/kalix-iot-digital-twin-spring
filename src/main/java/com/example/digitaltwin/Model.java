package com.example.digitaltwin;

import java.time.Instant;

public interface Model {

    //data to persists (state)
    record InstanceState (String name,
                          int metricValueAlertThreshold,
                          boolean alertActive) implements Model{}

    //events (event sourcing)
    record ProvisionedEvent(String dtId,
                            String name,
                            int metricValueAlertThreshold,
                            Instant timestamp) implements Model{}
    record AlertTriggeredEvent(String dtId,
                               int metricValue,
                               Instant timestamp) implements Model{}
    record AlertCanceledEvent(String dtId,
                         Instant timestamp) implements  Model{}

    //api data
    record ProvisionRequest(String name,
                            int metricValueAlertThreshold) implements Model{}
    record MetricAddRequest(int metricValue) implements Model{}

    //public events (eventing)
    record MetricAddMessage(String dtId, int metricValue) implements Model{}
    record AlertTriggeredMessage(String dtId,
                                 int metricValue,
                                 Instant timestamp) implements Model{}
    record AlertCanceledMessage(String dtId,
                                Instant timestamp) implements  Model{}

    public static String OK_RESPONSE = "OK";
}
