package com.gashchuk.demotask.api.dto;

public final class ImmutableSensorValue {
    private final long time;
    private final double value;

    public ImmutableSensorValue(long time, double value) {
        this.value = value;
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}
