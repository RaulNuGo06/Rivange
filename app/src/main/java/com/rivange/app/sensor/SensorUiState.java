package com.rivange.app.sensor;

public class SensorUiState {

    private final String label;
    private final int badgeColorRes;
    private final int textColorRes;

    public SensorUiState(String label, int badgeColorRes, int textColorRes) {
        this.label = label;
        this.badgeColorRes = badgeColorRes;
        this.textColorRes = textColorRes;
    }

    public String getLabel() {
        return label;
    }

    public int getBadgeColorRes() {
        return badgeColorRes;
    }

    public int getTextColorRes() {
        return textColorRes;
    }
}