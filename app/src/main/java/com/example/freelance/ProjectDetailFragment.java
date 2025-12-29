package com.example.freelance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailFragment extends Fragment {

    private TextView textProjectNameDetail;
    private TextView textProjectInfo;

    private RecyclerView recyclerTasks;
    private FloatingActionButton fabAddTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textProjectNameDetail = view.findViewById(R.id.textProjectNameDetail);
        textProjectInfo = view.findViewById(R.id.textProjectInfo);
        recyclerTasks = view.findViewById(R.id.recyclerTasks);
        fabAddTask = view.findViewById(R.id.fabAddTask);

        // ---- Récupérer les arguments envoyés ----
        Bundle args = getArguments();
        String projectName = "Projet (inconnu)";
        String projectClient = "Client inconnu";
        String projectDeadlineText = "Deadline : --/--/----";

        if (args != null) {
            projectName = args.getString("projectName", projectName);
            projectClient = args.getString("projectClient", projectClient);
            projectDeadlineText = args.getString("projectDeadlineText", projectDeadlineText);
        }

        textProjectNameDetail.setText(projectName);

        // Texte dynamique : client + deadline + fake temps/paiements
        String info = projectClient + " • " + projectDeadlineText
                + " • Temps : 5h30 (fake) • Paiements : 800€ (fake)";
        textProjectInfo.setText(info);

        // --- RecyclerView tâches (fake) ---
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        List<TaskUiModel> fakeTasks = new ArrayList<>();
        fakeTasks.add(new TaskUiModel("t1", "Maquette Figma",
                "Deadline : 05/01/2026", "Statut : À faire"));
        fakeTasks.add(new TaskUiModel("t2", "Intégration page d’accueil",
                "Deadline : 08/01/2026", "Statut : En cours"));
        fakeTasks.add(new TaskUiModel("t3", "Tests + livraison",
                "Deadline : 12/01/2026", "Statut : Fait"));

        TaskListAdapter adapter = new TaskListAdapter(fakeTasks, task -> {
            Toast.makeText(getContext(),
                    "Tâche cliquée : " + task.title,
                    Toast.LENGTH_SHORT).show();
        });

        recyclerTasks.setAdapter(adapter);

        fabAddTask.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "TODO: Ajouter une nouvelle tâche",
                        Toast.LENGTH_SHORT).show()
        );
    }
}