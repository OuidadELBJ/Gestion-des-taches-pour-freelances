package com.example.freelance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.fake.FakeProjectStore;
import data.modele.Project;

public class ProjectListFragment extends Fragment {

    private RecyclerView recyclerProjects;
    private FloatingActionButton fabAddProject;
    private EditText editSearch;
    private MaterialButton chipAll, chipInProgress, chipLate, chipDone;

    private ProjectListAdapter adapter;
    private final List<ProjectUiModel> fullList = new ArrayList<>();
    private ProjectUiModel.Status currentStatusFilter = null; // null = Tous

    private ActivityResultLauncher<Intent> addProjectLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addProjectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;

                    Intent data = result.getData();

                    String name = data.getStringExtra(AddProjectActivity.EXTRA_NAME);
                    String clientName = data.getStringExtra(AddProjectActivity.EXTRA_CLIENT);
                    String clientEmail = data.getStringExtra(AddProjectActivity.EXTRA_CLIENT_EMAIL);
                    String clientPhone = data.getStringExtra(AddProjectActivity.EXTRA_CLIENT_PHONE);
                    String deadlineStr = data.getStringExtra(AddProjectActivity.EXTRA_DEADLINE);
                    String statusStr = data.getStringExtra(AddProjectActivity.EXTRA_STATUS);

                    double budget = data.getDoubleExtra(AddProjectActivity.EXTRA_BUDGET, 0);
                    double hourly = data.getDoubleExtra(AddProjectActivity.EXTRA_HOURLY, 0);
                    String notes = data.getStringExtra(AddProjectActivity.EXTRA_NOTES);

                    if (name == null || clientName == null || deadlineStr == null) return;

                    // ✅ 1) créer le vrai Project
                    String newId = "p" + System.currentTimeMillis();
                    Project p = new Project(newId, name);
                    p.clientName = clientName;
                    p.clientEmail = (clientEmail == null ? "" : clientEmail);
                    p.clientPhone = (clientPhone == null ? "" : clientPhone);
                    p.status = (statusStr == null ? "En cours" : statusStr);
                    p.notes = (notes == null ? "" : notes);

                    // Budget/Hourly : on garde ton UX simple
                    p.budgetAmount = budget;
                    p.rate = hourly;
                    p.billingType = Project.BILLING_PROJECT;

                    // Deadline string -> millis (dd/MM/yyyy)
                    p.deadlineMillis = parseDateToMillis(deadlineStr);

                    FakeProjectStore.get().add(p);

                    // ✅ 2) recharger la liste depuis le store (source unique)
                    loadFromStore();
                }
        );
    }

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerProjects = view.findViewById(R.id.recyclerProjects);
        fabAddProject = view.findViewById(R.id.fabAddProject);
        editSearch = view.findViewById(R.id.editSearchProjects);

        chipAll = view.findViewById(R.id.chipFilterAll);
        chipInProgress = view.findViewById(R.id.chipFilterInProgress);
        chipLate = view.findViewById(R.id.chipFilterLate);
        chipDone = view.findViewById(R.id.chipFilterDone);

        recyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProjectListAdapter(project -> {
            NavController navController = Navigation.findNavController(requireView());
            Bundle bundle = new Bundle();
            bundle.putString("projectId", project.id);
            bundle.putString("projectName", project.name);
            bundle.putString("projectClient", project.client);

            navController.navigate(
                    R.id.action_projectListFragment_to_projectDetailFragment,
                    bundle
            );
        });

        recyclerProjects.setAdapter(adapter);

        loadFromStore();
        setupSearch();
        setupChips();
        setupFab();
    }

    private void loadFromStore() {
        fullList.clear();

        List<Project> projects = FakeProjectStore.get().list();
        for (Project p : projects) {
            String deadlineText = (p.deadlineMillis > 0)
                    ? "Deadline : " + new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date(p.deadlineMillis))
                    : "Deadline : -";

            String budgetText = String.format(Locale.FRANCE, "%.0f €", p.expectedAmount());

            ProjectUiModel.Status st = mapStatus(p.status);

            fullList.add(new ProjectUiModel(
                    p.id,
                    p.name,
                    (p.clientName == null ? "" : p.clientName),
                    deadlineText,
                    budgetText,
                    "0h suivies",
                    "Tâches : 0/0",
                    "Dernière activité : -",
                    0,
                    st
            ));
        }

        applyFilters();

        if (recyclerProjects != null) recyclerProjects.scrollToPosition(0);
    }

    private ProjectUiModel.Status mapStatus(String statusStr) {
        if (statusStr == null) return ProjectUiModel.Status.IN_PROGRESS;
        String s = statusStr.toLowerCase(Locale.FRANCE);

        if (s.contains("termin")) return ProjectUiModel.Status.DONE;
        if (s.contains("retard")) return ProjectUiModel.Status.LATE;
        return ProjectUiModel.Status.IN_PROGRESS;
    }

    private long parseDateToMillis(String ddMMyyyy) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Date d = sdf.parse(ddMMyyyy);
            return d != null ? d.getTime() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChips() {
        android.view.View.OnClickListener listener = v -> {
            if (v == chipAll) currentStatusFilter = null;
            else if (v == chipInProgress) currentStatusFilter = ProjectUiModel.Status.IN_PROGRESS;
            else if (v == chipLate) currentStatusFilter = ProjectUiModel.Status.LATE;
            else if (v == chipDone) currentStatusFilter = ProjectUiModel.Status.DONE;

            updateChipStyles();
            applyFilters();
        };

        chipAll.setOnClickListener(listener);
        chipInProgress.setOnClickListener(listener);
        chipLate.setOnClickListener(listener);
        chipDone.setOnClickListener(listener);

        updateChipStyles();
    }

    private void updateChipStyles() {
        setChipStyle(chipAll, currentStatusFilter == null);
        setChipStyle(chipInProgress, currentStatusFilter == ProjectUiModel.Status.IN_PROGRESS);
        setChipStyle(chipLate, currentStatusFilter == ProjectUiModel.Status.LATE);
        setChipStyle(chipDone, currentStatusFilter == ProjectUiModel.Status.DONE);
    }

    private void setChipStyle(MaterialButton chip, boolean selected) {
        if (getContext() == null) return;

        int primary = getResources().getColor(R.color.color_primary);
        int onPrimary = getResources().getColor(R.color.color_on_primary);
        int bg = getResources().getColor(R.color.cardBackground);
        int textSec = getResources().getColor(R.color.textSecondary);

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
                ? editSearch.getText().toString().trim().toLowerCase(Locale.FRANCE)
                : "";

        List<ProjectUiModel> filtered = new ArrayList<>();
        for (ProjectUiModel p : fullList) {

            if (currentStatusFilter != null && p.status != currentStatusFilter) continue;

            if (!search.isEmpty()) {
                String name = p.name != null ? p.name.toLowerCase(Locale.FRANCE) : "";
                String client = p.client != null ? p.client.toLowerCase(Locale.FRANCE) : "";
                if (!name.contains(search) && !client.contains(search)) continue;
            }

            filtered.add(p);
        }

        adapter.submitList(filtered);
    }
}