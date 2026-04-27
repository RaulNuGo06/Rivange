package com.rivange.app.sensor;

public final class HeatIndexCalculator {

    private HeatIndexCalculator() {
    }

    public static double calculate(double temperatureCelsius, double humidityPercent) {
        double t = temperatureCelsius;
        double rh = humidityPercent;

        if (t < 27.0) {
            return round(t);
        }

        double hi = -8.784695
                + 1.61139411 * t
                + 2.338549 * rh
                - 0.14611605 * t * rh
                - 0.012308094 * t * t
                - 0.016424828 * rh * rh
                + 0.002211732 * t * t * rh
                + 0.00072546 * t * rh * rh
                - 0.000003582 * t * t * rh * rh;

        return round(hi);
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}