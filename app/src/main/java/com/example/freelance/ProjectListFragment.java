package com.example.freelance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.navigation.fragment.NavHostFragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProjectListFragment extends Fragment {

    private RecyclerView recyclerProjects;
    private FloatingActionButton fabAddProject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerProjects = view.findViewById(R.id.recyclerProjects);
        fabAddProject    = view.findViewById(R.id.fabAddProject);

        recyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- Données fake pour tester l’UI ---
        List<ProjectUiModel> fakeProjects = new ArrayList<>();
        fakeProjects.add(new ProjectUiModel(
                "1",
                "Site web client A",
                "Client A",
                "Deadline : 15/01/2026",
                "Tâches : 2/5 (40%)"
        ));
        fakeProjects.add(new ProjectUiModel(
                "2",
                "App mobile B",
                "Client B",
                "Deadline : 20/02/2026",
                "Tâches : 1/3 (33%)"
        ));
        fakeProjects.add(new ProjectUiModel(
                "3",
                "Branding C",
                "Client C",
                "Deadline : 10/03/2026",
                "Tâches : 0/4 (0%)"
        ));

        ProjectListAdapter adapter = new ProjectListAdapter(fakeProjects, project -> {
            // Navigation vers le détail
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putString("projectId", project.id);
            bundle.putString("projectName", project.name);
            bundle.putString("projectClient", project.client);
            bundle.putString("projectDeadlineText", project.deadlineText);

            navController.navigate(
                    R.id.action_projectListFragment_to_projectDetailFragment,
                    bundle
            );
        });

        recyclerProjects.setAdapter(adapter);

        fabAddProject.setOnClickListener(v -> {
            Toast.makeText(
                    getContext(),
                    "TODO: Ajouter projet",
                    Toast.LENGTH_SHORT
            ).show();
            // plus tard -> navigation vers écran Ajouter/Modifier projet
        });
    }
}