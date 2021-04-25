package com.gashchuk.demotask.infrastructure;

import com.gashchuk.demotask.api.dto.ImmutableObjectValue;
import com.gashchuk.demotask.domain.InMemoryDatabase;
import com.gashchuk.demotask.domain.entity.SensorData;
import org.springframework.stereotype.Service;

import java.util.*;

/*
Service class is designed to be an access point to core database class
 */
@Service
public class SensorValuesService {

    private final InMemoryDatabase database;

    public SensorValuesService(InMemoryDatabase database) {
        this.database = database;
    }

    public long getTotalSize() {
        return database.size();
    }

    public List<SensorData> getHistory(int objectId, int sensorId, long timeFrom, long timeTo) {
        return database.getSensorData(objectId, sensorId, timeFrom, timeTo);
    }

    public List<SensorData> getLatest(int objectId) {
        return database.getLastSensorData(objectId);
    }

    public Map<Integer, Double> getAverage() {
        return database.getAverage();
    }

    public void insertData(List<ImmutableObjectValue> data) {
        for (var entity : data) {
            database.insertIntoDb(entity.getObjectId(), entity.getSensorId(), entity.getTime(), entity.getValue());
        }
    }

}
