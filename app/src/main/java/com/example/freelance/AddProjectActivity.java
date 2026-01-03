package com.example.freelance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {

    public static final String EXTRA_NAME     = "extra_name";
    public static final String EXTRA_CLIENT   = "extra_client";
    public static final String EXTRA_DEADLINE = "extra_deadline";
    public static final String EXTRA_STATUS   = "extra_status";
    public static final String EXTRA_BUDGET   = "extra_budget";
    public static final String EXTRA_HOURLY   = "extra_hourly";
    public static final String EXTRA_NOTES    = "extra_notes";

    private EditText editName;
    private EditText editClient;
    private EditText editDeadline;
    private Spinner  spinnerStatus;
    private EditText editBudget;
    private EditText editHourly;
    private EditText editNotes;
    private Button   buttonCancel;
    private Button   buttonCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        Toolbar toolbar = findViewById(R.id.toolbarAddProject);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nouveau projet");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        editName     = findViewById(R.id.editProjectName);
        editClient   = findViewById(R.id.editClient);
        editDeadline = findViewById(R.id.editDeadline);
        spinnerStatus= findViewById(R.id.spinnerStatus);
        editBudget   = findViewById(R.id.editBudget);
        editHourly   = findViewById(R.id.editHourlyRate);
        editNotes    = findViewById(R.id.editNotes);
        buttonCancel = findViewById(R.id.buttonCancelProject);
        buttonCreate = findViewById(R.id.buttonCreateProject);

        // Statuts
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.project_status_array,   // on le crée en strings.xml juste après
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Date picker
        editDeadline.setOnClickListener(v -> showDatePicker());

        buttonCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        buttonCreate.setOnClickListener(v -> validateAndReturn());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d",
                            dayOfMonth, month1 + 1, year1);
                    editDeadline.setText(date);
                },
                year, month, day
        );
        dialog.show();
    }

    private void validateAndReturn() {
        String name     = editName.getText().toString().trim();
        String client   = editClient.getText().toString().trim();
        String deadline = editDeadline.getText().toString().trim();
        String status   = spinnerStatus.getSelectedItem().toString();
        String budgetStr= editBudget.getText().toString().trim();
        String hourlyStr= editHourly.getText().toString().trim();
        String notes    = editNotes.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(client) || TextUtils.isEmpty(deadline)) {
            Toast.makeText(this,
                    "Nom, client et deadline sont obligatoires",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = 0;
        double hourly = 0;
        try {
            if (!budgetStr.isEmpty()) budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException ignored) {}
        try {
            if (!hourlyStr.isEmpty()) hourly = Double.parseDouble(hourlyStr);
        } catch (NumberFormatException ignored) {}

        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        data.putExtra(EXTRA_CLIENT, client);
        data.putExtra(EXTRA_DEADLINE, deadline);
        data.putExtra(EXTRA_STATUS, status);
        data.putExtra(EXTRA_BUDGET, budget);
        data.putExtra(EXTRA_HOURLY, hourly);
        data.putExtra(EXTRA_NOTES, notes);

        setResult(RESULT_OK, data);
        finish();
    }
}