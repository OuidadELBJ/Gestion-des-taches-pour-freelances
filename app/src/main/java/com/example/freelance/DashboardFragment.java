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

        // --- Profil ---
        imageProfile = view.findViewById(R.id.imageProfile);

        // --- Cards cliquables ---
        cardStatsProjects = view.findViewById(R.id.cardStatsProjects);
        cardStatsTime = view.findViewById(R.id.cardStatsTime);
        cardStatsPayments = view.findViewById(R.id.cardStatsPayments);
        cardTodayTasks = view.findViewById(R.id.cardTodayTasks);
        cardRecentProject = view.findViewById(R.id.cardRecentProject);

        // Fake data
        textProjectsCount.setText("6");
        textTimeThisWeekValue.setText("12h 30");
        textPaymentsThisMonthValue.setText("1 200 €");

        textUrgentTasksTitle.setText("2 tâches à faire avant demain");
        textTaskLine1.setText("• Finaliser maquette App mobile B");
        textTaskLine2.setText("• Préparer devis Branding C");

        // ---------- Navigation ----------
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
                Navigation.findNavController(v).navigate(
                        R.id.timerFragment
                )
        );

        View.OnClickListener goProjects = v ->
                Navigation.findNavController(v).navigate(
                        R.id.action_dashboardFragment_to_projectListFragment
                );

        cardTodayTasks.setOnClickListener(goProjects);
        textSeeAllTasks.setOnClickListener(goProjects);

        cardRecentProject.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("projectId", "P1"); // TODO remplacer par un vrai id
            Navigation.findNavController(v).navigate(R.id.projectDetailFragment, args);
        });
    }
}