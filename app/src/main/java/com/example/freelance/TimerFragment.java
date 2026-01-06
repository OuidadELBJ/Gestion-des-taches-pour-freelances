package com.example.freelance;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.entity.Tache;
import com.example.freelance.data.local.repository.ProjetRepository;
import com.example.freelance.data.local.repository.TacheRepository;
import com.example.freelance.data.mapper.ProjetMapper;
import com.example.freelance.data.mapper.TacheMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.modele.Project;
import data.modele.Task;
import service.NotificationChannels;
import service.TimerForegroundService;
import ui.timer.SessionsAdapter;
import ui.timer.TimerViewModel;

public class TimerFragment extends Fragment {

    private Spinner spinnerProjects;
    private Spinner spinnerTasks;

    private TextView tvTimer, tvTotal, textEmptySessions;
    private TimerViewModel vm;
    private SessionsAdapter adapter;

    private String selectedProjectId;
    private String selectedTaskId;

    // args (quand on vient d’un projet)
    private String argProjectId;
    private String argTaskId;

    // timer réellement actif (vient du service)
    private String activeProjectId = null;
    private String activeTaskId = null;
    private long activeElapsed = 0L;

    private Button btnStart, btnPause, btnStop;

    private ActivityResultLauncher<String> notifPermissionLauncher;

    // ✅ Room
    private ProjetRepository projetRepo;
    private TacheRepository tacheRepo;

    private final List<Project> projectsCache = new ArrayList<>();
    private List<Task> tasksCache = new ArrayList<>();

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (TimerForegroundService.ACTION_TICK.equals(action)) {
                activeProjectId = intent.getStringExtra(TimerForegroundService.EXTRA_PROJECT_ID);
                activeTaskId = intent.getStringExtra(TimerForegroundService.EXTRA_TASK_ID);
                activeElapsed = intent.getLongExtra(TimerForegroundService.EXTRA_ELAPSED, 0L);
                updateTimerUi();

            } else if (TimerForegroundService.ACTION_STOPPED.equals(action)) {
                String pid = intent.getStringExtra(TimerForegroundService.EXTRA_PROJECT_ID);
                String tid = intent.getStringExtra(TimerForegroundService.EXTRA_TASK_ID);
                long start = intent.getLongExtra(TimerForegroundService.EXTRA_START, 0L);
                long end = intent.getLongExtra(TimerForegroundService.EXTRA_END, 0L);

                vm.addSession(pid, tid, start, end);

                activeProjectId = null;
                activeTaskId = null;
                activeElapsed = 0L;

                refreshSessions(selectedProjectId, selectedTaskId);
                updateTimerUi();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // args
        Bundle args = getArguments();
        if (args != null) {
            argProjectId = args.getString("projectId", null);
            argTaskId = args.getString("taskId", null);
        }

        NotificationChannels.ensure(requireContext());

        // ✅ VM (déjà Room/async pour sessions dans ton code)
        vm = new TimerViewModel(requireContext());

        // ✅ Room repos
        projetRepo = new ProjetRepository(requireContext());
        tacheRepo = new TacheRepository(requireContext());

        ImageButton back = view.findViewById(R.id.buttonBackTimer);
        if (argProjectId != null) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(v -> {
                NavController nav = Navigation.findNavController(v);
                nav.popBackStack();
            });
        } else {
            back.setVisibility(View.GONE);
        }

        spinnerProjects = view.findViewById(R.id.spinnerProjects);
        spinnerTasks = view.findViewById(R.id.spinnerTasks);

        tvTimer = view.findViewById(R.id.tvTimer);
        tvTotal = view.findViewById(R.id.tvTotal);
        textEmptySessions = view.findViewById(R.id.textEmptySessions);

        RecyclerView rv = view.findViewById(R.id.rvSessions);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SessionsAdapter();
        rv.setAdapter(adapter);

        btnStart = view.findViewById(R.id.btnStart);
        btnPause = view.findViewById(R.id.btnPause);
        btnStop = view.findViewById(R.id.btnStop);

