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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    private FirebaseAuth firebaseAuth;
    private Button btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RecipeListActivity.class
            );

            startActivity(intent);
            finish();

            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());

        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        // Email validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Login successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(LoginActivity.this, RecipeListActivity.class);
                        startActivity(intent);
                        finish();
                    }

                     else {

                        String message;

                        if (task.getException() != null) {
                            message = task.getException().getMessage();
                        } else {
                            message = "Login failed";
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}