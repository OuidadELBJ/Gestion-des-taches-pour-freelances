package com.example.freelance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(ProjectUiModel project);
    }

    private final List<ProjectUiModel> projects;
    private final OnProjectClickListener listener;

    public ProjectListAdapter(List<ProjectUiModel> projects, OnProjectClickListener listener) {
        this.projects = projects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectUiModel project = projects.get(position);
        holder.bind(project, listener);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView textProjectName, textClient, textDeadline, textProgress;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            textProjectName = itemView.findViewById(R.id.textProjectName);
            textClient      = itemView.findViewById(R.id.textClient);
            textDeadline    = itemView.findViewById(R.id.textDeadline);
            textProgress    = itemView.findViewById(R.id.textProgress);
        }

        public void bind(final ProjectUiModel project,
                         final OnProjectClickListener listener) {

            textProjectName.setText(project.name);
            textClient.setText(project.client);
            textDeadline.setText(project.deadlineText);
            textProgress.setText(project.progressText);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProjectClick(project);
                }
            });
        }
    }
}