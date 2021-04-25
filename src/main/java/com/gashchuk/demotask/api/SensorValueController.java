package com.gashchuk.demotask.api;

import com.gashchuk.demotask.api.dto.ImmutableSensorValue;
import com.gashchuk.demotask.api.dto.ImmutableObjectValue;
import com.gashchuk.demotask.infrastructure.SensorValuesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class SensorValueController {

    private final SensorValuesService historyService;

    public SensorValueController(SensorValuesService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/totalSize")
    public long getHistory() {
        return historyService.getTotalSize();
    }

    @GetMapping("/history")
    public List<ImmutableSensorValue> getHistory(@RequestParam("objectId") int objectId,
                                                 @RequestParam("sensorId") int sensorId,
                                                 @RequestParam("from") long timeFrom,
                                                 @RequestParam("to") long timeTo) {
        var list = historyService.getHistory(objectId, sensorId, timeFrom, timeTo);
        return list.stream().map(it -> new ImmutableSensorValue(it.getTime(), it.getValue())).collect(Collectors.toList());
    }

    @GetMapping("/latest")
    public List<ImmutableSensorValue> getLatest(@RequestParam("objectId") int objectId) {
        return historyService.getLatest(objectId).stream().map(it -> new ImmutableSensorValue(it.getTime(), it.getValue())).collect(Collectors.toList());
    }

    @GetMapping("/avg")
    public List<ImmutableSensorValue> getAverage() {
        return historyService.getAverage().entrySet().stream().map(it -> new ImmutableSensorValue(it.getKey(), it.getValue())).collect(Collectors.toList());
    }

    @PostMapping("/save")
    public void save(@RequestBody List<ImmutableObjectValue> data) {
        historyService.insertData(data);
    }

}
