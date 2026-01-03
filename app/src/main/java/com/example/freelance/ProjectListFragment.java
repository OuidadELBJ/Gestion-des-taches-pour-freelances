package com.example.freelance;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.ArrayList;
import java.util.List;


public class ProjectListFragment extends Fragment {

    private RecyclerView recyclerProjects;
    private FloatingActionButton fabAddProject;
    private EditText editSearch;
    private MaterialButton chipAll, chipInProgress, chipLate, chipDone;

    private ProjectListAdapter adapter;
    private final List<ProjectUiModel> fullList = new ArrayList<>();
    private ProjectUiModel.Status currentStatusFilter = null; // null = Tous
    private ActivityResultLauncher<Intent> addProjectLauncher;
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addProjectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String name     = data.getStringExtra(AddProjectActivity.EXTRA_NAME);
                        String client   = data.getStringExtra(AddProjectActivity.EXTRA_CLIENT);
                        String deadline = data.getStringExtra(AddProjectActivity.EXTRA_DEADLINE);
                        String status   = data.getStringExtra(AddProjectActivity.EXTRA_STATUS);
                        double budget   = data.getDoubleExtra(AddProjectActivity.EXTRA_BUDGET, 0);
                        double hourly   = data.getDoubleExtra(AddProjectActivity.EXTRA_HOURLY, 0);
                        String notes    = data.getStringExtra(AddProjectActivity.EXTRA_NOTES);

                        if (name == null || client == null || deadline == null) {
                            return; // sécurité
                        }

                        // Mapping statut texte -> enum
                        ProjectUiModel.Status statusEnum;
                        if ("Terminé".equals(status)) {
                            statusEnum = ProjectUiModel.Status.DONE;
                        } else if ("En retard".equals(status)) {
                            statusEnum = ProjectUiModel.Status.LATE;
                        } else {
                            statusEnum = ProjectUiModel.Status.IN_PROGRESS;
                        }

                        String budgetText = budget > 0
                                ? String.format("%.0f €", budget)
                                : "0 €";

                        // Crée un nouveau projet "fake" cohérent avec tes champs
                        ProjectUiModel newProject = new ProjectUiModel(
                                String.valueOf(System.currentTimeMillis()), // id simple
                                name,
                                client,
                                "Deadline : " + deadline,
                                budgetText,
                                "0h suivies",
                                "Tâches : 0/0",
                                "Dernière activité : -",
                                0,
                                statusEnum
                        );

                        // Ajout en haut de ta liste source
                        fullList.add(0, newProject);
                        applyFilters();   // réapplique recherche + filtres

