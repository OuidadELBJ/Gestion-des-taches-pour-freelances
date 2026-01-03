package com.example.freelance;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.freelance.databinding.FragmentTaskDetailBinding;

import data.fake.FakeTaskStore;
import data.modele.Task;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding b;

    private String taskId = "";
    private String projectId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            taskId = args.getString("taskId", "");
            projectId = args.getString("projectId", "");
        }

        b.toolbarTaskDetail.setTitle("Détail tâche");
        b.toolbarTaskDetail.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        if (TextUtils.isEmpty(taskId)) {
            Toast.makeText(getContext(), "taskId manquant", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        Task t = FakeTaskStore.get().getById(taskId);
        if (t == null) {
            Toast.makeText(getContext(), "Tâche introuvable", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        bindTask(t);

        b.btnSaveTaskStatus.setOnClickListener(v -> {
            saveStatus();
            Toast.makeText(getContext(), "Statut enregistré ✅", Toast.LENGTH_SHORT).show();
        });
    }

    private void bindTask(Task t) {
        b.tvTaskTitle.setText(t.title);

        if (t.deadlineMillis > 0) {
            CharSequence d = android.text.format.DateFormat.format("dd/MM/yyyy", t.deadlineMillis);
            b.tvTaskDeadline.setText("Deadline : " + d);
        } else {
            b.tvTaskDeadline.setText("Pas de deadline");
        }


        // statut depuis prefs
        String status = getPrefs().getString(statusKey(), "À faire");
        selectStatus(status);
    }

    private void selectStatus(String status) {
        if ("En cours".equals(status)) b.radioInProgress.setChecked(true);
        else if ("Fait".equals(status)) b.radioDone.setChecked(true);
        else b.radioTodo.setChecked(true);
    }

    private void saveStatus() {
        String status = "À faire";
        if (b.radioInProgress.isChecked()) status = "En cours";
        else if (b.radioDone.isChecked()) status = "Fait";

        getPrefs().edit().putString(statusKey(), status).apply();
    }

    private String statusKey() {
        return "task_status_" + taskId;
    }

    private android.content.SharedPreferences getPrefs() {
        Context ctx = getContext();
        return ctx.getSharedPreferences("freelance_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}