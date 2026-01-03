package com.example.freelance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import data.fake.FakeTaskStore;
import data.modele.Task;

public class ProjectDetailFragment extends Fragment {

    public static final String ARG_PROJECT_ID = "projectId";
    public static final String ARG_PROJECT_NAME = "projectName";

    private static final String PREFS_NOTES = "project_notes_prefs";
    private static final String KEY_NOTES_PREFIX = "notes_";

    private String projectId = "";
    private String projectName = "Détail projet";

    // Notes
    private android.widget.EditText etProjectNotes;
    private com.google.android.material.button.MaterialButton btnSaveNotes;

    // Header
    private TextView textProjectTitleHeader, textProjectClientHeader, textDeadlineHeader;
    private TextView textAmountPlanned, textAmountPaid, textAmountRemaining;
    private TextView textTimeTotal, textHourlyRate, textProgressGlobal;
    private com.google.android.material.chip.Chip textStatusBadgeHeader;
    private ProgressBar progressGlobal;

    // Tabs / contenu
    private TabLayout tabLayout;
    private RecyclerView recyclerTasks;
    private LinearLayout layoutTimeTab, layoutPaymentsTab, layoutNotesTab;

    // Actions rapides
    private MaterialCardView cardActionAddTask, cardActionStartTimer, cardActionAddPayment;

    // Time tab
    private TextView tvProjectTimeSummary;
    private MaterialCardView cardOpenTimerFromProject;

    // Payments tab
    private TextView tvPaymentsExpected, tvPaymentsReceived, tvPaymentsRemaining;
    private MaterialCardView cardOpenPaymentsFromProject;

    // Notes tab (état vide)
    private TextView tvNotesPlaceholder;

