package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.model.DeviceEvent;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
@Controller
@RequestMapping(path = "/events/")
@RequiredArgsConstructor
public class CollectorController {
    private final CollectorService collectorService;

    @PostMapping("sensors")
    public ResponseEntity<Void> getSensors(@RequestBody @Valid SensorEvent sensorEvent) {
        collectorService.getSensors(sensorEvent);
        return ResponseEntity.ok(null);
    }

    @PostMapping("hubs")
    public ResponseEntity<Void> getHubs(@RequestBody @Valid DeviceEvent deviceEvent) {
        collectorService.getHubs(deviceEvent);
        return ResponseEntity.ok(null);
    }
}
