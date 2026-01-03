package com.example.freelance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        MaterialToolbar tb = findViewById(R.id.toolbarHelp);
        tb.setNavigationOnClickListener(v -> finish());

        LinearLayout rowFaq = findViewById(R.id.rowFaq);
        LinearLayout rowBug = findViewById(R.id.rowBug);

        rowFaq.setOnClickListener(v ->
                startActivity(new Intent(this, FaqActivity.class))
        );

        rowBug.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:"));
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@tonapp.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Bug - Freelance App");
            email.putExtra(Intent.EXTRA_TEXT,
                    "Décris le bug ici :\n\n" +
                            "Étapes pour reproduire :\n1)\n2)\n\n" +
                            "Téléphone / Android :\n" +
                            "Version app :\n");

            try {
                startActivity(Intent.createChooser(email, "Envoyer un email"));
            } catch (Exception e) {
                Toast.makeText(this, "Aucune app email trouvée", Toast.LENGTH_SHORT).show();
            }
        });
    }
}