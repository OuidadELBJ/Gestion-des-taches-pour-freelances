package com.example.freelance;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.entity.Tache;
import com.example.freelance.data.local.repository.ProjetRepository;
import com.example.freelance.data.local.repository.TacheRepository;
import com.example.freelance.data.repository.FirestoreRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import notifications.ReminderScheduler;

public class ProjectDetailFragment extends Fragment {

    public static final String ARG_PROJECT_ID = "projectId";
    public static final String ARG_PROJECT_NAME = "projectName";

    private String projectId = "";
    private String projectName = "Détail projet";

    private TacheRepository tacheRepo;
    private ProjetRepository projetRepo;

    // Notes (Room)
    private android.widget.EditText etProjectNotes;
    private com.google.android.material.button.MaterialButton btnSaveNotes;
    private com.google.android.material.button.MaterialButton btnContactSms, btnContactEmail;

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

    // cache projet Room
    private Projet currentProjet = null;

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

        tacheRepo = new TacheRepository(requireContext());
        projetRepo = new ProjetRepository(requireContext());

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

        btnContactSms = view.findViewById(R.id.btnContactSms);
        btnContactEmail = view.findViewById(R.id.btnContactEmail);

        setupTabs();
        setupTasksRecycler();

        // ===== Navigation CTA =====
        cardActionStartTimer.setOnClickListener(this::openTimerFragment);
        cardOpenTimerFromProject.setOnClickListener(this::openTimerFragment);

        cardActionAddPayment.setOnClickListener(this::openPaymentsFragment);
        cardOpenPaymentsFromProject.setOnClickListener(this::openPaymentsFragment);

        cardActionAddTask.setOnClickListener(this::openAddTask);

        // ===== Save notes (Room) =====
        btnSaveNotes.setOnClickListener(v -> {
            saveNotesToRoom();
            Toast.makeText(getContext(), "Notes enregistrées ✅", Toast.LENGTH_SHORT).show();
        });

