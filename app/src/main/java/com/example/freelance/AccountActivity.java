package com.example.freelance;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPhone;
    private MaterialButton btnSave;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        MaterialToolbar tb = findViewById(R.id.toolbarAccount);
        tb.setNavigationOnClickListener(v -> finish());

        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSaveAccount);

        loadAccount();

        btnSave.setOnClickListener(v -> saveAccount());
    }

    private void loadAccount() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) {
            finish();
            return;
        }

        // Email affiché: celui du login (auth)
        if (u.getEmail() != null) editEmail.setText(u.getEmail());

        // Téléphone + (optionnel) email contact: Firestore users/{uid}
        fs.collection("users").document(u.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String phone = doc.getString("phone");
                    if (phone != null) editPhone.setText(phone);

                    // si tu veux afficher email depuis Firestore aussi:
                    // String email = doc.getString("email");
                    // if (email != null) editEmail.setText(email);
                });
    }

    private void saveAccount() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) { finish(); return; }

        String email = safeText(editEmail);
        String phone = safeText(editPhone);

        if (!TextUtils.isEmpty(email) && !email.contains("@")) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        // ⚠️ Important:
        // Modifier l'email de login FirebaseAuth demande re-auth (sinon ça échoue).
        // Donc ici on sauvegarde surtout les infos "profil" dans Firestore.
        Map<String, Object> up = new HashMap<>();
        up.put("email", email);   // email de contact (pas forcément email de login)
        up.put("phone", phone);

        fs.collection("users").document(u.getUid())
                .update(up)
                .addOnSuccessListener(v -> Toast.makeText(this, "Enregistré ✅", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String safeText(TextInputEditText et) {
        if (et == null || et.getText() == null) return "";
        return et.getText().toString().trim();
    }
}