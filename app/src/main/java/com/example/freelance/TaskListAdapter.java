package com.example.freelance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(TaskUiModel task);
    }

    private List<TaskUiModel> tasks;
    private OnTaskClickListener listener;

    public TaskListAdapter(List<TaskUiModel> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskUiModel task = tasks.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textTaskTitle, textTaskDeadline, textTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskTitle = itemView.findViewById(R.id.textTaskTitle);
            textTaskDeadline = itemView.findViewById(R.id.textTaskDeadline);
            textTaskStatus = itemView.findViewById(R.id.textTaskStatus);
        }

        public void bind(final TaskUiModel task, final OnTaskClickListener listener) {
            textTaskTitle.setText(task.title);
            textTaskDeadline.setText(task.deadlineText);
            textTaskStatus.setText(task.statusText);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }
    }
}