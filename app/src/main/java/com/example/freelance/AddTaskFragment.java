package com.example.freelance;

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

import com.example.freelance.data.local.entity.Tache;
import com.example.freelance.data.local.repository.TacheRepository;
import com.example.freelance.data.mapper.TacheMapper;
import com.example.freelance.databinding.FragmentAddTaskBinding;

import java.util.concurrent.TimeUnit;

import data.modele.Task;

public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding b;

    private String projectId = "";
    private String projectName = "Projet";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentAddTaskBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            projectId = args.getString("projectId", "");
            projectName = args.getString("projectName", "Projet");
        }

        b.toolbarAddTask.setTitle("Ajouter tâche");
        b.toolbarAddTask.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        b.textProjectInfo.setText("Projet : " + projectName);

        b.btnCancelTask.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        b.btnCreateTask.setOnClickListener(v -> saveTaskAndBack(v));
    }

    private void saveTaskAndBack(View clickedView) {
        String title = b.etTaskTitle.getText() == null ? "" : b.etTaskTitle.getText().toString().trim();
        String hoursStr = b.etTaskEstimateHours.getText() == null ? "" : b.etTaskEstimateHours.getText().toString().trim();
        String deadlineDaysStr = b.etTaskDeadlineDays.getText() == null ? "" : b.etTaskDeadlineDays.getText().toString().trim();

        if (TextUtils.isEmpty(projectId)) {
            Toast.makeText(getContext(), "Projet manquant (projectId vide)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Titre requis", Toast.LENGTH_SHORT).show();
            return;
        }

        double hours = parseDouble(hoursStr, 0);
        long estimatedMillis = (long) (hours * 60 * 60 * 1000);

        int dueInDays = (int) parseDouble(deadlineDaysStr, 0);
        long now = System.currentTimeMillis();
        long deadlineMillis = (dueInDays > 0)
                ? now + TimeUnit.DAYS.toMillis(dueInDays)
                : 0L;

        boolean reminderEnabled = (deadlineMillis > 0);

        String id = "t" + System.currentTimeMillis();

        Task t = new Task(id, projectId, title, estimatedMillis, deadlineMillis, reminderEnabled);
        t.useDefaultOffsets = true;

        // ✅ Room insert + onDone (important)
        TacheRepository repo = new TacheRepository(requireContext());
        Tache entity = TacheMapper.toEntity(t);

        repo.insert(entity, () -> {
            Toast.makeText(getContext(), "Tâche ajoutée ✅", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(clickedView).navigateUp();
        });
    }

    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return def; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
