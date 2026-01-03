package com.example.freelance;

public class ProjectUiModel {

    public enum Status {
        IN_PROGRESS,
        LATE,
        DONE,
        BILLING
    }

    public final String id;
    public final String name;
    public final String client;
    public final String deadlineText;      // ex: "Deadline : 15/01/2026"
    public final String budgetText;        // ex: "1 200 €"
    public final String trackedTimeText;   // ex: "5h suivies"
    public final String tasksSummaryText;  // ex: "Tâches : 2/5"
    public final String lastActivityText;  // ex: "Dernière activité : hier"
    public final int progressPercent;      // 0..100
    public final Status status;

    public ProjectUiModel(String id,
                          String name,
                          String client,
                          String deadlineText,
                          String budgetText,
                          String trackedTimeText,
                          String tasksSummaryText,
                          String lastActivityText,
                          int progressPercent,
                          Status status) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.deadlineText = deadlineText;
        this.budgetText = budgetText;
        this.trackedTimeText = trackedTimeText;
        this.tasksSummaryText = tasksSummaryText;
        this.lastActivityText = lastActivityText;
        this.progressPercent = progressPercent;
        this.status = status;
    }

    public boolean isLate() {
        return status == Status.LATE;
    }

    public boolean isDone() {
        return status == Status.DONE;
    }
}