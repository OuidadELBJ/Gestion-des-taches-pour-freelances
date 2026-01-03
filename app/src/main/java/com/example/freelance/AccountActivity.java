package com.example.freelance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AccountActivity extends AppCompatActivity {

    private static final String PREFS = "profile_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private TextInputEditText editEmail, editPhone;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        MaterialToolbar tb = findViewById(R.id.toolbarAccount);
        tb.setNavigationOnClickListener(v -> finish());

        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSaveAccount);

        loadPrefs();

        btnSave.setOnClickListener(v -> {
            String email = safeText(editEmail);
            String phone = safeText(editPhone);

            // validations simples (optionnel)
            if (!TextUtils.isEmpty(email) && !email.contains("@")) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            savePrefs(email, phone);
            Toast.makeText(this, "Enregistré ✅", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPrefs() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        editEmail.setText(sp.getString(KEY_EMAIL, "ouidad@email.com"));
        editPhone.setText(sp.getString(KEY_PHONE, "+212 6 00 00 00 00"));
    }

    private void savePrefs(String email, String phone) {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        sp.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, phone)
                .apply();
    }

    private String safeText(TextInputEditText et) {
        if (et == null || et.getText() == null) return "";
        return et.getText().toString().trim();
    }
}