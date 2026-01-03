package com.example.freelance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(ProjectUiModel project);
    }

    private final List<ProjectUiModel> items = new ArrayList<>();
    private final OnProjectClickListener listener;

    public ProjectListAdapter(OnProjectClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProjectUiModel> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectUiModel p = items.get(position);
        holder.bind(p, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        final CardView card;
        final TextView textProjectName;
        final TextView textStatusBadge;
        final ImageView iconChevron;
        final TextView textClient;
        final TextView textDeadline;
        final TextView textBudget;
        final TextView textTrackedTime;
        final ProgressBar progressProject;
        final TextView textProgress;
        final TextView textTasksSummary;
        final TextView textLastActivity;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            card             = itemView.findViewById(R.id.cardProject);
            textProjectName  = itemView.findViewById(R.id.textProjectName);
            textStatusBadge  = itemView.findViewById(R.id.textStatusBadge);
            iconChevron      = itemView.findViewById(R.id.iconChevron);
            textClient       = itemView.findViewById(R.id.textClient);
            textDeadline     = itemView.findViewById(R.id.textDeadline);
            textBudget       = itemView.findViewById(R.id.textBudget);
            textTrackedTime  = itemView.findViewById(R.id.textTrackedTime);
            progressProject  = itemView.findViewById(R.id.progressProject);
            textProgress     = itemView.findViewById(R.id.textProgress);
            textTasksSummary = itemView.findViewById(R.id.textTasksSummary);
            textLastActivity = itemView.findViewById(R.id.textLastActivity);
        }

        void bind(ProjectUiModel p, OnProjectClickListener listener) {
            textProjectName.setText(p.name);
            textClient.setText(p.client);
            textDeadline.setText(p.deadlineText);
            textBudget.setText("• " + p.budgetText);
            textTrackedTime.setText("• " + p.trackedTimeText);
            textTasksSummary.setText(p.tasksSummaryText);
            textLastActivity.setText(p.lastActivityText);

            progressProject.setProgress(p.progressPercent);
            textProgress.setText(p.progressPercent + "%");

            // Badge statut
            switch (p.status) {
                case IN_PROGRESS:
                    textStatusBadge.setText("En cours");
                    textStatusBadge.setBackgroundColor(
                            itemView.getResources().getColor(R.color.cardBackground));
                    textStatusBadge.setTextColor(
                            itemView.getResources().getColor(R.color.color_primary));
                    break;

                case LATE:
                    textStatusBadge.setText("En retard");
                    // léger accent orange sur le texte
                    textStatusBadge.setBackgroundColor(
                            itemView.getResources().getColor(R.color.cardBackground));
                    textStatusBadge.setTextColor(
                            itemView.getResources().getColor(R.color.color_warning_orange));
                    break;

                case DONE:
                    textStatusBadge.setText("Terminé");
                    textStatusBadge.setBackgroundColor(
                            itemView.getResources().getColor(R.color.cardBackground));
                    textStatusBadge.setTextColor(
                            itemView.getResources().getColor(R.color.color_done_green));
                    break;

                case BILLING:
                    textStatusBadge.setText("Facturation");
                    textStatusBadge.setBackgroundColor(
                            itemView.getResources().getColor(R.color.cardBackground));
                    textStatusBadge.setTextColor(
                            itemView.getResources().getColor(R.color.textSecondary));
                    break;
            }

            View.OnClickListener click = v -> {
                if (listener != null) listener.onProjectClick(p);
            };
            card.setOnClickListener(click);
            iconChevron.setOnClickListener(click);
        }
    }
}