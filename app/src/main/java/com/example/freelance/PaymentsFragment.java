package com.example.freelance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ui.payments.AddPaymentActivity;
import ui.payments.PaymentsAdapter;
import ui.payments.PaymentsViewModel;

import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.repository.ProjetRepository;

public class PaymentsFragment extends Fragment {

    private MaterialToolbar toolbar;

    private Spinner spinnerProjects, spinnerMonth, spinnerYear;
    private TextView tvTotalReceived, tvExpected, tvRemaining;
    private ProgressBar progress;
    private PaymentsAdapter adapter;
    private PaymentsViewModel vm;

    private TextView tvEmpty;

    private String selectedProjectId;

    private Integer filterMonth = null; // 0..11
    private Integer filterYear = null;

    // Wrappers cliquables
    private View rowProject, rowMonth, rowYear;

    // ✅ Room Repo projets
    private ProjetRepository projetRepo;

    // ✅ Items spinner (pas de FakeProjectStore)
    private final List<ProjectItem> projectItems = new ArrayList<>();
    private ArrayAdapter<ProjectItem> projectsAdapter;

    // Petit modèle pour le spinner
    private static class ProjectItem {
        final String id;
        final String name;

        ProjectItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() { return name; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        projetRepo = new ProjetRepository(requireContext());
        vm = new PaymentsViewModel(requireContext());

        toolbar = view.findViewById(R.id.toolbarPayments);
        toolbar.setTitle("Paiements");
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(ContextCompat.getColor(requireContext(), android.R.color.white));
        }
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        spinnerProjects = view.findViewById(R.id.spinnerProjectsPayments);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear = view.findViewById(R.id.spinnerYear);

        tvTotalReceived = view.findViewById(R.id.tvTotalPayments);
        tvExpected = view.findViewById(R.id.tvExpected);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        progress = view.findViewById(R.id.progressBilling);

        tvEmpty = view.findViewById(R.id.tvEmptyPayments);

        RecyclerView rv = view.findViewById(R.id.rvPayments);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PaymentsAdapter(requireContext());
        rv.setAdapter(adapter);

        Button btnAdd = view.findViewById(R.id.btnAddPayment);
        Button btnClear = view.findViewById(R.id.btnClearFilter);

        rowProject = view.findViewById(R.id.rowSelectProject);
        rowMonth = view.findViewById(R.id.rowSelectMonth);
        rowYear = view.findViewById(R.id.rowSelectYear);

        rowProject.setOnClickListener(v -> spinnerProjects.performClick());
        rowMonth.setOnClickListener(v -> spinnerMonth.performClick());
        rowYear.setOnClickListener(v -> spinnerYear.performClick());

        setupProjectsFromArgsIfAny();
        setupProjectsSpinnerAdapter();
        setupMonthYear();

        btnAdd.setOnClickListener(v -> {
            if (selectedProjectId == null || selectedProjectId.trim().isEmpty()) {
                Toast.makeText(requireContext(), "Choisis d’abord un projet", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(requireContext(), AddPaymentActivity.class);
            i.putExtra("projectId", selectedProjectId);
            startActivity(i);
        });

        btnClear.setOnClickListener(v -> {
            filterMonth = null;
            filterYear = null;
            spinnerMonth.setSelection(0);
            spinnerYear.setSelection(0);
            loadPayments();
        });

        // ✅ Charge les projets depuis Room, puis charge les paiements
        loadProjectsFromRoom();
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ recharger tout (projets + paiements) en cas d’ajout
        loadProjectsFromRoom();
    }

    private void setupProjectsFromArgsIfAny() {
        Bundle args = getArguments();
        if (args != null) {
            String argProjectId = args.getString("projectId", null);
            if (argProjectId != null && !argProjectId.trim().isEmpty()) {
                selectedProjectId = argProjectId;
            }
        }
    }

    private void setupProjectsSpinnerAdapter() {
        projectsAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                projectItems
        );
        projectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjects.setAdapter(projectsAdapter);

        spinnerProjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                ProjectItem p = (ProjectItem) parent.getItemAtPosition(position);
                selectedProjectId = p.id;
                loadPayments();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadProjectsFromRoom() {
        projetRepo.getAll(projets -> {
            projectItems.clear();

            if (projets != null) {
                for (Projet p : projets) {
                    String name = (p.getName() == null || p.getName().trim().isEmpty())
                            ? "(Sans nom)"
                            : p.getName();
                    projectItems.add(new ProjectItem(p.getIdProjet(), name));
                }
            }

            projectsAdapter.notifyDataSetChanged();

            if (projectItems.isEmpty()) {
                selectedProjectId = null;
                adapter.submit(new ArrayList<>());
                tvTotalReceived.setText("Reçu : 0 €");
                tvExpected.setText("Attendu : 0 €");
                tvRemaining.setText("Reste : 0 €");
                progress.setProgress(0);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Aucun projet en base. Ajoute un projet d’abord.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ sélectionner le bon projet (args ou premier)
            int index = 0;
            if (selectedProjectId != null) {
                for (int i = 0; i < projectItems.size(); i++) {
                    if (selectedProjectId.equals(projectItems.get(i).id)) {
                        index = i;
                        break;
                    }
                }
            } else {
                selectedProjectId = projectItems.get(0).id;
            }

            spinnerProjects.setSelection(index);
            // loadPayments() est appelé par onItemSelected automatiquement
        });
    }

    private void setupMonthYear() {
        String[] months = new String[]{
                "Tous", "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
                "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"
        };
        ArrayAdapter<String> monthAd = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAd);

        Calendar c = Calendar.getInstance();
        int yNow = c.get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        years.add(0); // Tous
        for (int y = yNow - 3; y <= yNow + 1; y++) years.add(y);

        ArrayAdapter<Integer> yearAd = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAd);

        spinnerMonth.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterMonth = (position == 0) ? null : (position - 1);
                loadPayments();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                Integer val = (Integer) parent.getItemAtPosition(position);
                filterYear = (val != null && val != 0) ? val : null;
                loadPayments();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerMonth.setSelection(0);
        spinnerYear.setSelection(0);
    }

    private void loadPayments() {
        if (selectedProjectId == null) return;

        vm.loadPayments(selectedProjectId, filterMonth, filterYear, state -> {
            adapter.submit(state.list);

            if (state.filtered) {
                tvTotalReceived.setText("Reçu (filtré) : " + vm.money(state.received));
            } else {
                tvTotalReceived.setText("Reçu : " + vm.money(state.received));
            }

            tvExpected.setText("Attendu : " + vm.money(state.expected));
            tvRemaining.setText("Reste : " + vm.money(state.remaining));
            progress.setProgress(state.progressPercent);

            boolean isEmpty = (state.list == null) || state.list.isEmpty();
            tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }
}
