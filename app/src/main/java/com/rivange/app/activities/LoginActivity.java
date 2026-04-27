package com.rivange.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.rivange.app.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilLoginEmail;
    private TextInputLayout tilLoginPassword;
    private TextInputEditText etLoginEmail;
    private TextInputEditText etLoginPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> iniciarSesion());
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void iniciarSesion() {
        limpiarErrores();

        String email = getText(etLoginEmail);
        String password = getText(etLoginPassword);

        boolean valido = true;

        if (TextUtils.isEmpty(email)) {
            tilLoginEmail.setError("Introduce el correo");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilLoginEmail.setError("Correo no válido");
            valido = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilLoginPassword.setError("Introduce la contraseña");
            valido = false;
        } else if (password.length() < 6) {
            tilLoginPassword.setError("Mínimo 6 caracteres");
            valido = false;
        }

        if (!valido) {
            return;
        }

        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String mensaje = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error al iniciar sesión";
                        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void limpiarErrores() {
        tilLoginEmail.setError(null);
        tilLoginPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}