package com.rivange.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rivange.app.R;
import com.rivange.app.models.User;
import com.rivange.app.utils.BottomNavHelper;
import com.rivange.app.utils.Constants;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfilePhone;
    private TextView tvProfileRole;
    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        btnLogout = findViewById(R.id.btnLogout);

        BottomNavHelper.setup(this, findViewById(R.id.bottomNavigation), R.id.nav_profile);

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadProfile();
    }

    private void loadProfile() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        tvProfileName.setText(user.getName());
                        tvProfileEmail.setText(user.getEmail());
                        tvProfilePhone.setText(user.getPhone());
                        String role = user.getRole();
                        if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
                            tvProfileRole.setText("admin");
                        } else if (Constants.ROLE_REFEREE.equalsIgnoreCase(role)) {
                            tvProfileRole.setText("arbitro");
                        } else {
                            tvProfileRole.setText("usuario");
                        }
                    }
                });
    }
}
