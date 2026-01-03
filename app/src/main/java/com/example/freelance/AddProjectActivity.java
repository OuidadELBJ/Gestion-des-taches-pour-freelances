package com.example.freelance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {

    public static final String EXTRA_NAME         = "extra_name";
    public static final String EXTRA_CLIENT       = "extra_client";
    public static final String EXTRA_CLIENT_EMAIL = "extra_client_email";
    public static final String EXTRA_CLIENT_PHONE = "extra_client_phone";
    public static final String EXTRA_DEADLINE     = "extra_deadline";
    public static final String EXTRA_STATUS       = "extra_status";
    public static final String EXTRA_BUDGET       = "extra_budget";
    public static final String EXTRA_HOURLY       = "extra_hourly";
    public static final String EXTRA_NOTES        = "extra_notes";

    private EditText editName, editClientName, editClientEmail, editClientPhone;
    private EditText editDeadline, editBudget, editHourlyRate, editNotes;
    private Spinner spinnerStatus;
    private Button buttonCancel, buttonCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddProject);
        toolbar.setNavigationOnClickListener(v -> finish());

        editName = findViewById(R.id.editProjectName);
        editClientName = findViewById(R.id.editClientName);
        editClientEmail = findViewById(R.id.editClientEmail);
        editClientPhone = findViewById(R.id.editClientPhone);

        editDeadline = findViewById(R.id.editDeadline);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        editBudget = findViewById(R.id.editBudget);
        editHourlyRate = findViewById(R.id.editHourlyRate);
        editNotes = findViewById(R.id.editNotes);

        buttonCancel = findViewById(R.id.buttonCancelProject);
        buttonCreate = findViewById(R.id.buttonCreateProject);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.project_status_array,
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        editDeadline.setOnClickListener(v -> showDatePicker());

        buttonCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        buttonCreate.setOnClickListener(v -> validateAndReturn());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    String date = String.format("%02d/%02d/%04d", d, m + 1, y);
                    editDeadline.setText(date);
                },
                year, month, day
        );
        dialog.show();
    }

    private void validateAndReturn() {
        String name = safeText(editName);
        String clientName = safeText(editClientName);
        String clientEmail = safeText(editClientEmail);
        String clientPhone = safeText(editClientPhone);
        String deadline = safeText(editDeadline);
        String status = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : "En cours";
        String budgetStr = safeText(editBudget);
        String hourlyStr = safeText(editHourlyRate);
        String notes = safeText(editNotes);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(clientName) || TextUtils.isEmpty(deadline)) {
            Toast.makeText(this, "Nom, client et deadline sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(clientEmail) && !android.util.Patterns.EMAIL_ADDRESS.matcher(clientEmail).matches()) {
            Toast.makeText(this, "Email client invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = parseDouble(budgetStr, 0.0);
        double hourly = parseDouble(hourlyStr, 0.0);

        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        data.putExtra(EXTRA_CLIENT, clientName);
        data.putExtra(EXTRA_CLIENT_EMAIL, clientEmail);
        data.putExtra(EXTRA_CLIENT_PHONE, clientPhone);
        data.putExtra(EXTRA_DEADLINE, deadline);
        data.putExtra(EXTRA_STATUS, status);
        data.putExtra(EXTRA_BUDGET, budget);
        data.putExtra(EXTRA_HOURLY, hourly);
        data.putExtra(EXTRA_NOTES, notes);

        setResult(RESULT_OK, data);
        finish();
    }

    private String safeText(EditText et) {
        return (et.getText() == null) ? "" : et.getText().toString().trim();
    }

    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return def; }
    }
}