        // permission notifications (Android 13+)
        notifPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (!granted) {
                        Toast.makeText(requireContext(),
                                "Active les notifications pour utiliser le timer.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setupProjects();

        btnStart.setOnClickListener(v -> startServiceAction(TimerForegroundService.ACTION_START));
        btnPause.setOnClickListener(v -> startServiceAction(TimerForegroundService.ACTION_PAUSE));
        btnStop.setOnClickListener(v -> startServiceAction(TimerForegroundService.ACTION_STOP));

        updateTimerUi();
    }

    private void setupProjects() {
        projetRepo.getAll(projets -> {
            projectsCache.clear();

            if (projets != null) {
                for (Projet e : projets) {
                    Project p = ProjetMapper.toModel(e);
                    if (p != null) projectsCache.add(p);
                }
            }

            ArrayAdapter<Project> ad = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    projectsCache
            );
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProjects.setAdapter(ad);

            spinnerProjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    Project p = (Project) parent.getItemAtPosition(position);
                    selectedProjectId = p.id;
                    setupTasksForProject(selectedProjectId);
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });

            if (!projectsCache.isEmpty()) {
                int index = 0;
                if (argProjectId != null) {
                    for (int i = 0; i < projectsCache.size(); i++) {
                        if (argProjectId.equals(projectsCache.get(i).id)) { index = i; break; }
                    }
                }
                spinnerProjects.setSelection(index);
                selectedProjectId = projectsCache.get(index).id;
                setupTasksForProject(selectedProjectId);
            } else {
                // aucun projet
                selectedProjectId = null;
                selectedTaskId = null;
                spinnerTasks.setAdapter(null);
                adapter.submit(Collections.emptyList());
                tvTotal.setText("Tâche: 00:00:00 • Projet: 00:00:00");
                textEmptySessions.setVisibility(View.VISIBLE);
                updateTimerUi();
            }
        });
    }

    private void setupTasksForProject(String projectId) {
        tacheRepo.getByProject(projectId, taches -> {
            tasksCache = new ArrayList<>();

            if (taches != null) {
                for (Tache e : taches) {
                    Task t = TacheMapper.toModel(e);
                    if (t != null) tasksCache.add(t);
                }
            }

            ArrayAdapter<Task> ad = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    tasksCache
            );
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTasks.setAdapter(ad);

            spinnerTasks.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    Task t = (Task) parent.getItemAtPosition(position);
                    selectedTaskId = t.id;
                    refreshSessions(selectedProjectId, selectedTaskId);
                    updateTimerUi();
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });

            if (!tasksCache.isEmpty()) {
                int index = 0;
                if (argTaskId != null) {
                    for (int i = 0; i < tasksCache.size(); i++) {
                        if (argTaskId.equals(tasksCache.get(i).id)) { index = i; break; }
                    }
                }
                spinnerTasks.setSelection(index);
                selectedTaskId = tasksCache.get(index).id;
                refreshSessions(projectId, selectedTaskId);
            } else {
                selectedTaskId = null;
                adapter.submit(Collections.emptyList());
                tvTotal.setText("Tâche: 00:00:00 • Projet: 00:00:00");
                textEmptySessions.setVisibility(View.VISIBLE);
            }

            updateTimerUi();
        });
    }

    private void refreshSessions(String projectId, String taskId) {
        if (projectId == null || taskId == null) return;

        vm.getSessionsByTask(taskId, sessions -> {
            adapter.submit(sessions);
            textEmptySessions.setVisibility((sessions == null || sessions.isEmpty()) ? View.VISIBLE : View.GONE);
        });

        vm.getTotals(projectId, taskId, totals -> {
            tvTotal.setText("Tâche: " + vm.format(totals.totalTask)
                    + " • Projet: " + vm.format(totals.totalProject));
        });
    }

    private void updateTimerUi() {
        boolean isSelectedActive = (activeTaskId != null && activeTaskId.equals(selectedTaskId));

        if (isSelectedActive) {
            tvTimer.setText(vm.format(activeElapsed));
            tvTimer.setTextColor(0xFF2F5BFF);
            btnPause.setEnabled(true);
            btnStop.setEnabled(true);
        } else {
            tvTimer.setText("00:00:00");
            tvTimer.setTextColor(0xFFAEB7D0);
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
        }

        btnStart.setEnabled(selectedProjectId != null && selectedTaskId != null);
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return true;
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 33) return;
        if (!hasNotificationPermission()) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void startServiceAction(String action) {
        if (!hasNotificationPermission()) {
            requestNotificationPermissionIfNeeded();
            Toast.makeText(requireContext(),
                    "Active les notifications pour utiliser le timer.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Context context = requireContext();
        Intent i = new Intent(context, TimerForegroundService.class);
        i.setAction(action);

        if (TimerForegroundService.ACTION_START.equals(action)) {
            if (selectedProjectId == null || selectedTaskId == null) {
                Toast.makeText(context, "Choisis un projet et une tâche", Toast.LENGTH_SHORT).show();
                return;
            }
            i.putExtra(TimerForegroundService.EXTRA_PROJECT_ID, selectedProjectId);
            i.putExtra(TimerForegroundService.EXTRA_TASK_ID, selectedTaskId);
        } else {
            if (activeProjectId == null || activeTaskId == null) {
                Toast.makeText(context, "Aucun timer actif", Toast.LENGTH_SHORT).show();
                return;
            }
            i.putExtra(TimerForegroundService.EXTRA_PROJECT_ID, activeProjectId);
            i.putExtra(TimerForegroundService.EXTRA_TASK_ID, activeTaskId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, i);
        } else {
            context.startService(i);
        }
    }

    @Override public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(TimerForegroundService.ACTION_TICK);
        f.addAction(TimerForegroundService.ACTION_STOPPED);
        ContextCompat.registerReceiver(requireContext(), receiver, f, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override public void onStop() {
        super.onStop();
        try { requireContext().unregisterReceiver(receiver); } catch (Exception ignored) {}
    }
}
