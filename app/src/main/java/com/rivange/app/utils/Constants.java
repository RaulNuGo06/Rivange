package com.rivange.app.utils;

public final class Constants {

    private Constants() {
    }

    public static final String COLLECTION_USERS = "usuarios";
    public static final String COLLECTION_RESERVATIONS = "reservas";
    public static final String COLLECTION_MATCHES = "partidos";
    public static final String COLLECTION_CHATS = "chats";
    public static final String COLLECTION_MESSAGES = "mensajes";
    public static final String COLLECTION_COURTS = "pistas";
    public static final String COLLECTION_RANKING = "ranking";

    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_REFEREE = "arbitro";

    public static final String EXTRA_CHAT_ID = "extra_chat_id";
    public static final String EXTRA_CHAT_NAME = "extra_chat_name";
    public static final String EXTRA_RESERVATION_ID = "extra_reservation_id";

    // Sensores reales de Zafra
    public static final String SENSOR_TEMP_ENTITY = "sensor.hp1000se_pro_pro_v1_9_0_feels_like_temperature";
    public static final String SENSOR_HUMIDITY_ENTITY = "sensor.hp1000se_pro_pro_v1_9_0_humidity";

    public static final String DEFAULT_PROFILE_IMAGE = "";

    public static final int POINTS_WIN = 25;
    public static final int POINTS_LOSS = -10;
}
