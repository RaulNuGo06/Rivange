package com.rivange.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rivange.app.R;
import com.rivange.app.models.User;
import com.rivange.app.utils.Constants;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilRegisterName;
    private TextInputLayout tilRegisterEmail;
    private TextInputLayout tilRegisterPhone;
    private TextInputLayout tilRegisterPassword;
    private TextInputLayout tilRegisterConfirmPassword;

    private TextInputEditText etRegisterName;
    private TextInputEditText etRegisterEmail;
    private TextInputEditText etRegisterPhone;
    private TextInputEditText etRegisterPassword;
    private TextInputEditText etRegisterConfirmPassword;

    private Button btnRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tilRegisterName = findViewById(R.id.tilRegisterName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPhone = findViewById(R.id.tilRegisterPhone);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilRegisterConfirmPassword = findViewById(R.id.tilRegisterConfirmPassword);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPhone = findViewById(R.id.etRegisterPhone);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        limpiarErrores();

        String nombre = getText(etRegisterName);
        String email = getText(etRegisterEmail);
        String telefono = getText(etRegisterPhone);
        String password = getText(etRegisterPassword);
        String confirmPassword = getText(etRegisterConfirmPassword);

        boolean valido = true;

        if (TextUtils.isEmpty(nombre)) {
            tilRegisterName.setError("Introduce tu nombre");
            valido = false;
        } else if (nombre.length() < 3) {
            tilRegisterName.setError("Nombre demasiado corto");
            valido = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilRegisterEmail.setError("Introduce el correo");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegisterEmail.setError("Correo no válido");
            valido = false;
        }

        if (TextUtils.isEmpty(telefono)) {
            tilRegisterPhone.setError("Introduce el teléfono");
            valido = false;
        } else if (telefono.length() < 9) {
            tilRegisterPhone.setError("Teléfono no válido");
            valido = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilRegisterPassword.setError("Introduce una contraseña");
            valido = false;
        } else if (password.length() < 6) {
            tilRegisterPassword.setError("Mínimo 6 caracteres");
            valido = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilRegisterConfirmPassword.setError("Confirma la contraseña");
            valido = false;
        } else if (!password.equals(confirmPassword)) {
            tilRegisterConfirmPassword.setError("Las contraseñas no coinciden");
            valido = false;
        }

        if (!valido) {
            return;
        }

        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        String mensaje = task.getException() != null
                                ? task.getException().getMessage()
                                : "No se pudo registrar";
                        Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

                    User user = new User(
                            uid,
                            nombre,
                            email,
                            Constants.ROLE_USER,
                            telefono,
                            Constants.DEFAULT_PROFILE_IMAGE,
                            System.currentTimeMillis()
                    );

                    db.collection(Constants.COLLECTION_USERS)
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                });
    }

    private void limpiarErrores() {
        tilRegisterName.setError(null);
        tilRegisterEmail.setError(null);
        tilRegisterPhone.setError(null);
        tilRegisterPassword.setError(null);
        tilRegisterConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}