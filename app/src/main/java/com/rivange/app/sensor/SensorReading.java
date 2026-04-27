package com.rivange.app.sensor;

public class SensorReading {

    private double temperature;
    private double humidity;
    private double heatIndex;
    private boolean valid;
    private String source;

    public SensorReading() {
    }

    public SensorReading(double temperature, double humidity, double heatIndex, boolean valid, String source) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.heatIndex = heatIndex;
        this.valid = valid;
        this.source = source;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getHeatIndex() {
        return heatIndex;
    }

    public void setHeatIndex(double heatIndex) {
        this.heatIndex = heatIndex;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getSource() {
        return source == null ? "" : source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}