    private TaskAdapter taskAdapter;
    private final List<TaskUiModel> tasksUi = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            projectId = args.getString(ARG_PROJECT_ID, "");
            projectName = args.getString(ARG_PROJECT_NAME, "Détail projet");
        }

        // ===== TOOLBAR =====
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarProjectDetail);
        toolbar.setTitle(projectName);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_project_detail);
        toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClicked);

        // ===== HEADER =====
        textProjectTitleHeader = view.findViewById(R.id.textProjectTitleHeader);
        textProjectClientHeader = view.findViewById(R.id.textProjectClientHeader);
        textStatusBadgeHeader = view.findViewById(R.id.textStatusBadgeHeader);
        textDeadlineHeader = view.findViewById(R.id.textDeadlineHeader);

        textAmountPlanned = view.findViewById(R.id.textAmountPlanned);
        textAmountPaid = view.findViewById(R.id.textAmountPaid);
        textAmountRemaining = view.findViewById(R.id.textAmountRemaining);

        textTimeTotal = view.findViewById(R.id.textTimeTotal);
        textHourlyRate = view.findViewById(R.id.textHourlyRate);

        progressGlobal = view.findViewById(R.id.progressGlobal);
        textProgressGlobal = view.findViewById(R.id.textProgressGlobal);

        // ===== ACTIONS RAPIDES =====
        cardActionAddTask = view.findViewById(R.id.cardActionAddTask);
        cardActionStartTimer = view.findViewById(R.id.cardActionStartTimer);
        cardActionAddPayment = view.findViewById(R.id.cardActionAddPayment);

        // ===== TABS / CONTENU =====
        tabLayout = view.findViewById(R.id.tabLayoutProject);
        recyclerTasks = view.findViewById(R.id.recyclerTasks);
        layoutTimeTab = view.findViewById(R.id.layoutTimeTab);
        layoutPaymentsTab = view.findViewById(R.id.layoutPaymentsTab);
        layoutNotesTab = view.findViewById(R.id.layoutNotesTab);

        // ===== IDs tabs =====
        tvProjectTimeSummary = view.findViewById(R.id.tvProjectTimeSummary);
        cardOpenTimerFromProject = view.findViewById(R.id.cardOpenTimerFromProject);

        tvPaymentsExpected = view.findViewById(R.id.tvPaymentsExpected);
        tvPaymentsReceived = view.findViewById(R.id.tvPaymentsReceived);
        tvPaymentsRemaining = view.findViewById(R.id.tvPaymentsRemaining);
        cardOpenPaymentsFromProject = view.findViewById(R.id.cardOpenPaymentsFromProject);

        tvNotesPlaceholder = view.findViewById(R.id.tvNotesPlaceholder);
        etProjectNotes = view.findViewById(R.id.etProjectNotes);
        btnSaveNotes = view.findViewById(R.id.btnSaveNotes);

        // ===== Bind fake data header/tabs (temp) =====
        bindFakeHeader();
        bindFakeTimeTab();
        bindFakePaymentsTab();

        setupTabs();
        setupTasksRecycler();

        // ===== Navigation CTA =====
        cardActionStartTimer.setOnClickListener(this::openTimerFragment);
        cardOpenTimerFromProject.setOnClickListener(this::openTimerFragment);

        cardActionAddPayment.setOnClickListener(this::openPaymentsFragment);
        cardOpenPaymentsFromProject.setOnClickListener(this::openPaymentsFragment);

        cardActionAddTask.setOnClickListener(this::openAddTask);

        // ===== Notes =====
        loadNotes(); // pré-remplit l’EditText
        btnSaveNotes.setOnClickListener(v -> {
            saveNotes();
            Toast.makeText(getContext(), "Notes enregistrées ✅", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ refresh liste tâches + notes quand on revient (AddTaskFragment etc.)
        loadTasksFromStore();
        loadNotes();
    }

    // ---------- Menu toolbar ----------
    private boolean onToolbarMenuItemClicked(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_project) {
            Toast.makeText(getContext(), "Modifier (TODO)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_archive_project) {
            Toast.makeText(getContext(), "Archiver (TODO)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete_project) {
            Toast.makeText(getContext(), "Supprimer (TODO)", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // ---------- Header fake ----------
    private void bindFakeHeader() {
        textProjectTitleHeader.setText(projectName);
        textProjectClientHeader.setText("Client A");
        textStatusBadgeHeader.setText("En cours");
        textDeadlineHeader.setText("Deadline : 15/01/2026");

        textAmountPlanned.setText("1 500 €");
        textAmountPaid.setText("800 €");
        textAmountRemaining.setText("700 €");

        textTimeTotal.setText("Temps total : 12h30");
        textHourlyRate.setText("Taux horaire : 60 €/h");

        progressGlobal.setProgress(40);
        textProgressGlobal.setText("40%");
    }

    private void bindFakeTimeTab() {
        tvProjectTimeSummary.setText("Tâche: 00:00:00 • Projet: 12:30:00");
    }

    private void bindFakePaymentsTab() {
        tvPaymentsExpected.setText("Attendu : 1 500 €");
        tvPaymentsReceived.setText("Reçu : 800 €");
        tvPaymentsRemaining.setText("Restant : 700 €");
    }

    // ---------- Tabs ----------
    private void setupTabs() {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Tâches"));
        tabLayout.addTab(tabLayout.newTab().setText("Temps"));
        tabLayout.addTab(tabLayout.newTab().setText("Paiements"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { showTab(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        showTab(0);
    }

    private void showTab(int position) {
        recyclerTasks.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        layoutTimeTab.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        layoutPaymentsTab.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        layoutNotesTab.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
    }

    // ---------- Recycler tâches ----------
    private void setupTasksRecycler() {
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(tasksUi);
        recyclerTasks.setAdapter(taskAdapter);

        loadTasksFromStore();
    }

    private void loadTasksFromStore() {
        tasksUi.clear();

        if (TextUtils.isEmpty(projectId)) {
            if (taskAdapter != null) taskAdapter.notifyDataSetChanged();
            return;
        }

        List<Task> tasks = FakeTaskStore.get().listByProject(projectId);
        for (Task t : tasks) {
            tasksUi.add(new TaskUiModel(
                    safe(t.title),
                    (t.deadlineMillis > 0
                            ? "Deadline : " + android.text.format.DateFormat.format("dd/MM/yyyy", t.deadlineMillis)
                            : "Pas de deadline"),
                    false,
                    "À faire",
                    false
            ));
        }

        if (taskAdapter != null) taskAdapter.notifyDataSetChanged();
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    // ---------- Notes (SharedPreferences) ----------
    private void loadNotes() {
        if (etProjectNotes == null || tvNotesPlaceholder == null) return;

        String txt = "";
        if (!TextUtils.isEmpty(projectId)) {
            SharedPreferences sp = requireContext().getSharedPreferences(PREFS_NOTES, Context.MODE_PRIVATE);
            txt = sp.getString(KEY_NOTES_PREFIX + projectId, "");
        }

        etProjectNotes.setText(txt);

        // état vide
        boolean empty = TextUtils.isEmpty(txt.trim());
        tvNotesPlaceholder.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void saveNotes() {
        if (etProjectNotes == null) return;

        if (TextUtils.isEmpty(projectId)) {
            Toast.makeText(getContext(), "Projet invalide (projectId vide)", Toast.LENGTH_SHORT).show();
            return;
        }

        String txt = etProjectNotes.getText() == null ? "" : etProjectNotes.getText().toString();

        SharedPreferences sp = requireContext().getSharedPreferences(PREFS_NOTES, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_NOTES_PREFIX + projectId, txt).apply();

        if (tvNotesPlaceholder != null) {
            tvNotesPlaceholder.setVisibility(TextUtils.isEmpty(txt.trim()) ? View.VISIBLE : View.GONE);
        }
    }

    // ---------- Navigation ----------
    private void openTimerFragment(View clickedView) {
        NavController nav = Navigation.findNavController(clickedView);
        Bundle b = new Bundle();
        b.putString("projectId", projectId);
        b.putString("projectName", projectName);
        nav.navigate(R.id.timerFragment, b);
    }

    private void openPaymentsFragment(View clickedView) {
        NavController nav = Navigation.findNavController(clickedView);
        Bundle b = new Bundle();
        b.putString("projectId", projectId);
        b.putString("projectName", projectName);
        nav.navigate(R.id.paymentsFragment, b);
    }

    private void openAddTask(View clickedView) {
        NavController nav = Navigation.findNavController(clickedView);
        Bundle b = new Bundle();
        b.putString("projectId", projectId);
        b.putString("projectName", projectName);
        nav.navigate(R.id.addTaskFragment, b);
    }

    // ---------- UI model + Adapter ----------
    private static class TaskUiModel {
        final String title, deadline, status;
        final boolean highPriority;
        boolean done;

        TaskUiModel(String title, String deadline, boolean highPriority, String status, boolean done) {
            this.title = title;
            this.deadline = deadline;
            this.highPriority = highPriority;
            this.status = status;
            this.done = done;
        }
    }

    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<TaskUiModel> items;

        TaskAdapter(List<TaskUiModel> items) { this.items = items; }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_project_task, parent, false);
            return new TaskViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            TaskUiModel t = items.get(position);

            holder.textTitle.setText(t.title);
            holder.textDeadline.setText(t.deadline);
            holder.textStatus.setText(t.status);
            holder.checkDone.setChecked(t.done);
            holder.textPriority.setVisibility(t.highPriority ? View.VISIBLE : View.GONE);

            // Task detail à brancher ensuite (TaskDetailFragment)
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(v.getContext(), "Détail tâche (TODO)", Toast.LENGTH_SHORT).show()
            );

            holder.checkDone.setOnCheckedChangeListener((buttonView, isChecked) -> t.done = isChecked);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            final TextView textTitle, textDeadline, textStatus, textPriority;
            final android.widget.CheckBox checkDone;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.textTaskTitle);
                textDeadline = itemView.findViewById(R.id.textTaskDeadline);
                textStatus = itemView.findViewById(R.id.textTaskStatus);
                textPriority = itemView.findViewById(R.id.textPriorityBadge);
                checkDone = itemView.findViewById(R.id.checkTaskDone);
            }
        }
    }
}