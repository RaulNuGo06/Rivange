package com.rivange.app.sensor;

import org.json.JSONObject;

public class SensorRepository {

    public interface RepositoryCallback {
        void onSuccess(SensorReading reading);
        void onError(String message);
    }

    private final SensorApiClient apiClient;

    public SensorRepository(SensorApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void loadEnvironment(String tempEntity, String humidityEntity, RepositoryCallback callback) {
        apiClient.getSensorState(tempEntity, new SensorApiClient.SensorCallback() {
            @Override
            public void onSuccess(JSONObject tempJson) {
                apiClient.getSensorState(humidityEntity, new SensorApiClient.SensorCallback() {
                    @Override
                    public void onSuccess(JSONObject humidityJson) {
                        try {
                            double temperature = parseState(tempJson, "temperatura");
                            double humidity = parseState(humidityJson, "humedad");
                            double heatIndex = HeatIndexCalculator.calculate(temperature, humidity);

                            SensorReading reading = new SensorReading(
                                    temperature,
                                    humidity,
                                    heatIndex,
                                    true,
                                    "home_assistant"
                            );

                            callback.onSuccess(reading);

                        } catch (Exception e) {
                            callback.onError("Error procesando sensor: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Exception e, int code) {
                        callback.onError("Error leyendo humedad: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception e, int code) {
                callback.onError("Error leyendo temperatura: " + e.getMessage());
            }
        });
    }

    private double parseState(JSONObject jsonObject, String label) throws Exception {
        if (jsonObject == null) {
            throw new Exception("Respuesta nula en " + label);
        }

        String state = jsonObject.optString("state", "").replace(",", ".");

        if (state.isEmpty()) {
            throw new Exception("Estado vacío en " + label);
        }

        if ("unknown".equalsIgnoreCase(state) || "unavailable".equalsIgnoreCase(state)) {
            throw new Exception("Dato no disponible en " + label + ": " + state);
        }

        return Double.parseDouble(state);
    }
}