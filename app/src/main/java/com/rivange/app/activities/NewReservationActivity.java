package com.rivange.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rivange.app.R;
import com.rivange.app.models.Court;
import com.rivange.app.models.Reservation;
import com.rivange.app.models.User;
import com.rivange.app.utils.Constants;
import com.rivange.app.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NewReservationActivity extends AppCompatActivity {

    private Spinner spinnerSports;
    private Spinner spinnerCourts;
    private TextInputLayout tilReservationDate;
    private TextInputLayout tilReservationStart;
    private TextInputLayout tilReservationEnd;
    private TextInputEditText etReservationDate;
    private TextInputEditText etReservationStart;
    private TextInputEditText etReservationEnd;
    private Button btnSaveReservation;

    private final List<Court> allCourts = new ArrayList<>();
    private final List<Court> filteredCourts = new ArrayList<>();
    private final List<String> sports = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String currentUserName = "Usuario";
    private String reservationIdToEdit = "";
    private long createdAtForEdit = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        spinnerSports = findViewById(R.id.spinnerSports);
        spinnerCourts = findViewById(R.id.spinnerCourts);
        tilReservationDate = findViewById(R.id.tilReservationDate);
        tilReservationStart = findViewById(R.id.tilReservationStart);
        tilReservationEnd = findViewById(R.id.tilReservationEnd);
        etReservationDate = findViewById(R.id.etReservationDate);
        etReservationStart = findViewById(R.id.etReservationStart);
        etReservationEnd = findViewById(R.id.etReservationEnd);
        btnSaveReservation = findViewById(R.id.btnSaveReservation);

        reservationIdToEdit = getIntent().getStringExtra(Constants.EXTRA_RESERVATION_ID);
        if (reservationIdToEdit == null) {
            reservationIdToEdit = "";
        }
        if (!reservationIdToEdit.isEmpty()) {
            btnSaveReservation.setText("Guardar cambios");
        }

        etReservationDate.setOnClickListener(v -> showCalendar());
        etReservationStart.setOnClickListener(v -> showTimePicker(etReservationStart));
        etReservationEnd.setOnClickListener(v -> showTimePicker(etReservationEnd));

        loadCurrentUserName();
        loadCourts();

        btnSaveReservation.setOnClickListener(v -> saveReservation());
    }

    private void loadCurrentUserName() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
                        currentUserName = user.getName();
                    }
                });
    }

    private void loadCourts() {
        db.collection(Constants.COLLECTION_COURTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allCourts.clear();
                    Set<String> sportSet = new LinkedHashSet<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Court court = doc.toObject(Court.class);
                        if (court == null) {
                            continue;
                        }

                        Boolean available = doc.getBoolean("available");
                        if (available != null && !available) {
                            continue;
                        }

                        court.setId(doc.getId());
                        if (court.getSport().isEmpty()) {
                            String legacySport = doc.getString("deporte");
                            if (legacySport != null && !legacySport.trim().isEmpty()) {
                                court.setSport(legacySport.trim());
                            }
                        }
                        if (court.getSport().isEmpty()) {
                            court.setSport("General");
                        }
                        allCourts.add(court);
                        sportSet.add(court.getSport());
                    }

                    sports.clear();
                    sports.addAll(sportSet);
                    if (sports.isEmpty() || allCourts.isEmpty()) {
                        Toast.makeText(this, "No hay deportes disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayAdapter<String> sportsAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            sports
                    );
                    sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSports.setAdapter(sportsAdapter);
                    spinnerSports.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                            filterCourtsBySport(sports.get(position));
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        }
                    });

                    filterCourtsBySport(sports.get(0));
                    if (!reservationIdToEdit.isEmpty()) {
                        loadReservationToEdit();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando pistas (revisa permisos de Firestore para 'pistas')", Toast.LENGTH_LONG).show()
                );
    }

    private void filterCourtsBySport(String sport) {
        filteredCourts.clear();
        List<String> courtNames = new ArrayList<>();
        for (Court court : allCourts) {
            if (sport.equalsIgnoreCase(court.getSport())) {
                filteredCourts.add(court);
                courtNames.add(court.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courtNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourts.setAdapter(adapter);
    }

    private void loadReservationToEdit() {
        db.collection(Constants.COLLECTION_RESERVATIONS)
                .document(reservationIdToEdit)
                .get()
                .addOnSuccessListener(doc -> {
                    Reservation reservation = doc.toObject(Reservation.class);
                    if (reservation == null) {
                        return;
                    }
                    createdAtForEdit = reservation.getCreatedAt();
                    etReservationDate.setText(reservation.getDate());
                    etReservationStart.setText(reservation.getStartTime());
                    etReservationEnd.setText(reservation.getEndTime());
                    selectSportAndCourt(reservation.getSport(), reservation.getCourtId());
                });
    }

    private void selectSportAndCourt(String sport, String courtId) {
        int sportIndex = Math.max(0, sports.indexOf(sport));
        spinnerSports.setSelection(sportIndex);
        filterCourtsBySport(sports.get(sportIndex));

        int courtIndex = 0;
        for (int i = 0; i < filteredCourts.size(); i++) {
            if (filteredCourts.get(i).getId().equals(courtId)) {
                courtIndex = i;
                break;
            }
        }
        spinnerCourts.setSelection(courtIndex);
    }

    private void showCalendar() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, month + 1, year);
                    etReservationDate.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showTimePicker(TextInputEditText target) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    target.setText(hora);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }

    private void saveReservation() {
        clearErrors();

        String date = getText(etReservationDate);
        String start = getText(etReservationStart);
        String end = getText(etReservationEnd);

        if (auth.getUid() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filteredCourts.isEmpty()) {
            Toast.makeText(this, "No hay pistas para este deporte", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean valid = true;
        if (TextUtils.isEmpty(date)) {
            tilReservationDate.setError("Selecciona una fecha");
            valid = false;
        }
        if (TextUtils.isEmpty(start)) {
            tilReservationStart.setError("Selecciona la hora de inicio");
            valid = false;
        }
        if (TextUtils.isEmpty(end)) {
            tilReservationEnd.setError("Selecciona la hora de fin");
            valid = false;
        }
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end) && start.compareTo(end) >= 0) {
            tilReservationEnd.setError("La hora fin debe ser mayor que la de inicio");
            valid = false;
        }
        if (!TextUtils.isEmpty(date) && DateTimeUtils.isPastDay(date)) {
            tilReservationDate.setError("No se puede reservar en una fecha pasada");
            valid = false;
        }
        long startMillis = DateTimeUtils.parseDateTimeMillis(date, start);
        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(start) && startMillis <= System.currentTimeMillis()) {
            tilReservationStart.setError("No puedes reservar en una hora ya pasada");
            valid = false;
        }
        if (!valid) {
            return;
        }

        Court selectedCourt = filteredCourts.get(spinnerCourts.getSelectedItemPosition());

        db.collection(Constants.COLLECTION_RESERVATIONS)
                .whereEqualTo("courtId", selectedCourt.getId())
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean overlap = false;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.getId().equals(reservationIdToEdit)) {
                            continue;
                        }
                        Reservation existing = doc.toObject(Reservation.class);
                        if (existing != null && hoursOverlap(start, end, existing.getStartTime(), existing.getEndTime())) {
                            overlap = true;
                            break;
                        }
                    }

                    if (overlap) {
                        Toast.makeText(this, "Ya existe una reserva en esa pista y tramo horario", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Reservation reservation = new Reservation(
                            reservationIdToEdit,
                            auth.getUid(),
                            currentUserName,
                            selectedCourt.getId(),
                            selectedCourt.getName(),
                            selectedCourt.getSport(),
                            date,
                            start,
                            end,
                            "Activa",
                            createdAtForEdit > 0 ? createdAtForEdit : System.currentTimeMillis()
                    );

                    btnSaveReservation.setEnabled(false);

                    if (reservationIdToEdit.isEmpty()) {
                        db.collection(Constants.COLLECTION_RESERVATIONS)
                                .add(reservation)
                                .addOnSuccessListener(documentReference -> {
                                    btnSaveReservation.setEnabled(true);
                                    Toast.makeText(this, "Reserva creada correctamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    btnSaveReservation.setEnabled(true);
                                    Toast.makeText(this, "Error al crear la reserva", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        db.collection(Constants.COLLECTION_RESERVATIONS)
                                .document(reservationIdToEdit)
                                .set(reservation)
                                .addOnSuccessListener(unused -> {
                                    btnSaveReservation.setEnabled(true);
                                    Toast.makeText(this, "Reserva actualizada", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    btnSaveReservation.setEnabled(true);
                                    Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudo validar la disponibilidad", Toast.LENGTH_SHORT).show()
                );
    }

    private boolean hoursOverlap(String start1, String end1, String start2, String end2) {
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }

    private void clearErrors() {
        tilReservationDate.setError(null);
        tilReservationStart.setError(null);
        tilReservationEnd.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
