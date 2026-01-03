package ui.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.freelance.R;
import java.util.HashSet;
import java.util.Set;

import data.prefs.ReminderPrefs;
import notifications.ReminderScheduler;

public class NotificationsSettingsActivity extends AppCompatActivity {

    private SwitchCompat swEnabled;
    private CheckBox cbTasks, cbProjects;
    private CheckBox cb10m, cb1h, cb1d, cb2d, cb1w;
    private CheckBox cbAtNine;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_settings);
        com.google.android.material.appbar.MaterialToolbar tb = findViewById(R.id.toolbarNotif);
        tb.setNavigationOnClickListener(v -> finish());
        requestNotifPermissionIfNeeded();

        swEnabled = findViewById(R.id.swRemindersEnabled);
        cbTasks = findViewById(R.id.cbRemindTasks);
        cbProjects = findViewById(R.id.cbRemindProjects);

        cb10m = findViewById(R.id.cb10m);
        cb1h = findViewById(R.id.cb1h);
        cb1d = findViewById(R.id.cb1d);
        cb2d = findViewById(R.id.cb2d);
        cb1w = findViewById(R.id.cb1w);

        cbAtNine = findViewById(R.id.cbAtNine);

        Button btnSave = findViewById(R.id.btnSaveReminders);
        Button btnTest = findViewById(R.id.btnTestNotification);

        ReminderPrefs p = ReminderPrefs.load(this);
        applyToUi(p);

        btnSave.setOnClickListener(v -> {
            ReminderPrefs out = readFromUi();
            out.save(this);
            ReminderScheduler.rescheduleAll(this);
            android.widget.Toast.makeText(this, "Rappels enregistrés ✅", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnTest.setOnClickListener(v -> {
            ReminderScheduler.scheduleTestInSeconds(this, 15);
            android.widget.Toast.makeText(this, "Test dans 15s ✅", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void applyToUi(ReminderPrefs p) {
        swEnabled.setChecked(p.enabled);
        cbTasks.setChecked(p.remindTasks);
        cbProjects.setChecked(p.remindProjects);

        cb10m.setChecked(p.offsetsMillis.contains(10 * 60_000L));
        cb1h.setChecked(p.offsetsMillis.contains(60 * 60_000L));
        cb1d.setChecked(p.offsetsMillis.contains(24 * 60 * 60_000L));
        cb2d.setChecked(p.offsetsMillis.contains(2 * 24 * 60 * 60_000L));
        cb1w.setChecked(p.offsetsMillis.contains(7 * 24 * 60 * 60_000L));

        cbAtNine.setChecked(p.atNine);
    }

    private ReminderPrefs readFromUi() {
        ReminderPrefs p = new ReminderPrefs();
        p.enabled = swEnabled.isChecked();
        p.remindTasks = cbTasks.isChecked();
        p.remindProjects = cbProjects.isChecked();

        Set<Long> offsets = new HashSet<>();
        if (cb10m.isChecked()) offsets.add(10 * 60_000L);
        if (cb1h.isChecked()) offsets.add(60 * 60_000L);
        if (cb1d.isChecked()) offsets.add(24 * 60 * 60_000L);
        if (cb2d.isChecked()) offsets.add(2 * 24 * 60 * 60_000L);
        if (cb1w.isChecked()) offsets.add(7 * 24 * 60 * 60_000L);
        if (offsets.isEmpty()) offsets.add(60 * 60_000L); // sécurité

        p.offsetsMillis = offsets;
        p.atNine = cbAtNine.isChecked();
        return p;
    }

    private void requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
    }
}
