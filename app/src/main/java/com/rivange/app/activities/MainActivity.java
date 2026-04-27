package com.rivange.app.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rivange.app.BuildConfig;
import com.rivange.app.R;
import com.rivange.app.adapters.ReservationAdapter;
import com.rivange.app.models.Match;
import com.rivange.app.models.Reservation;
import com.rivange.app.models.User;
import com.rivange.app.sensor.SensorApiClient;
import com.rivange.app.sensor.SensorReading;
import com.rivange.app.sensor.SensorRepository;
import com.rivange.app.sensor.SensorUiMapper;
import com.rivange.app.sensor.SensorUiState;
import com.rivange.app.utils.BottomNavHelper;
import com.rivange.app.utils.Constants;
import com.rivange.app.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ADMIN_EMAIL = "admin@rivange.com";

    private TextView tvWelcome;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvHeatIndex;
    private TextView tvSensorBadge;
    private TextView tvEmptyReservations;

    private ProgressBar progressSensor;
    private RecyclerView rvReservations;
    private Button btnNewReservation;
    private Button btnOpenAdminPanel;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ReservationAdapter reservationAdapter;

    private String currentUserName = "Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecycler();
        setupBottomNavigation();

        btnNewReservation.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, NewReservationActivity.class))
        );

        btnOpenAdminPanel.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AdminPanelActivity.class))
        );

        ensureCurrentUserDocument();
        loadReservations();
        loadSensor();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvHeatIndex = findViewById(R.id.tvHeatIndex);
        tvSensorBadge = findViewById(R.id.tvSensorBadge);
        tvEmptyReservations = findViewById(R.id.tvEmptyReservations);

        progressSensor = findViewById(R.id.progressSensor);
        rvReservations = findViewById(R.id.rvReservations);
        btnNewReservation = findViewById(R.id.btnNewReservation);
        btnOpenAdminPanel = findViewById(R.id.btnOpenAdminPanel);
    }

    private void setupRecycler() {
        reservationAdapter = new ReservationAdapter(new ReservationAdapter.OnReservationActionListener() {
            @Override
            public void onEdit(Reservation reservation) {
                editReservation(reservation);
            }

            @Override
            public void onDelete(Reservation reservation) {
                deleteReservationStyled(reservation);
            }

            @Override
            public void onCreateMatch(Reservation reservation) {
                createMatchFromReservation(reservation);
            }
        });
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(reservationAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);
    }

    private void ensureCurrentUserDocument() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        String uid = firebaseUser.getUid();

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        updateUserUi(user, firebaseUser);
                    } else {
                        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";
                        String name = firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().trim().isEmpty()
                                ? firebaseUser.getDisplayName().trim()
                                : email.contains("@") ? email.substring(0, email.indexOf("@")) : "Usuario";

                        String role = ADMIN_EMAIL.equalsIgnoreCase(email)
                                ? Constants.ROLE_ADMIN
                                : Constants.ROLE_USER;

                        User user = new User(
                                uid,
                                name,
                                email,
                                role,
                                "",
                                Constants.DEFAULT_PROFILE_IMAGE,
                                System.currentTimeMillis()
                        );

                        db.collection(Constants.COLLECTION_USERS)
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> updateUserUi(user, firebaseUser))
                                .addOnFailureListener(e -> {
                                    tvWelcome.setText("Hola");
                                    btnOpenAdminPanel.setVisibility(
                                            ADMIN_EMAIL.equalsIgnoreCase(email) ? View.VISIBLE : View.GONE
                                    );
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    FirebaseUser current = auth.getCurrentUser();
                    String email = current != null && current.getEmail() != null ? current.getEmail() : "";
                    tvWelcome.setText("Hola");
                    btnOpenAdminPanel.setVisibility(
                            ADMIN_EMAIL.equalsIgnoreCase(email) ? View.VISIBLE : View.GONE
                    );
                });
    }

    private void updateUserUi(User user, FirebaseUser firebaseUser) {
        currentUserName = user != null && !user.getName().trim().isEmpty() ? user.getName() : "Usuario";
        tvWelcome.setText("Hola, " + currentUserName);

        boolean isAdmin = (user != null && user.isAdmin())
                || (firebaseUser.getEmail() != null && ADMIN_EMAIL.equalsIgnoreCase(firebaseUser.getEmail()));

        btnOpenAdminPanel.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }

    private void loadReservations() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection(Constants.COLLECTION_RESERVATIONS)
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Reservation> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Reservation reservation = doc.toObject(Reservation.class);
                        reservation.setId(doc.getId());
                        list.add(reservation);
                    }
                    list.sort(Comparator.comparingLong(Reservation::getCreatedAt).reversed());

                    reservationAdapter.submitList(list);
                    tvEmptyReservations.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error cargando reservas", Toast.LENGTH_SHORT).show()
                );
    }

    private void editReservation(Reservation reservation) {
        Intent intent = new Intent(this, NewReservationActivity.class);
        intent.putExtra(Constants.EXTRA_RESERVATION_ID, reservation.getId());
        startActivity(intent);
    }

    private void deleteReservation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar reserva")
                .setMessage("Se eliminará la reserva de " + reservation.getDate() + " " + reservation.getStartTime())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) ->
                        db.collection(Constants.COLLECTION_RESERVATIONS)
                                .document(reservation.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                                    loadReservations();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                                ))
                .show();
    }

    private void deleteReservationStyled(Reservation reservation) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_reservation, null);
        TextView tvDeleteMessage = dialogView.findViewById(R.id.tvDeleteMessage);
        Button btnCancelDelete = dialogView.findViewById(R.id.btnCancelDelete);
        Button btnConfirmDelete = dialogView.findViewById(R.id.btnConfirmDelete);

        tvDeleteMessage.setText("Se eliminara la reserva de " + reservation.getDate() + " " + reservation.getStartTime() + ".");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());
        btnConfirmDelete.setOnClickListener(v ->
                db.collection(Constants.COLLECTION_RESERVATIONS)
                        .document(reservation.getId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                            loadReservations();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                        ));

        dialog.show();
    }

    private void createMatchFromReservation(Reservation reservation) {
        long reservationStartMillis = DateTimeUtils.parseDateTimeMillis(reservation.getDate(), reservation.getStartTime());
        if (reservationStartMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Solo puedes crear partido de una reserva futura", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("role", Constants.ROLE_REFEREE)
                .get()
                .addOnSuccessListener(refereesSnapshot -> {
                    List<User> referees = new ArrayList<>();
                    List<String> refereeNames = new ArrayList<>();

                    for (DocumentSnapshot doc : refereesSnapshot.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setId(doc.getId());
                            referees.add(user);
                            refereeNames.add(user.getName());
                        }
                    }

                    if (referees.isEmpty()) {
                        Toast.makeText(this, "No hay arbitros disponibles", Toast.LENGTH_LONG).show();
                        return;
                    }

                    showCreateMatchDialog(reservation, referees, refereeNames);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando arbitros", Toast.LENGTH_SHORT).show()
                );
    }

    private void showCreateMatchDialog(Reservation reservation, List<User> referees, List<String> refereeNames) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, 0, pad, 0);
        container.setBackgroundColor(Color.WHITE);

        EditText etTitle = new EditText(this);
        etTitle.setHint("Titulo del partido");
        etTitle.setText("Partido " + reservation.getSport() + " " + reservation.getStartTime());
        etTitle.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
        etTitle.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        container.addView(etTitle);

        Spinner levelSpinner = new Spinner(this);
        List<String> levels = Arrays.asList("Principiante", "Intermedio", "Avanzado");
        ArrayAdapter<String> levelAdapter = buildDialogSpinnerAdapter(levels);
        levelSpinner.setAdapter(levelAdapter);
        container.addView(levelSpinner);

        Spinner maxPlayersSpinner = new Spinner(this);
        List<String> maxPlayers = Arrays.asList("2", "4");
        ArrayAdapter<String> maxPlayersAdapter = buildDialogSpinnerAdapter(maxPlayers);
        maxPlayersSpinner.setAdapter(maxPlayersAdapter);
        maxPlayersSpinner.setSelection(1);
        container.addView(maxPlayersSpinner);

        Spinner refereeSpinner = new Spinner(this);
        ArrayAdapter<String> refereeAdapter = buildDialogSpinnerAdapter(refereeNames);
        refereeSpinner.setAdapter(refereeAdapter);
        container.addView(refereeSpinner);

        new AlertDialog.Builder(this)
                .setTitle("Crear partido")
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        title = "Partido";
                    }

                    int selectedRefereePosition = refereeSpinner.getSelectedItemPosition();
                    User selectedReferee = referees.get(selectedRefereePosition);
                    int maxPlayersSelected = Integer.parseInt(maxPlayers.get(maxPlayersSpinner.getSelectedItemPosition()));
                    String level = levels.get(levelSpinner.getSelectedItemPosition());

                    validateRefereeAndCreateMatch(reservation, selectedReferee, title, level, maxPlayersSelected);
                })
                .show();
    }

    private ArrayAdapter<String> buildDialogSpinnerAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_dark));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_dark));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void validateRefereeAndCreateMatch(
            Reservation reservation,
            User referee,
            String title,
            String level,
            int maxPlayers
    ) {
        db.collection(Constants.COLLECTION_MATCHES)
                .whereEqualTo("refereeId", referee.getId())
                .whereEqualTo("open", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this, "Ese arbitro ya dirige otro partido abierto", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = auth.getUid();
                    if (uid == null) {
                        return;
                    }

                    Match match = new Match(
                            "",
                            title,
                            reservation.getDate(),
                            reservation.getStartTime(),
                            reservation.getCourtName(),
                            reservation.getCourtId(),
                            reservation.getSport(),
                            level,
                            maxPlayers,
                            1,
                            uid,
                            true,
                            new ArrayList<>(Arrays.asList(uid)),
                            new ArrayList<>(Arrays.asList(currentUserName)),
                            referee.getId(),
                            referee.getName(),
                            "",
                            ""
                    );

                    db.collection(Constants.COLLECTION_MATCHES)
                            .add(match)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Partido creado. Ya puedes verlo en Partidos", Toast.LENGTH_LONG).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "No se pudo crear el partido", Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudo validar el arbitro", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadSensor() {
        String url = BuildConfig.HOME_ASSISTANT_URL;
        String token = BuildConfig.HOME_ASSISTANT_TOKEN;

        if (url == null || token == null || url.trim().isEmpty() || token.trim().isEmpty()) {
            showSensorError("Sin configurar");
            return;
        }

        progressSensor.setVisibility(View.VISIBLE);

        SensorApiClient apiClient = new SensorApiClient(url, token);
        SensorRepository repository = new SensorRepository(apiClient);

        repository.loadEnvironment(
                Constants.SENSOR_TEMP_ENTITY,
                Constants.SENSOR_HUMIDITY_ENTITY,
                new SensorRepository.RepositoryCallback() {
                    @Override
                    public void onSuccess(SensorReading reading) {
                        runOnUiThread(() -> {
                            progressSensor.setVisibility(View.GONE);

                            tvTemperature.setText(reading.getTemperature() + " C");
                            tvHumidity.setText(reading.getHumidity() + " %");
                            tvHeatIndex.setText(reading.getHeatIndex() + " C");

                            SensorUiState state = SensorUiMapper.fromReading(reading);
                            tvSensorBadge.setText(state.getLabel());
                            tvSensorBadge.setBackgroundTintList(
                                    ColorStateList.valueOf(
                                            ContextCompat.getColor(MainActivity.this, state.getBadgeColorRes())
                                    )
                            );
                            tvSensorBadge.setTextColor(
                                    ContextCompat.getColor(MainActivity.this, state.getTextColorRes())
                            );
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            showSensorError("Sin datos");
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    private void showSensorError(String label) {
        progressSensor.setVisibility(View.GONE);
        tvTemperature.setText("-- C");
        tvHumidity.setText("-- %");
        tvHeatIndex.setText("-- C");
        tvSensorBadge.setText(label);
        tvSensorBadge.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.sensor_neutral))
        );
        tvSensorBadge.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
        ensureCurrentUserDocument();
        loadSensor();
    }
}
