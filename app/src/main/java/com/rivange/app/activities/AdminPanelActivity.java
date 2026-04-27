package com.rivange.app.activities;

import android.os.Bundle;
import android.text.InputType;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rivange.app.R;
import com.rivange.app.models.User;
import com.rivange.app.utils.Constants;
import com.rivange.app.utils.FirestoreSeeder;

public class AdminPanelActivity extends AppCompatActivity {

    private static final String ADMIN_EMAIL = "admin@rivange.com";

    private TextView tvUsersCount;
    private TextView tvReservationsCount;
    private TextView tvMatchesCount;
    private TextView tvChatsCount;
    private TextView tvRefereesCount;
    private TextView tvOpenMatchesCount;
    private Button btnSeedDemoData;
    private Button btnAssignReferee;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvUsersCount = findViewById(R.id.tvUsersCount);
        tvReservationsCount = findViewById(R.id.tvReservationsCount);
        tvMatchesCount = findViewById(R.id.tvMatchesCount);
        tvChatsCount = findViewById(R.id.tvChatsCount);
        tvRefereesCount = findViewById(R.id.tvRefereesCount);
        tvOpenMatchesCount = findViewById(R.id.tvOpenMatchesCount);
        btnSeedDemoData = findViewById(R.id.btnSeedDemoData);
        btnAssignReferee = findViewById(R.id.btnAssignReferee);

        btnSeedDemoData.setOnClickListener(v -> cargarDatosDemo());
        btnAssignReferee.setOnClickListener(v -> showAssignRefereeDialog());

        validarAccesoAdmin();
    }

    private void validarAccesoAdmin() {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null) {
            finish();
            return;
        }

        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);

                    boolean isAdmin = (user != null && user.isAdmin())
                            || ADMIN_EMAIL.equalsIgnoreCase(email);

                    if (!isAdmin) {
                        Toast.makeText(this, "Acceso restringido", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    loadStats();
                })
                .addOnFailureListener(e -> {
                    if (ADMIN_EMAIL.equalsIgnoreCase(email)) {
                        loadStats();
                    } else {
                        Toast.makeText(this, "Acceso restringido", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void cargarDatosDemo() {
        String adminUid = "FfVhRmS53Zbxy1ka0fpvgjZ0LWL2";
        String userUid = "W1LHCy7TqhbyQFs3IdDGYuj0TpU2";

        btnSeedDemoData.setEnabled(false);

        FirestoreSeeder.seedDemoData(db, adminUid, userUid, new FirestoreSeeder.SeedCallback() {
            @Override
            public void onSuccess() {
                btnSeedDemoData.setEnabled(true);
                Toast.makeText(AdminPanelActivity.this, "Datos demo cargados correctamente", Toast.LENGTH_LONG).show();
                loadStats();
            }

            @Override
            public void onError(Exception e) {
                btnSeedDemoData.setEnabled(true);
                Toast.makeText(AdminPanelActivity.this, "Error al cargar datos demo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadStats() {
        db.collection(Constants.COLLECTION_USERS).get()
                .addOnSuccessListener(s -> tvUsersCount.setText(String.valueOf(s.size())));

        db.collection(Constants.COLLECTION_RESERVATIONS).get()
                .addOnSuccessListener(s -> tvReservationsCount.setText(String.valueOf(s.size())));

        db.collection(Constants.COLLECTION_MATCHES).get()
                .addOnSuccessListener(s -> tvMatchesCount.setText(String.valueOf(s.size())));

        db.collection(Constants.COLLECTION_CHATS).get()
                .addOnSuccessListener(s -> tvChatsCount.setText(String.valueOf(s.size())));

        db.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("role", Constants.ROLE_REFEREE)
                .get()
                .addOnSuccessListener(s -> tvRefereesCount.setText(String.valueOf(s.size())));

        db.collection(Constants.COLLECTION_MATCHES)
                .whereEqualTo("open", true)
                .get()
                .addOnSuccessListener(s -> tvOpenMatchesCount.setText(String.valueOf(s.size())));
    }

    private void showAssignRefereeDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, 0, pad, 0);
        container.setBackgroundColor(Color.WHITE);

        EditText etEmail = new EditText(this);
        etEmail.setHint("Correo del usuario");
        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
        etEmail.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        container.addView(etEmail);

        new AlertDialog.Builder(this)
                .setTitle("Asignar rol arbitro")
                .setView(container)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String email = etEmail.getText().toString().trim();
                    if (email.isEmpty()) {
                        Toast.makeText(this, "Introduce un correo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    assignRefereeRole(email);
                })
                .show();
    }

    private void assignRefereeRole(String email) {
        db.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        Toast.makeText(this, "No existe un usuario con ese correo", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String userDocId = snapshot.getDocuments().get(0).getId();
                    db.collection(Constants.COLLECTION_USERS)
                            .document(userDocId)
                            .update("role", Constants.ROLE_REFEREE)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Rol arbitro asignado", Toast.LENGTH_SHORT).show();
                                loadStats();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "No se pudo actualizar rol", Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error buscando usuario", Toast.LENGTH_SHORT).show()
                );
    }
}
