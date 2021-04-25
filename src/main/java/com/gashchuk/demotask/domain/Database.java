package com.gashchuk.demotask.domain;

import com.gashchuk.demotask.domain.entity.SensorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Database {

    long size();

    void cleanDb();

    List<SensorData> getLastSensorData(int objectId);

    ArrayList<SensorData> getSensorData(int objectId, int sensorId, long timeFrom, long timeTo);

    Map<Integer, Double> getAverage();

    void insertIntoDb(int objectId, int sensorId, long time, double value);
}
