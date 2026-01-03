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
import com.example.freelance.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import data.fake.FakeProjectStore;
import data.modele.Project;
import ui.payments.AddPaymentActivity;
import ui.payments.PaymentsAdapter;
import ui.payments.PaymentsViewModel;

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

    // Wrappers cliquables pour rendre les flèches plus claires
    private View rowProject, rowMonth, rowYear;

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

        vm = new PaymentsViewModel();

        toolbar = view.findViewById(R.id.toolbarPayments);
        toolbar.setTitle("Paiements");
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back); // ton icône retour
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(ContextCompat.getColor(requireContext(), android.R.color.white));
        }
        toolbar.setNavigationOnClickListener(v -> {
            // si tu es venu depuis ProjectDetail -> revient
            // sinon ne fait rien (tu peux aussi faire navigateUp)
            Navigation.findNavController(v).navigateUp();
        });

        // IDs existants (logique D)
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
        adapter = new PaymentsAdapter();
        rv.setAdapter(adapter);

        Button btnAdd = view.findViewById(R.id.btnAddPayment);
        Button btnClear = view.findViewById(R.id.btnClearFilter);

        // Wrappers UI (pour supprimer le côté "plein de flèches")
        rowProject = view.findViewById(R.id.rowSelectProject);
        rowMonth = view.findViewById(R.id.rowSelectMonth);
        rowYear = view.findViewById(R.id.rowSelectYear);

        rowProject.setOnClickListener(v -> spinnerProjects.performClick());
        rowMonth.setOnClickListener(v -> spinnerMonth.performClick());
        rowYear.setOnClickListener(v -> spinnerYear.performClick());

        setupProjectsFromArgsIfAny();
        setupProjects();
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

        loadPayments();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPayments();
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

    private void setupProjects() {
        List<Project> projects = FakeProjectStore.get().list();
        ArrayAdapter<Project> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, projects);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjects.setAdapter(ad);

        spinnerProjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                Project p = (Project) parent.getItemAtPosition(position);
                selectedProjectId = p.id;
                loadPayments();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Si tu viens d’un projet (args), pré-sélectionne
        if (!projects.isEmpty()) {
            int index = 0;
            if (selectedProjectId != null) {
                for (int i = 0; i < projects.size(); i++) {
                    if (selectedProjectId.equals(projects.get(i).id)) {
                        index = i;
                        break;
                    }
                }
            } else {
                selectedProjectId = projects.get(0).id;
            }
            spinnerProjects.setSelection(index);
        }
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
        years.add(0); // "Tous"
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

        List<?> list;

        if (filterMonth != null && filterYear != null) {
            list = vm.listByMonth(selectedProjectId, filterMonth, filterYear);
            adapter.submit(vm.listByMonth(selectedProjectId, filterMonth, filterYear));
            tvTotalReceived.setText("Reçu (filtré) : " + vm.money(vm.totalReceivedByMonth(selectedProjectId, filterMonth, filterYear)));
        } else {
            list = vm.list(selectedProjectId);
            adapter.submit(vm.list(selectedProjectId));
            tvTotalReceived.setText("Reçu : " + vm.money(vm.totalReceived(selectedProjectId)));
        }

        double expected = vm.expectedAmount(selectedProjectId);
        double received = vm.totalReceived(selectedProjectId);

        tvExpected.setText("Attendu : " + vm.money(expected));
        tvRemaining.setText("Reste : " + vm.money(vm.remaining(expected, received)));
        progress.setProgress(vm.progressPercent(expected, received));

        // Empty state
        boolean isEmpty = (list == null) || list.isEmpty();
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}