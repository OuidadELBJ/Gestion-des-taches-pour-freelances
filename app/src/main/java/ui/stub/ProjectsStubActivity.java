package ui.stub;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.R;
import java.util.List;

import data.fake.FakeProjectStore;
import data.modele.Project;

public class ProjectsStubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_stub);

        RecyclerView rv = findViewById(R.id.rvProjectsStub);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Project> projects = FakeProjectStore.get().list();
        rv.setAdapter(new ProjectsStubAdapter(projects, p -> {
            Intent i = new Intent(this, ProjectStubActivity.class);
            i.putExtra("projectId", p.id);
            startActivity(i);
        }));
    }
}
