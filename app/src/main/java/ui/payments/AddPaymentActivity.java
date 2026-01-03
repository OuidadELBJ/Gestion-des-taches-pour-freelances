package ui.payments;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freelance.R;
import com.google.android.material.appbar.MaterialToolbar;

import data.fake.FakePaymentStore;

public class AddPaymentActivity extends AppCompatActivity {

    private android.widget.EditText etAmount, etNote, etDueDays;
    private android.widget.CheckBox cbPaid;
    private String projectId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        // Toolbar (style identique aux autres pages)
        MaterialToolbar toolbar = findViewById(R.id.toolbarAddPayment);
        if (toolbar != null) {
            toolbar.setTitle("Ajouter paiement");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        projectId = getIntent().getStringExtra("projectId");
        if (TextUtils.isEmpty(projectId)) {
            Toast.makeText(this, "Projet manquant (projectId)", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etAmount = findViewById(R.id.etPayAmount);
        etNote   = findViewById(R.id.etPayNote);
        etDueDays= findViewById(R.id.etDueDays);
        cbPaid   = findViewById(R.id.cbPaid);

        com.google.android.material.button.MaterialButton btnSave = findViewById(R.id.btnSavePayment);
        com.google.android.material.button.MaterialButton btnCancel = findViewById(R.id.btnCancelPayment);

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            double amount = parseDouble(etAmount.getText() == null ? "" : etAmount.getText().toString());
            if (amount <= 0) {
                Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            String note = etNote.getText() == null ? "" : etNote.getText().toString();
            boolean paid = cbPaid.isChecked();

            int dueInDays = (int) parseDouble(etDueDays.getText() == null ? "" : etDueDays.getText().toString());
            if (dueInDays < 0) dueInDays = 0;

            long now = System.currentTimeMillis();
            long due = now + dueInDays * 24L * 60L * 60L * 1000L;

            FakePaymentStore.get().add(projectId, amount, now, due, paid, note);
            finish();
        });
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }
}