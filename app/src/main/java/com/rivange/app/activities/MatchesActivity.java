package com.rivange.app.activities;

import android.os.Bundle;
import android.text.InputType;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.rivange.app.R;
import com.rivange.app.adapters.MatchAdapter;
import com.rivange.app.models.Match;
import com.rivange.app.models.RankingEntry;
import com.rivange.app.models.Reservation;
import com.rivange.app.models.User;
import com.rivange.app.utils.BottomNavHelper;
import com.rivange.app.utils.Constants;
import com.rivange.app.utils.DateTimeUtils;
import com.rivange.app.utils.RankingRules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView rvMatches;
    private MatchAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration matchesListener;

    private String currentUserRole = Constants.ROLE_USER;
    private String currentUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid() == null ? "" : auth.getUid();

        rvMatches = findViewById(R.id.rvMatches);
        adapter = new MatchAdapter(this::joinMatch, this::scoreMatch);

        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        rvMatches.setAdapter(adapter);

        BottomNavHelper.setup(this, findViewById(R.id.bottomNavigation), R.id.nav_matches);

        loadCurrentUserRole();
        listenMatches();
    }

    private void loadCurrentUserRole() {
        if (currentUserId.isEmpty()) return;
        db.collection(Constants.COLLECTION_USERS)
                .document(currentUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        currentUserRole = user.getRole();
                    }
                });
    }

    private void listenMatches() {
        matchesListener = db.collection(Constants.COLLECTION_MATCHES)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots == null) {
                        Toast.makeText(this, "Error cargando partidos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Match> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Match match = doc.toObject(Match.class);
                        if (match != null) {
                            match.setId(doc.getId());
                            list.add(match);
                        }
                    }

                    list.sort(Comparator.comparingLong(m ->
                            DateTimeUtils.parseDateTimeMillis(m.getDate(), m.getTime())
                    ));

                    adapter.submitList(list);
                });
    }

    private void joinMatch(Match match) {
        if (match == null || match.getId().isEmpty()) {
            return;
        }
        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        validateReservationForMatch(match, canJoin -> {
            if (!canJoin) {
                Toast.makeText(this, "Para unirte necesitas reserva activa en esa pista y horario", Toast.LENGTH_LONG).show();
                return;
            }
            joinMatchTransaction(match);
        });
    }

    private interface ReservationValidationCallback {
        void onValidated(boolean canJoin);
    }

    private void validateReservationForMatch(Match match, ReservationValidationCallback callback) {
        String courtField = match.getCourtId().isEmpty() ? "courtName" : "courtId";
        String courtValue = match.getCourtId().isEmpty() ? match.getCourtName() : match.getCourtId();

        db.collection(Constants.COLLECTION_RESERVATIONS)
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo(courtField, courtValue)
                .whereEqualTo("date", match.getDate())
                .whereEqualTo("status", "Activa")
                .get()
                .addOnSuccessListener(snapshot -> {
                    boolean valid = false;
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Reservation reservation = doc.toObject(Reservation.class);
                        if (reservation == null) continue;
                        String start = reservation.getStartTime();
                        String end = reservation.getEndTime();
                        String matchTime = match.getTime();
                        if (start.compareTo(matchTime) <= 0 && end.compareTo(matchTime) > 0) {
                            valid = true;
                            break;
                        }
                    }
                    callback.onValidated(valid);
                })
                .addOnFailureListener(e -> callback.onValidated(false));
    }

    private void joinMatchTransaction(Match match) {
        db.runTransaction(transaction -> {
            DocumentReference ref = db.collection(Constants.COLLECTION_MATCHES).document(match.getId());
            DocumentSnapshot snapshot = transaction.get(ref);

            Long currentPlayersLong = snapshot.getLong("currentPlayers");
            Long maxPlayersLong = snapshot.getLong("maxPlayers");
            Boolean open = snapshot.getBoolean("open");
            List<String> playerIds = (List<String>) snapshot.get("playerIds");
            List<String> playerNames = (List<String>) snapshot.get("playerNames");

            int currentPlayers = currentPlayersLong != null ? currentPlayersLong.intValue() : 0;
            int maxPlayers = maxPlayersLong != null ? maxPlayersLong.intValue() : 0;
            boolean isOpen = open != null && open;

            if (playerIds == null) playerIds = new ArrayList<>();
            if (playerNames == null) playerNames = new ArrayList<>();
            if (playerIds.isEmpty() && snapshot.getString("createdBy") != null) {
                playerIds.add(snapshot.getString("createdBy"));
                playerNames.add("Creador");
            }

            if (playerIds.contains(currentUserId)) {
                throw new IllegalStateException("Ya estas dentro del partido");
            }
            if (!isOpen || currentPlayers >= maxPlayers) {
                throw new IllegalStateException("El partido ya esta completo");
            }

            String displayName = "Jugador";
            DocumentSnapshot userSnapshot = transaction.get(db.collection(Constants.COLLECTION_USERS).document(currentUserId));
            User user = userSnapshot.toObject(User.class);
            if (user != null && !user.getName().isEmpty()) {
                displayName = user.getName();
            }

            playerIds.add(currentUserId);
            playerNames.add(displayName);
            int updatedPlayers = playerIds.size();

            transaction.update(ref, "currentPlayers", updatedPlayers);
            transaction.update(ref, "playerIds", playerIds);
            transaction.update(ref, "playerNames", playerNames);

            if (updatedPlayers >= maxPlayers) {
                transaction.update(ref, "open", false);
            }

            return updatedPlayers;
        }).addOnSuccessListener(result ->
                Toast.makeText(this, "Te has unido a " + match.getTitle(), Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(
                        this,
                        e.getMessage() != null ? e.getMessage() : "No se pudo unir al partido",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void scoreMatch(Match match) {
        if (match == null || match.getId().isEmpty()) return;
        if (!match.getScore().isEmpty()) {
            Toast.makeText(this, "Este partido ya tiene marcador", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdmin = Constants.ROLE_ADMIN.equalsIgnoreCase(currentUserRole);
        boolean isRefereeOfMatch = currentUserId.equals(match.getRefereeId());
        if (!isAdmin && !isRefereeOfMatch) {
            Toast.makeText(this, "Solo el arbitro seleccionado puede puntuar", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> playerIds = match.getPlayerIds();
        List<String> playerNames = match.getPlayerNames();
        if (playerIds.isEmpty() || playerNames.isEmpty()) {
            Toast.makeText(this, "No hay jugadores para puntuar", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, 0, pad, 0);
        container.setBackgroundColor(Color.WHITE);

        EditText etScoreA = new EditText(this);
        etScoreA.setInputType(InputType.TYPE_CLASS_NUMBER);
        etScoreA.setHint("Puntuacion local");
        etScoreA.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
        etScoreA.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        container.addView(etScoreA);

        EditText etScoreB = new EditText(this);
        etScoreB.setInputType(InputType.TYPE_CLASS_NUMBER);
        etScoreB.setHint("Puntuacion visitante");
        etScoreB.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
        etScoreB.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        container.addView(etScoreB);

        Spinner winnerSpinner = new Spinner(this);
        ArrayAdapter<String> winnerAdapter = buildDialogSpinnerAdapter(playerNames);
        winnerSpinner.setAdapter(winnerAdapter);
        container.addView(winnerSpinner);

        new AlertDialog.Builder(this)
                .setTitle("Puntuar " + match.getTitle())
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String scoreA = etScoreA.getText().toString().trim();
                    String scoreB = etScoreB.getText().toString().trim();
                    if (scoreA.isEmpty() || scoreB.isEmpty()) {
                        Toast.makeText(this, "Completa el marcador", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int winnerIndex = winnerSpinner.getSelectedItemPosition();
                    String winnerId = playerIds.get(winnerIndex);
                    String score = String.format(Locale.getDefault(), "%s-%s", scoreA, scoreB);

                    db.collection(Constants.COLLECTION_MATCHES)
                            .document(match.getId())
                            .update(
                                    "score", score,
                                    "winnerId", winnerId,
                                    "open", false
                            )
                            .addOnSuccessListener(unused -> {
                                applyRankingResult(playerIds, playerNames, winnerId);
                                Toast.makeText(this, "Marcador guardado", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "No se pudo guardar marcador", Toast.LENGTH_SHORT).show()
                            );
                })
                .show();
    }

    private ArrayAdapter<String> buildDialogSpinnerAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(MatchesActivity.this, R.color.text_dark));
                }
                return view;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(MatchesActivity.this, R.color.text_dark));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void applyRankingResult(List<String> playerIds, List<String> playerNames, String winnerId) {
        for (int i = 0; i < playerIds.size(); i++) {
            String userId = playerIds.get(i);
            String userName = i < playerNames.size() ? playerNames.get(i) : "Jugador";
            boolean winner = userId.equals(winnerId);
            updateUserRanking(userId, userName, winner);
        }
    }

    private void updateUserRanking(String userId, String userName, boolean winner) {
        db.collection(Constants.COLLECTION_RANKING)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        RankingEntry entry = doc.toObject(RankingEntry.class);
                        if (entry == null) return;

                        int nextPoints = Math.max(0, entry.getPoints() + (winner ? Constants.POINTS_WIN : Constants.POINTS_LOSS));
                        int nextWins = entry.getWins() + (winner ? 1 : 0);
                        int nextLosses = entry.getLosses() + (winner ? 0 : 1);

                        db.collection(Constants.COLLECTION_RANKING)
                                .document(doc.getId())
                                .update(
                                        "points", nextPoints,
                                        "wins", nextWins,
                                        "losses", nextLosses,
                                        "rankName", RankingRules.getRankName(nextPoints)
                                );
                    } else {
                        int points = winner ? 1000 + Constants.POINTS_WIN : Math.max(0, 1000 + Constants.POINTS_LOSS);
                        RankingEntry entry = new RankingEntry(
                                "",
                                userId,
                                userName,
                                points,
                                winner ? 1 : 0,
                                winner ? 0 : 1,
                                0,
                                RankingRules.getRankName(points)
                        );
                        db.collection(Constants.COLLECTION_RANKING).add(entry);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (matchesListener != null) {
            matchesListener.remove();
        }
    }
}
