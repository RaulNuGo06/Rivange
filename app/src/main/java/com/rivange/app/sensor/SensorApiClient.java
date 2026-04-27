package com.rivange.app.sensor;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SensorApiClient {

    public interface SensorCallback {
        void onSuccess(JSONObject jsonObject);
        void onError(Exception e, int code);
    }

    private final OkHttpClient client;
    private final String baseUrl;
    private final String bearerToken;

    public SensorApiClient(String baseUrl, String bearerToken) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .writeTimeout(12, TimeUnit.SECONDS)
                .build();
        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
    }

    public void getSensorState(String entityId, SensorCallback callback) {
        String safeBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String url = safeBaseUrl + "/api/states/" + entityId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + bearerToken)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new IOException("Fallo de red: " + e.getMessage(), e), -1);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody body = response.body()) {
                    String bodyText = body != null ? body.string() : "";

                    if (!response.isSuccessful()) {
                        callback.onError(
                                new IOException("HTTP " + response.code() + " - " + bodyText),
                                response.code()
                        );
                        return;
                    }

                    if (bodyText.trim().isEmpty()) {
                        callback.onError(new IOException("Respuesta vacía del sensor"), response.code());
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(bodyText);
                    callback.onSuccess(jsonObject);

                } catch (Exception e) {
                    callback.onError(new IOException("Respuesta inválida: " + e.getMessage(), e), response.code());
                }
            }
        });
    }
}