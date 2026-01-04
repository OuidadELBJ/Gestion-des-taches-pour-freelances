package com.example.freelance;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etNom, etPrenom, etEmail, etPassword;
    private Button btnSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Init vues
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // 1. État initial du bouton (Désactivé)
        updateButtonState();

        // 2. Ajouter les surveillants sur les 4 champs
        etNom.addTextChangedListener(textWatcher);
        etPrenom.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

        btnSignup.setOnClickListener(v -> attemptSignup());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    // Le surveillant unique pour tous les champs
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateButtonState();
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void updateButtonState() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Tout doit être rempli et mot de passe >= 6
        boolean isReady = !nom.isEmpty() && !prenom.isEmpty() && !email.isEmpty() && password.length() >= 6;

        if (isReady) {
            btnSignup.setEnabled(true);
            btnSignup.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnSignup.setTextColor(Color.WHITE);
        } else {
            btnSignup.setEnabled(false);
            btnSignup.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonDisabled));
            btnSignup.setTextColor(Color.WHITE);
        }
    }

    private void attemptSignup() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        creerCompteEtSauvegarder(email, password, nom, prenom);
    }

    private void creerCompteEtSauvegarder(String email, String password, String nom, String prenom) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sauvegarderInfosUtilisateur(user.getUid(), nom, prenom, email);
                        }
                    } else {
                        Toast.makeText(this, "Erreur Auth : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sauvegarderInfosUtilisateur(String userId, String nom, String prenom, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nom", nom);
        userMap.put("prenom", prenom);
        userMap.put("email", email);
        userMap.put("date_creation", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Compte créé ! Veuillez vous connecter.", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur sauvegarde : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}