                        if (recyclerProjects != null) {
                            recyclerProjects.scrollToPosition(0);
                        }
                    }
                }
        );
    }
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
        editSearch       = view.findViewById(R.id.editSearchProjects);

        chipAll        = view.findViewById(R.id.chipFilterAll);
        chipInProgress = view.findViewById(R.id.chipFilterInProgress);
        chipLate       = view.findViewById(R.id.chipFilterLate);
        chipDone       = view.findViewById(R.id.chipFilterDone);

        recyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProjectListAdapter(project -> {
            // Navigation vers détail projet (Bundle classique)
            NavController navController = Navigation.findNavController(requireView());
            Bundle bundle = new Bundle();
            bundle.putString("projectId", project.id);
            bundle.putString("projectName", project.name);
            bundle.putString("projectClient", project.client);
            bundle.putString("projectInfo",
                    project.client + " • " + project.deadlineText +
                            " • Temps : " + project.trackedTimeText +
                            " • Paiements : " + project.budgetText);
            navController.navigate(
                    R.id.action_projectListFragment_to_projectDetailFragment,
                    bundle
            );
        });

        recyclerProjects.setAdapter(adapter);

        setupFakeData();
        setupSearch();
        setupChips();
        setupFab();
    }

    private void setupFakeData() {
        // TODO: plus tard on remplacera par les données de Room (membre B)
        fullList.clear();
        fullList.add(new ProjectUiModel(
                "1",
                "Site web client A",
                "Client A",
                "Deadline : 15/01/2026",
                "1 200 €",
                "5h suivies",
                "Tâches : 2/5",
                "Dernière activité : hier",
                40,
                ProjectUiModel.Status.IN_PROGRESS
        ));
        fullList.add(new ProjectUiModel(
                "2",
                "App Android freelance",
                "Client B",
                "Deadline : 05/02/2026",
                "2 500 €",
                "8h suivies",
                "Tâches : 4/8",
                "Dernière activité : aujourd’hui",
                50,
                ProjectUiModel.Status.LATE
        ));
        fullList.add(new ProjectUiModel(
                "3",
                "Logo + branding",
                "Client C",
                "Deadline : 10/12/2025",
                "900 €",
                "12h suivies",
                "Tâches : 6/6",
                "Dernière activité : il y a 3 jours",
                100,
                ProjectUiModel.Status.DONE
        ));
        fullList.add(new ProjectUiModel(
                "4",
                "Refonte site vitrine",
                "Client D",
                "Deadline : 20/03/2026",
                "3 000 €",
                "0h suivies",
                "Tâches : 0/4",
                "Dernière activité : -",
                0,
                ProjectUiModel.Status.IN_PROGRESS
        ));

        applyFilters();
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChips() {
        View.OnClickListener listener = v -> {
            if (v == chipAll) {
                currentStatusFilter = null;
            } else if (v == chipInProgress) {
                currentStatusFilter = ProjectUiModel.Status.IN_PROGRESS;
            } else if (v == chipLate) {
                currentStatusFilter = ProjectUiModel.Status.LATE;
            } else if (v == chipDone) {
                currentStatusFilter = ProjectUiModel.Status.DONE;
            }
            updateChipStyles();
            applyFilters();
        };

        chipAll.setOnClickListener(listener);
        chipInProgress.setOnClickListener(listener);
        chipLate.setOnClickListener(listener);
        chipDone.setOnClickListener(listener);

        // état initial
        updateChipStyles();
    }

    private void updateChipStyles() {
        // Simple : un seul chip "actif" en bleu, le reste en gris
        setChipStyle(chipAll,        currentStatusFilter == null);
        setChipStyle(chipInProgress, currentStatusFilter == ProjectUiModel.Status.IN_PROGRESS);
        setChipStyle(chipLate,       currentStatusFilter == ProjectUiModel.Status.LATE);
        setChipStyle(chipDone,       currentStatusFilter == ProjectUiModel.Status.DONE);
    }

    private void setChipStyle(MaterialButton chip, boolean selected) {
        if (getContext() == null) return;
        int primary   = getResources().getColor(R.color.color_primary);
        int onPrimary = getResources().getColor(R.color.color_on_primary);
        int bg        = getResources().getColor(R.color.cardBackground);
        int textSec   = getResources().getColor(R.color.textSecondary);

        if (selected) {
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primary));
            chip.setTextColor(onPrimary);
        } else {
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bg));
            chip.setTextColor(textSec);
        }
    }

    private void setupFab() {
        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddProjectActivity.class);
            addProjectLauncher.launch(intent);
        });
    }

    private void applyFilters() {
        String search = editSearch.getText() != null
                ? editSearch.getText().toString().trim().toLowerCase()
                : "";

        List<ProjectUiModel> filtered = new ArrayList<>();
        for (ProjectUiModel p : fullList) {
            // filtre statut
            if (currentStatusFilter != null && p.status != currentStatusFilter) {
                continue;
            }
            // filtre texte (nom + client)
            if (!search.isEmpty()) {
                String name   = p.name != null ? p.name.toLowerCase() : "";
                String client = p.client != null ? p.client.toLowerCase() : "";
                if (!name.contains(search) && !client.contains(search)) {
                    continue;
                }
            }
            filtered.add(p);
        }

        adapter.submitList(filtered);
    }
}