package com.example.digitaltwin;

import kalix.springsdk.testkit.EventSourcedTestKit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class EntityUnitTest {

    @Test
    public void happyPath(){
        var dtId = UUID.randomUUID().toString();
        var name = "myName";
        var metricValueAlertThreshold = 10;
        var metricValueLow = 5;
        var metricValueHigh = 11;

        var testKit = EventSourcedTestKit.of(dtId, DigitalTwinEntity::new);

        //provision
        var provisionRequest = new Model.ProvisionRequest(name, metricValueAlertThreshold);
        var provisionResult = testKit.call(entity -> entity.provision(provisionRequest));
        assertTrue(provisionResult.didEmitEvents());
        provisionResult.getNextEventOfType(Model.ProvisionedEvent.class);
        var state = (Model.InstanceState)provisionResult.getUpdatedState();
        assertEquals(name,state.name());

        //add low metric value and do not expect any events emitted
        var addMetricResult = testKit.call(entity -> entity.addMetric(new Model.MetricAddRequest(metricValueLow)));
        assertFalse(addMetricResult.didEmitEvents());

        //add high metric value and expect AlertTriggeredEvent emitted and alertActive true
        addMetricResult = testKit.call(entity -> entity.addMetric(new Model.MetricAddRequest(metricValueHigh)));
        assertTrue(addMetricResult.didEmitEvents());
        addMetricResult.getNextEventOfType(Model.AlertTriggeredEvent.class);
        state = (Model.InstanceState)addMetricResult.getUpdatedState();
        assertTrue(state.alertActive());

        //add low metric value and expect AlertCanceledEvent emitted and alertActive false
        addMetricResult = testKit.call(entity -> entity.addMetric(new Model.MetricAddRequest(metricValueLow)));
        assertTrue(addMetricResult.didEmitEvents());
        addMetricResult.getNextEventOfType(Model.AlertCanceledEvent.class);
        state = (Model.InstanceState)addMetricResult.getUpdatedState();
        assertFalse(state.alertActive());

    }
}
