package com.rivange.app.utils;

public final class RankingRules {

    private RankingRules() {
    }

    public static String getRankName(int points) {
        if (points >= 1600) return "Olimpico";
        if (points >= 1300) return "Deportista de elite";
        if (points >= 1100) return "Competidor firme";
        if (points >= 900) return "Jugador en forma";
        return "Novato";
    }
}
