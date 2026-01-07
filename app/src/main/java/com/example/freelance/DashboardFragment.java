package com.example.freelance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    private TextView textProjectsCount, textTimeThisWeekValue, textPaymentsThisMonthValue;
    private TextView textUrgentTasksTitle, textTaskLine1, textTaskLine2, textSeeAllTasks;
    private ImageView imageProfile;

    // ✅ Projet récent (UI)
    private TextView textRecentProjectTitle, textRecentProjectSubtitle;

    private com.example.freelance.data.local.repository.DashboardRepository dashRepo;

    private MaterialCardView cardStatsProjects, cardStatsTime, cardStatsPayments;
    private MaterialCardView cardTodayTasks, cardRecentProject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboard();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Stats ---
        textProjectsCount = view.findViewById(R.id.textProjectsCount);
        textTimeThisWeekValue = view.findViewById(R.id.textTimeThisWeekValue);
        textPaymentsThisMonthValue = view.findViewById(R.id.textPaymentsThisMonthValue);

        // --- Tâches du jour ---
        textUrgentTasksTitle = view.findViewById(R.id.textUrgentTasksTitle);
        textTaskLine1 = view.findViewById(R.id.textTaskLine1);
        textTaskLine2 = view.findViewById(R.id.textTaskLine2);
        textSeeAllTasks = view.findViewById(R.id.textSeeAllTasks);

        // --- Projet récent ---
        textRecentProjectTitle = view.findViewById(R.id.textRecentProjectTitle);
        textRecentProjectSubtitle = view.findViewById(R.id.textRecentProjectSubtitle);

        // --- Profil ---
        imageProfile = view.findViewById(R.id.imageProfile);

        // --- Cards cliquables ---
        cardStatsProjects = view.findViewById(R.id.cardStatsProjects);
        cardStatsTime = view.findViewById(R.id.cardStatsTime);
        cardStatsPayments = view.findViewById(R.id.cardStatsPayments);
        cardTodayTasks = view.findViewById(R.id.cardTodayTasks);
        cardRecentProject = view.findViewById(R.id.cardRecentProject);

        dashRepo = new com.example.freelance.data.local.repository.DashboardRepository(requireContext());

        // ---------- Navigation fixe ----------
        imageProfile.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboardFragment_to_profileFragment
                )
        );

        cardStatsProjects.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboardFragment_to_projectListFragment
                )
        );

        cardStatsPayments.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboardFragment_to_paymentsFragment
                )
        );

        cardStatsTime.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.timerFragment)
        );

        View.OnClickListener goProjects = v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboardFragment_to_projectListFragment
                );

        cardTodayTasks.setOnClickListener(goProjects);
        textSeeAllTasks.setOnClickListener(goProjects);

        // ✅ charge les vraies données
        loadDashboard();
    }

    private void loadDashboard() {
        if (dashRepo == null) return;

        dashRepo.load(data -> {
            // --- Stats ---
            textProjectsCount.setText(String.valueOf(data.projectsCount));
            textPaymentsThisMonthValue.setText(formatEuro(data.paymentsThisMonth));
            textTimeThisWeekValue.setText(formatDuration(data.timeThisWeekMillis));

            // --- Projet récent ---
            if (data.latestProject != null) {
                textRecentProjectTitle.setText(safe(data.latestProject.getName()));
                textRecentProjectSubtitle.setText("Voir le détail du projet");

                cardRecentProject.setEnabled(true);
                cardRecentProject.setAlpha(1f);

                cardRecentProject.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putString("projectId", data.latestProject.getIdProjet());
                    args.putString("projectName", data.latestProject.getName());
                    Navigation.findNavController(v).navigate(R.id.projectDetailFragment, args);
                });

            } else {
                textRecentProjectTitle.setText("Aucun projet");
                textRecentProjectSubtitle.setText("Crée ton premier projet");

                cardRecentProject.setEnabled(false);
                cardRecentProject.setAlpha(0.6f);
                cardRecentProject.setOnClickListener(null);
            }

            // --- Urgent tasks ---
            if (data.urgentCount <= 0) {
                textUrgentTasksTitle.setText("Aucune tâche urgente");
                textTaskLine1.setText("");
                textTaskLine2.setText("");
            } else {
                textUrgentTasksTitle.setText(data.urgentCount + " tâche(s) à faire avant demain");

                String l1 = (data.urgentTasks != null && data.urgentTasks.size() >= 1)
                        ? "• " + safe(data.urgentTasks.get(0).getTitle())
                        : "";
                String l2 = (data.urgentTasks != null && data.urgentTasks.size() >= 2)
                        ? "• " + safe(data.urgentTasks.get(1).getTitle())
                        : "";

                textTaskLine1.setText(l1);
                textTaskLine2.setText(l2);
            }
        });
    }

    private String formatEuro(double v) {
        return String.format(java.util.Locale.FRANCE, "%,.2f €", v);
    }

    private String formatDuration(long millis) {
        long totalMin = millis / 60000L;
        long h = totalMin / 60;
        long m = totalMin % 60;
        return h + "h " + m;
    }

    private String safe(String s) { return s == null ? "" : s; }
}