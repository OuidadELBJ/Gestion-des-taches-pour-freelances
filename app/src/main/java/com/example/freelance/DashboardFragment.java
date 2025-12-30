package com.example.freelance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private TextView textTimeThisWeekValue;
    private TextView textPaymentsThisMonthValue;
    private TextView textUrgentTasksTitle;
    private TextView textUrgentTasksSubtitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTimeThisWeekValue = view.findViewById(R.id.textTimeThisWeekValue);
        textPaymentsThisMonthValue = view.findViewById(R.id.textPaymentsThisMonthValue);
        textUrgentTasksTitle = view.findViewById(R.id.textUrgentTasksTitle);
        textUrgentTasksSubtitle = view.findViewById(R.id.textUrgentTasksSubtitle);

        // Fake data pour l’instant
        textTimeThisWeekValue.setText("12h 30 (fake)");
        textPaymentsThisMonthValue.setText("1 200 € (fake)");
        textUrgentTasksTitle.setText("2 tâches à faire avant demain (fake)");
        textUrgentTasksSubtitle.setText(
                "- Finaliser maquette App mobile B\n- Préparer devis Branding C"
        );
    }
}