        // charge projet + notes + header + contact
        loadProjectFromRoom();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasksFromRoom();
        loadProjectFromRoom();
    }

    // ✅ Remplacement des TOAST TODO
    private boolean onToolbarMenuItemClicked(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_archive_project) {
            archiveProject();
            return true;
        } else if (id == R.id.action_delete_project) {
            confirmDeleteProject();
            return true;
        } else if (id == R.id.action_edit_project) {
            openEditProject();
            return true;
        }
        return false;
    }

    private void openEditProject() {
        if (TextUtils.isEmpty(projectId)) return;

        // Si ton AddProjectActivity supporte l'édition, passe l'id
        Intent i = new Intent(requireContext(), AddProjectActivity.class);
        i.putExtra("mode", "edit");
        i.putExtra("projectId", projectId);
        startActivity(i);
    }

    private void archiveProject() {
        if (currentProjet == null) return;

        // 1) update local entity (Room)
        // ⚠️ si tes setters n'existent pas, dis-moi et je t'adapte via Dao Query
        currentProjet.setStatus("ARCHIVED");
        currentProjet.setReminderEnabled(false);
        currentProjet.setLastUpdated(new Date());
        currentProjet.setSynced(false);

        projetRepo.update(currentProjet);

        // 2) cancel reminders projet
        ReminderScheduler.cancelProject(requireContext().getApplicationContext(), currentProjet.getIdProjet());

        // 3) push Firestore (sinon au prochain sync tu reverras l'ancien statut)
        new FirestoreRepository().upsertProjet(currentProjet, new FirestoreRepository.OnComplete() {
            @Override public void onSuccess() { /* ok */ }
            @Override public void onError(Exception e) { /* ignore ou log */ }
        });

        Toast.makeText(getContext(), "Projet archivé ✅", Toast.LENGTH_SHORT).show();
        loadProjectFromRoom();
    }

    private void confirmDeleteProject() {
        if (currentProjet == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Supprimer le projet")
                .setMessage("Cette action est irréversible.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (d, w) -> deleteProjectNow())
                .show();
    }

    private void deleteProjectNow() {
        if (currentProjet == null) return;

        String pid = currentProjet.getIdProjet();

        // 1) delete Room
        projetRepo.delete(currentProjet);

        // 2) cancel reminders
        notifications.ReminderScheduler.onProjectDeleted(requireContext(), pid);

        // 3) delete Firestore (optionnel)
        new com.example.freelance.data.repository.FirestoreRepository()
                .deleteProjet(pid, new com.example.freelance.data.repository.FirestoreRepository.OnComplete() {
                    @Override public void onSuccess() {
                        // ok
                    }
                    @Override public void onError(Exception e) {
                        // ne bloque pas la suppression locale
                    }
                });

        android.widget.Toast.makeText(getContext(), "Projet supprimé ✅", android.widget.Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    // ✅ charge projet depuis Room (header + notes + contacts)
    private void loadProjectFromRoom() {
        if (TextUtils.isEmpty(projectId)) return;

        projetRepo.getById(projectId, p -> {
            currentProjet = p;

            if (p == null) {
                textProjectTitleHeader.setText(projectName);
                textProjectClientHeader.setText("-");
                textDeadlineHeader.setText("Deadline : -");
                textStatusBadgeHeader.setText("En cours");

                etProjectNotes.setText("");
                tvNotesPlaceholder.setVisibility(View.VISIBLE);

                setEnabledContactButton(btnContactSms, false);
                setEnabledContactButton(btnContactEmail, false);
                return;
            }

            String title = safe(p.getName());
            String client = safe(p.getClientName());
            String status = safe(p.getStatus());

            String deadlineTxt = "Deadline : -";
            if (p.getDeadline() != null) {
                deadlineTxt = "Deadline : " + android.text.format.DateFormat.format("dd/MM/yyyy", p.getDeadline());
            }

            textProjectTitleHeader.setText(title.isEmpty() ? projectName : title);
            textProjectClientHeader.setText(client.isEmpty() ? "-" : client);
            textDeadlineHeader.setText(deadlineTxt);
            textStatusBadgeHeader.setText(status.isEmpty() ? "En cours" : status);

            textAmountPlanned.setText("0 €");
            textAmountPaid.setText("0 €");
            textAmountRemaining.setText("0 €");
            textTimeTotal.setText("Temps total : 0h");
            textHourlyRate.setText("Taux horaire : -");
            progressGlobal.setProgress(0);
            textProgressGlobal.setText("0%");

            String notes = safe(p.getDescription());
            etProjectNotes.setText(notes);
            tvNotesPlaceholder.setVisibility(TextUtils.isEmpty(notes.trim()) ? View.VISIBLE : View.GONE);

            String phone = safe(p.getClientPhone());
            String email = safe(p.getClientEmail());

            String projectLabel = !TextUtils.isEmpty(title) ? title : projectName;
            String msg = "Bonjour" + (client.isEmpty() ? "" : " " + client) + ",\n\n"
                    + "Je vous contacte concernant le projet \"" + projectLabel + "\".\n"
                    + "Merci.\n";
            String subject = "Projet - " + projectLabel;

            setEnabledContactButton(btnContactSms, !phone.isEmpty());
            setEnabledContactButton(btnContactEmail, !email.isEmpty());

            btnContactSms.setOnClickListener(v -> {
                if (phone.isEmpty()) return;
                ui.payments.PaymentContactHelper.openSms(requireContext(), phone, msg);
            });

            btnContactEmail.setOnClickListener(v -> {
                if (email.isEmpty()) return;
                ui.payments.PaymentContactHelper.openEmail(requireContext(), email, subject, msg);
            });

            bindFakeTimeTab();
            bindFakePaymentsTab();
        });
    }

    private void saveNotesToRoom() {
        if (TextUtils.isEmpty(projectId)) return;
        String txt = (etProjectNotes.getText() == null) ? "" : etProjectNotes.getText().toString();

        projetRepo.updateDescription(projectId, txt, new Date());

        if (tvNotesPlaceholder != null) {
            tvNotesPlaceholder.setVisibility(TextUtils.isEmpty(txt.trim()) ? View.VISIBLE : View.GONE);
        }
    }

    private void setEnabledContactButton(com.google.android.material.button.MaterialButton b, boolean enabled) {
        if (b == null) return;
        b.setEnabled(enabled);
        b.setAlpha(enabled ? 1f : 0.45f);
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
        loadTasksFromRoom();
    }

    private void loadTasksFromRoom() {
        tasksUi.clear();

        if (TextUtils.isEmpty(projectId)) {
            if (taskAdapter != null) taskAdapter.notifyDataSetChanged();
            return;
        }

        tacheRepo.getByProject(projectId, entities -> {
            tasksUi.clear();

            if (entities != null) {
                for (Tache e : entities) {
                    String title = safe(e.getTitle());

                    String deadlineText = (e.getDeadline() != null)
                            ? "Deadline : " + android.text.format.DateFormat.format("dd/MM/yyyy", e.getDeadline())
                            : "Pas de deadline";

                    String status = safe(e.getStatus());
                    if (status.isEmpty()) status = "À faire";

                    boolean done = status.toLowerCase(Locale.FRANCE).contains("done")
                            || status.toLowerCase(Locale.FRANCE).contains("fait");

                    boolean highPriority = false;
                    String prio = e.getPriority();
                    if (prio != null) {
                        String p = prio.toLowerCase(Locale.FRANCE);
                        highPriority = p.contains("high") || p.contains("haute") || p.contains("urgent");
                    }

                    tasksUi.add(new TaskUiModel(title, deadlineText, highPriority, status, done));
                }
            }

            if (taskAdapter != null) taskAdapter.notifyDataSetChanged();
        });
    }

    private String safe(String s) { return (s == null) ? "" : s; }

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