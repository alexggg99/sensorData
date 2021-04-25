package com.gashchuk.demotask.domain.entity;

public class SensorData {
    private long time;
    private double value;

    private ObjectSensorKey key;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public ObjectSensorKey getKey() {
        return key;
    }

    public void setKey(ObjectSensorKey key) {
        this.key = key;
    }
}
