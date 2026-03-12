package ru.yandex.practicum.service;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.service.entity.Action;
import ru.yandex.practicum.service.entity.Scenario;

import java.util.Map;

@Slf4j
@Component
public class HubRouterGrpcClient {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub stub;

    public void handleDeviceAction(Scenario scenario, String hubId) {
        Map<String, Action> actions = scenario.getActions();

        DeviceActionRequest request = null;

        for (Map.Entry<String, Action> entry : actions.entrySet()) {
            String sensorId = entry.getKey();
            Action action = entry.getValue();

            request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.valueOf(action.getType()))
                            .setValue(action.getValue())
                            .build())
                    .build();

            try {
                stub.handleDeviceAction(request); // просто вызываем метод
                log.info("Выполнено действие для сценария '{}': sensor={}, action={}",
                        scenario.getName(), sensorId, action.getType());
            } catch (StatusRuntimeException e) {
                log.error("Ошибка выполнения действия для сценария '{}' на датчике {}: {}",
                        scenario.getName(), sensorId, e.getStatus());
            }
        }
    }
}
