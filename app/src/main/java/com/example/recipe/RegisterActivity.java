package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnRegister;
    private Button btnGoToLogin;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());

        btnGoToLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        // -------------------------
        // Name validation
        // -------------------------

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (name.length() < 2) {
            etName.setError(
                    "Name must contain at least 2 characters"
            );
            etName.requestFocus();
            return;
        }

        if (name.length() > 50) {
            etName.setError(
                    "Name cannot exceed 50 characters"
            );
            etName.requestFocus();
            return;
        }

        // -------------------------
        // Email validation
        // -------------------------

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(
                    "Enter a valid email address"
            );
            etEmail.requestFocus();
            return;
        }

        // -------------------------
        // Password validation
        // -------------------------

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError(
                    "Password must contain at least 8 characters"
            );
            etPassword.requestFocus();
            return;
        }

        if (password.length() > 64) {
            etPassword.setError(
                    "Password cannot exceed 64 characters"
            );
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            etPassword.setError(
                    "Password must contain an uppercase letter"
            );
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            etPassword.setError(
                    "Password must contain a lowercase letter"
            );
            etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*\\d.*")) {
            etPassword.setError(
                    "Password must contain a number"
            );
            etPassword.requestFocus();
            return;
        }

        // -------------------------
        // Confirm password
        // -------------------------

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(
                    "Please confirm your password"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // -------------------------
        // Firebase registration
        // -------------------------

        btnRegister.setEnabled(false);

        firebaseAuth
                .createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(this, task -> {

                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        /*
                         * Firebase automatically signs the newly
                         * registered user in.
                         *
                         * We sign them out here because our current
                         * Phase 2 flow sends them back to Login.
                         */
                        firebaseAuth.signOut();

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        String message;

                        if (task.getException() != null) {
                            message =
                                    task.getException().getMessage();
                        } else {
                            message = "Registration failed";
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}