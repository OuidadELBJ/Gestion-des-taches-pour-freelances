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

import data.fake.FakeTaskStore;
import data.modele.Task;
import notifications.ReminderScheduler;

public class EditTaskNotificationsActivity extends AppCompatActivity {

    private String taskId;
    private Task t;

    private SwitchCompat swEnabled;
    private CheckBox cbUseDefault, cb10m, cb1h, cb1d, cb2d, cb1w;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_notifications);

        taskId = getIntent().getStringExtra("taskId");
        t = FakeTaskStore.get().getById(taskId);

        swEnabled = findViewById(R.id.swTaskEnabled);
        cbUseDefault = findViewById(R.id.cbTaskUseDefault);

        cb10m = findViewById(R.id.cbT10m);
        cb1h  = findViewById(R.id.cbT1h);
        cb1d  = findViewById(R.id.cbT1d);
        cb2d  = findViewById(R.id.cbT2d);
        cb1w  = findViewById(R.id.cbT1w);

        Button btnSave = findViewById(R.id.btnSaveTaskNotif);

        bind();

        cbUseDefault.setOnCheckedChangeListener((b, checked) -> setCustomEnabled(!checked));

        btnSave.setOnClickListener(v -> {
            save();
            ReminderScheduler.rescheduleAll(this);
            finish();
        });
    }

    private void bind() {
        if (t == null) return;

        swEnabled.setChecked(t.reminderEnabled);
        cbUseDefault.setChecked(t.useDefaultOffsets);

        setCustomEnabled(!t.useDefaultOffsets);

        long[] arr = t.customOffsetsMillis == null ? new long[0] : t.customOffsetsMillis;

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
        if (t == null) return;

        t.reminderEnabled = swEnabled.isChecked();
        t.useDefaultOffsets = cbUseDefault.isChecked();

        if (!t.useDefaultOffsets) {
            List<Long> offsets = new ArrayList<>();
            if (cb10m.isChecked()) offsets.add(TimeUnit.MINUTES.toMillis(10));
            if (cb1h.isChecked()) offsets.add(TimeUnit.HOURS.toMillis(1));
            if (cb1d.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(1));
            if (cb2d.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(2));
            if (cb1w.isChecked()) offsets.add(TimeUnit.DAYS.toMillis(7));

            if (offsets.isEmpty()) offsets.add(TimeUnit.MINUTES.toMillis(10));

            t.customOffsetsMillis = new long[offsets.size()];
            for (int i = 0; i < offsets.size(); i++) t.customOffsetsMillis[i] = offsets.get(i);
        } else {
            t.customOffsetsMillis = new long[0];
        }
    }

    private boolean contains(long[] arr, long v) {
        for (long x : arr) if (x == v) return true;
        return false;
    }
}
