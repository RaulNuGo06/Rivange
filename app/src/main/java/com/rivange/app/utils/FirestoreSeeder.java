package com.rivange.app.utils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class FirestoreSeeder {

    public interface SeedCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private FirestoreSeeder() {
    }

    public static void seedDemoData(FirebaseFirestore db, String adminUid, String userUid, SeedCallback callback) {
        WriteBatch batch = db.batch();
        long now = System.currentTimeMillis();

        if (adminUid != null && !adminUid.trim().isEmpty()) {
            Map<String, Object> admin = new HashMap<>();
            admin.put("id", adminUid);
            admin.put("name", "Administrador Rivange");
            admin.put("email", "admin@rivange.com");
            admin.put("role", Constants.ROLE_ADMIN);
            admin.put("phone", "600111111");
            admin.put("profileImageUrl", "");
            admin.put("createdAt", now);

            batch.set(db.collection(Constants.COLLECTION_USERS).document(adminUid), admin);
        }

        if (userUid != null && !userUid.trim().isEmpty()) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", userUid);
            user.put("name", "Usuario Demo");
            user.put("email", "user@rivange.com");
            user.put("role", Constants.ROLE_USER);
            user.put("phone", "600222222");
            user.put("profileImageUrl", "");
            user.put("createdAt", now);

            batch.set(db.collection(Constants.COLLECTION_USERS).document(userUid), user);
        }

        Map<String, Object> referee = new HashMap<>();
        referee.put("id", "ref_demo_1");
        referee.put("name", "Arbitro Demo");
        referee.put("email", "arbitro@rivange.com");
        referee.put("role", Constants.ROLE_REFEREE);
        referee.put("phone", "600333333");
        referee.put("profileImageUrl", "");
        referee.put("createdAt", now);
        batch.set(db.collection(Constants.COLLECTION_USERS).document("ref_demo_1"), referee);

        Map<String, Object> pista1 = new HashMap<>();
        pista1.put("id", "court_1");
        pista1.put("name", "Pista Central");
        pista1.put("sport", "Pádel");
        pista1.put("available", true);
        pista1.put("surface", "Césped artificial");
        pista1.put("imageUrl", "");

        Map<String, Object> pista2 = new HashMap<>();
        pista2.put("id", "court_2");
        pista2.put("name", "Pista Norte");
        pista2.put("sport", "Tenis");
        pista2.put("available", true);
        pista2.put("surface", "Resina");
        pista2.put("imageUrl", "");

        Map<String, Object> pista3 = new HashMap<>();
        pista3.put("id", "court_3");
        pista3.put("name", "Pista Sur");
        pista3.put("sport", "Pádel");
        pista3.put("available", true);
        pista3.put("surface", "Césped artificial");
        pista3.put("imageUrl", "");

        Map<String, Object> pista4 = new HashMap<>();
        pista4.put("id", "court_4");
        pista4.put("name", "Piscina 1");
        pista4.put("sport", "Natacion");
        pista4.put("available", true);
        pista4.put("surface", "Agua");
        pista4.put("imageUrl", "");

        Map<String, Object> pista5 = new HashMap<>();
        pista5.put("id", "court_5");
        pista5.put("name", "Piscina 2");
        pista5.put("sport", "Natacion");
        pista5.put("available", true);
        pista5.put("surface", "Agua");
        pista5.put("imageUrl", "");

        Map<String, Object> pista6 = new HashMap<>();
        pista6.put("id", "court_6");
        pista6.put("name", "Campo Futbol 1");
        pista6.put("sport", "Futbol");
        pista6.put("available", true);
        pista6.put("surface", "Cesped natural");
        pista6.put("imageUrl", "");

        Map<String, Object> pista7 = new HashMap<>();
        pista7.put("id", "court_7");
        pista7.put("name", "Campo Futbol 2");
        pista7.put("sport", "Futbol");
        pista7.put("available", true);
        pista7.put("surface", "Cesped artificial");
        pista7.put("imageUrl", "");

        Map<String, Object> pista8 = new HashMap<>();
        pista8.put("id", "court_8");
        pista8.put("name", "Pista Basket 1");
        pista8.put("sport", "Baloncesto");
        pista8.put("available", true);
        pista8.put("surface", "Parquet");
        pista8.put("imageUrl", "");

        Map<String, Object> pista9 = new HashMap<>();
        pista9.put("id", "court_9");
        pista9.put("name", "Pista Basket 2");
        pista9.put("sport", "Baloncesto");
        pista9.put("available", true);
        pista9.put("surface", "Resina");
        pista9.put("imageUrl", "");

        Map<String, Object> pista10 = new HashMap<>();
        pista10.put("id", "court_10");
        pista10.put("name", "Ring 1");
        pista10.put("sport", "Boxeo");
        pista10.put("available", true);
        pista10.put("surface", "Lona");
        pista10.put("imageUrl", "");

        Map<String, Object> pista11 = new HashMap<>();
        pista11.put("id", "court_11");
        pista11.put("name", "Ring 2");
        pista11.put("sport", "Boxeo");
        pista11.put("available", true);
        pista11.put("surface", "Lona");
        pista11.put("imageUrl", "");

        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_1"), pista1);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_2"), pista2);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_3"), pista3);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_4"), pista4);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_5"), pista5);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_6"), pista6);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_7"), pista7);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_8"), pista8);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_9"), pista9);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_10"), pista10);
        batch.set(db.collection(Constants.COLLECTION_COURTS).document("court_11"), pista11);

        Map<String, Object> partido1 = new HashMap<>();
        partido1.put("id", "match_001");
        partido1.put("title", "Americana de tarde");
        partido1.put("date", "25/04/2026");
        partido1.put("time", "19:00");
        partido1.put("courtName", "Pista Central");
        partido1.put("courtId", "court_1");
        partido1.put("sport", "Padel");
        partido1.put("level", "Intermedio");
        partido1.put("maxPlayers", 4);
        partido1.put("currentPlayers", 2);
        partido1.put("createdBy", userUid != null ? userUid : "demo_user");
        partido1.put("open", true);
        partido1.put("playerIds", java.util.Arrays.asList(
                userUid != null ? userUid : "demo_user",
                adminUid != null ? adminUid : "demo_admin"
        ));
        partido1.put("playerNames", java.util.Arrays.asList("Usuario Demo", "Administrador Rivange"));
        partido1.put("refereeId", "ref_demo_1");
        partido1.put("refereeName", "Arbitro Demo");
        partido1.put("score", "");
        partido1.put("winnerId", "");

        Map<String, Object> partido2 = new HashMap<>();
        partido2.put("id", "match_002");
        partido2.put("title", "Partido rápido");
        partido2.put("date", "26/04/2026");
        partido2.put("time", "18:30");
        partido2.put("courtName", "Pista Norte");
        partido2.put("courtId", "court_2");
        partido2.put("sport", "Tenis");
        partido2.put("level", "Principiante");
        partido2.put("maxPlayers", 2);
        partido2.put("currentPlayers", 1);
        partido2.put("createdBy", adminUid != null ? adminUid : "demo_admin");
        partido2.put("open", true);
        partido2.put("playerIds", java.util.Arrays.asList(adminUid != null ? adminUid : "demo_admin"));
        partido2.put("playerNames", java.util.Arrays.asList("Administrador Rivange"));
        partido2.put("refereeId", "ref_demo_1");
        partido2.put("refereeName", "Arbitro Demo");
        partido2.put("score", "");
        partido2.put("winnerId", "");

        Map<String, Object> partido3 = new HashMap<>();
        partido3.put("id", "match_003");
        partido3.put("title", "Americana nocturna");
        partido3.put("date", "27/04/2026");
        partido3.put("time", "21:00");
        partido3.put("courtName", "Pista Sur");
        partido3.put("courtId", "court_3");
        partido3.put("sport", "Padel");
        partido3.put("level", "Avanzado");
        partido3.put("maxPlayers", 4);
        partido3.put("currentPlayers", 3);
        partido3.put("createdBy", adminUid != null ? adminUid : "demo_admin");
        partido3.put("open", true);
        partido3.put("playerIds", java.util.Arrays.asList(
                adminUid != null ? adminUid : "demo_admin",
                userUid != null ? userUid : "demo_user",
                "demo_user_3"
        ));
        partido3.put("playerNames", java.util.Arrays.asList("Administrador Rivange", "Usuario Demo", "Lucia Fernandez"));
        partido3.put("refereeId", "ref_demo_1");
        partido3.put("refereeName", "Arbitro Demo");
        partido3.put("score", "");
        partido3.put("winnerId", "");

        batch.set(db.collection(Constants.COLLECTION_MATCHES).document("match_001"), partido1);
        batch.set(db.collection(Constants.COLLECTION_MATCHES).document("match_002"), partido2);
        batch.set(db.collection(Constants.COLLECTION_MATCHES).document("match_003"), partido3);

        Map<String, Object> rank1 = new HashMap<>();
        rank1.put("id", "rank_001");
        rank1.put("userId", adminUid != null ? adminUid : "demo_admin");
        rank1.put("userName", "Administrador Rivange");
        rank1.put("points", 1280);
        rank1.put("wins", 18);
        rank1.put("losses", 7);
        rank1.put("position", 1);
        rank1.put("rankName", "Deportista de elite");

        Map<String, Object> rank2 = new HashMap<>();
        rank2.put("id", "rank_002");
        rank2.put("userId", userUid != null ? userUid : "demo_user");
        rank2.put("userName", "Usuario Demo");
        rank2.put("points", 1150);
        rank2.put("wins", 15);
        rank2.put("losses", 8);
        rank2.put("position", 2);
        rank2.put("rankName", "Competidor firme");

        Map<String, Object> rank3 = new HashMap<>();
        rank3.put("id", "rank_003");
        rank3.put("userId", "demo_user_3");
        rank3.put("userName", "Lucía Fernández");
        rank3.put("points", 1040);
        rank3.put("wins", 12);
        rank3.put("losses", 10);
        rank3.put("position", 3);
        rank3.put("rankName", "Competidor firme");

        Map<String, Object> rank4 = new HashMap<>();
        rank4.put("id", "rank_004");
        rank4.put("userId", "demo_user_4");
        rank4.put("userName", "Carlos Vega");
        rank4.put("points", 980);
        rank4.put("wins", 10);
        rank4.put("losses", 11);
        rank4.put("position", 4);
        rank4.put("rankName", "Jugador en forma");

        Map<String, Object> rank5 = new HashMap<>();
        rank5.put("id", "rank_005");
        rank5.put("userId", "demo_user_5");
        rank5.put("userName", "Ana Martín");
        rank5.put("points", 920);
        rank5.put("wins", 9);
        rank5.put("losses", 13);
        rank5.put("position", 5);
        rank5.put("rankName", "Jugador en forma");

        batch.set(db.collection(Constants.COLLECTION_RANKING).document("rank_001"), rank1);
        batch.set(db.collection(Constants.COLLECTION_RANKING).document("rank_002"), rank2);
        batch.set(db.collection(Constants.COLLECTION_RANKING).document("rank_003"), rank3);
        batch.set(db.collection(Constants.COLLECTION_RANKING).document("rank_004"), rank4);
        batch.set(db.collection(Constants.COLLECTION_RANKING).document("rank_005"), rank5);

        Map<String, Object> chatGeneral = new HashMap<>();
        chatGeneral.put("id", "chat_general");
        chatGeneral.put("name", "Chat general");
        chatGeneral.put("lastMessage", "Nos vemos esta tarde");
        chatGeneral.put("lastMessageAt", now);
        chatGeneral.put("lastSenderId", userUid != null ? userUid : "demo_user");

        batch.set(db.collection(Constants.COLLECTION_CHATS).document("chat_general"), chatGeneral);

        Map<String, Object> msg1 = new HashMap<>();
        msg1.put("id", "msg_001");
        msg1.put("chatId", "chat_general");
        msg1.put("senderId", userUid != null ? userUid : "demo_user");
        msg1.put("senderName", "Usuario Demo");
        msg1.put("text", "¿Quién juega mañana por la tarde?");
        msg1.put("createdAt", now - 300000);

        Map<String, Object> msg2 = new HashMap<>();
        msg2.put("id", "msg_002");
        msg2.put("chatId", "chat_general");
        msg2.put("senderId", adminUid != null ? adminUid : "demo_admin");
        msg2.put("senderName", "Administrador Rivange");
        msg2.put("text", "Yo puedo reservar la pista central.");
        msg2.put("createdAt", now - 200000);

        Map<String, Object> msg3 = new HashMap<>();
        msg3.put("id", "msg_003");
        msg3.put("chatId", "chat_general");
        msg3.put("senderId", userUid != null ? userUid : "demo_user");
        msg3.put("senderName", "Usuario Demo");
        msg3.put("text", "Perfecto, entonces me apunto.");
        msg3.put("createdAt", now - 100000);

        batch.set(db.collection(Constants.COLLECTION_MESSAGES).document("msg_001"), msg1);
        batch.set(db.collection(Constants.COLLECTION_MESSAGES).document("msg_002"), msg2);
        batch.set(db.collection(Constants.COLLECTION_MESSAGES).document("msg_003"), msg3);

        if (userUid != null && !userUid.trim().isEmpty()) {
            Map<String, Object> reserva1 = new HashMap<>();
            reserva1.put("id", "res_001");
            reserva1.put("userId", userUid);
            reserva1.put("userName", "Usuario Demo");
            reserva1.put("courtId", "court_1");
            reserva1.put("courtName", "Pista Central");
            reserva1.put("sport", "Padel");
            reserva1.put("date", "25/04/2026");
            reserva1.put("startTime", "18:00");
            reserva1.put("endTime", "19:30");
            reserva1.put("status", "Activa");
            reserva1.put("createdAt", now);

            Map<String, Object> reserva2 = new HashMap<>();
            reserva2.put("id", "res_002");
            reserva2.put("userId", userUid);
            reserva2.put("userName", "Usuario Demo");
            reserva2.put("courtId", "court_3");
            reserva2.put("courtName", "Pista Sur");
            reserva2.put("sport", "Padel");
            reserva2.put("date", "28/04/2026");
            reserva2.put("startTime", "20:00");
            reserva2.put("endTime", "21:00");
            reserva2.put("status", "Activa");
            reserva2.put("createdAt", now);

            batch.set(db.collection(Constants.COLLECTION_RESERVATIONS).document("res_001"), reserva1);
            batch.set(db.collection(Constants.COLLECTION_RESERVATIONS).document("res_002"), reserva2);
        }

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }
}
