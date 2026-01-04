package com.example.freelance;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        updateButtonState();

        etEmail.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { updateButtonState(); }
            public void afterTextChanged(Editable s) {}
        });

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Email envoyé ! Vérifiez vos spams.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateButtonState() {
        String email = etEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            btnReset.setEnabled(true);
            btnReset.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnReset.setTextColor(Color.WHITE);
        } else {
            btnReset.setEnabled(false);
            btnReset.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonDisabled));
            btnReset.setTextColor(Color.WHITE);
        }
    }
}