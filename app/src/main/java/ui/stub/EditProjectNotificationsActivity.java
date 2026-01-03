package ui.stub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.freelance.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.fake.FakeProjectStore;
import data.modele.Project;
import notifications.ReminderScheduler;

public class EditProjectNotificationsActivity extends AppCompatActivity {

    private String projectId;
    private Project p;

    private SwitchCompat swEnabled;
    private CheckBox cbUseDefault, cb10m, cb1h, cb1d, cb2d, cb1w;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project_notifications);

        projectId = getIntent().getStringExtra("projectId");
        p = FakeProjectStore.get().getById(projectId);

        swEnabled = findViewById(R.id.swProjectEnabled);
        cbUseDefault = findViewById(R.id.cbProjectUseDefault);

        cb10m = findViewById(R.id.cbP10m);
        cb1h  = findViewById(R.id.cbP1h);
        cb1d  = findViewById(R.id.cbP1d);
        cb2d  = findViewById(R.id.cbP2d);
        cb1w  = findViewById(R.id.cbP1w);

        Button btnSave = findViewById(R.id.btnSaveProjectNotif);

        bind();

        cbUseDefault.setOnCheckedChangeListener((b, checked) -> setCustomEnabled(!checked));

        btnSave.setOnClickListener(v -> {
            save();
            ReminderScheduler.rescheduleAll(this);
            finish();
        });
    }

    private void bind() {
        if (p == null) return;

        swEnabled.setChecked(p.reminderEnabled);
        cbUseDefault.setChecked(p.useDefaultOffsets);

        setCustomEnabled(!p.useDefaultOffsets);

        long[] arr = p.customOffsetsMillis == null ? new long[0] : p.customOffsetsMillis;

        cb10m.setChecked(contains(arr, TimeUnit.MINUTES.toMillis(10)));
        cb1h.setChecked(contains(arr, TimeUnit.HOURS.toMillis(1)));
        cb1d.setChecked(contains(arr, TimeUnit.DAYS.toMillis(1)));
        cb2d.setChecked(contains(arr, TimeUnit.DAYS.toMillis(2)));
        cb1w.setChecked(contains(arr, TimeUnit.DAYS.toMillis(7)));
    }

    private void setCustomEnabled(boolean enabled) {
        cb10m.setEnabled(enabled);
        cb1h.setEnabled(enabled);
        cb1d.setEnabled(enabled);
        cb2d.setEnabled(enabled);
        cb1w.setEnabled(enabled);
    }

    private void save() {
        if (p == null) return;

        p.reminderEnabled = swEnabled.isChecked();
        p.useDefaultOffsets = cbUseDefault.isChecked();

        if (!p.useDefaultOffsets) {
            List<Long> offsets = new ArrayList<>();
            if (cb10m.isChecked()) offsets.add(TimeUnit.MINUTES.toMillis(10));
            if (cb1h.isChecked()) offsets.add(TimeUnit.HOURS.toMillis(1));
            if (cb1d.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(1));
            if (cb2d.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(2));
            if (cb1w.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(7));

            if (offsets.isEmpty()) offsets.add(TimeUnit.HOURS.toMillis(1));

            p.customOffsetsMillis = new long[offsets.size()];
            for (int i = 0; i < offsets.size(); i++) p.customOffsetsMillis[i] = offsets.get(i);
        } else {
            p.customOffsetsMillis = new long[0];
        }

        // compat champs anciens
        p.notifEnabled = p.reminderEnabled;
        p.notifUseDefault = p.useDefaultOffsets;
        p.notifOffsetsMillis = p.customOffsetsMillis;
    }

    private boolean contains(long[] arr, long v) {
        for (long x : arr) if (x == v) return true;
        return false;
    }
}
