package com.gashchuk.demotask.api.dto;

public class ImmutableObjectValue {
    private final int objectId;
    private final int sensorId;
    private final long time;
    private final double value;

    public ImmutableObjectValue(int objectId, int sensorId, long time, double value) {
        this.objectId = objectId;
        this.sensorId = sensorId;
        this.value = value;
        this.time = time;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}
