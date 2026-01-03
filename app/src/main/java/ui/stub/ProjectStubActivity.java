package ui.stub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import data.fake.FakeProjectStore;
import data.fake.FakeTaskStore;
import data.modele.Project;

public class ProjectStubActivity extends AppCompatActivity {

    private String projectId;
    private Project project;

    private TextView tvTitle, tvMeta;
    private TasksStubAdapter adapter;

    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stub);

        projectId = getIntent().getStringExtra("projectId");
        project = FakeProjectStore.get().getById(projectId);

        tvTitle = findViewById(R.id.tvProjectTitleStub);
        tvMeta  = findViewById(R.id.tvProjectMetaStub);

        Button btnEditProject = findViewById(R.id.btnEditProjectNotif);
        btnEditProject.setOnClickListener(v -> {
            Intent i = new Intent(this, EditProjectNotificationsActivity.class);
            i.putExtra("projectId", projectId);
            startActivity(i);
        });

        RecyclerView rv = findViewById(R.id.rvTasksStub);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TasksStubAdapter(taskId -> {
            Intent i = new Intent(this, EditTaskNotificationsActivity.class);
            i.putExtra("taskId", taskId);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        refresh();
    }

    @Override protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        project = FakeProjectStore.get().getById(projectId);
        if (project == null) return;

        tvTitle.setText(project.name);

        String deadline = project.deadlineMillis > 0 ? df.format(new Date(project.deadlineMillis)) : "Pas de deadline";
        String status = project.reminderEnabled
                ? (project.useDefaultOffsets ? "Rappel projet: défaut" : "Rappel projet: custom")
                : "Rappel projet: OFF";

        tvMeta.setText(deadline + " • " + status);

        adapter.submit(FakeTaskStore.get().listByProject(projectId));
    }
}
