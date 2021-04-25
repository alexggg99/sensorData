package com.gashchuk.demotask.domain.entity;

import java.util.Objects;

public class ObjectSensorKey {
    private int objectId;
    private int sensorId;

    public ObjectSensorKey(int objectId, int sensorId) {
        this.objectId = objectId;
        this.sensorId = sensorId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public int getObjectId() {
        return objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectSensorKey that = (ObjectSensorKey) o;
        return objectId == that.objectId && sensorId == that.sensorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, sensorId);
    }
}
