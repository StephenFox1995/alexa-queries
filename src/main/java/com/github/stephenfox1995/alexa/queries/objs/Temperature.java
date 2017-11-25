package com.github.stephenfox1995.alexa.queries.objs;

public class Temperature {
    private float temperature;
    private String timestamp;

    public Temperature(float temperature, String timestamp) {
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
