package com.rivange.app.sensor;

import com.rivange.app.R;

public final class SensorUiMapper {

    private SensorUiMapper() {
    }

    public static SensorUiState fromReading(SensorReading reading) {
        if (reading == null || !reading.isValid()) {
            return new SensorUiState(
                    "Sin datos",
                    R.color.sensor_neutral,
                    R.color.white
            );
        }

        double index = reading.getHeatIndex();

        if (index < 26) {
            return new SensorUiState("Óptimo", R.color.sensor_good, R.color.white);
        } else if (index < 31) {
            return new SensorUiState("Cálido", R.color.sensor_warning, R.color.white);
        } else {
            return new SensorUiState("Alto", R.color.sensor_danger, R.color.white);
        }
    }
}