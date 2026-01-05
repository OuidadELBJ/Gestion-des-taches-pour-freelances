package com.example.freelance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")

public class NotificationReminder {
    public static final String TYPE_TASK = "TASK";
    public static final String TYPE_PROJECT = "PROJECT";
    public static final String TYPE_PAYMENT = "PAYMENT";
    public static final String STATUS_SCHEDULED = "SCHEDULED";
    public static final String STATUS_FIRED = "FIRED";
    public static final String STATUS_CANCELED = "CANCELED";

    @PrimaryKey
    @NonNull
    private String id;
    // TASK / PROJECT / PAYMENT
    @NonNull
    private String targetType;
    // Liens (nullable selon type)
    private String projectId;
    private String taskId;
    private String paymentId;
    // Quand la notif doit être affichée
    private long triggerAtMillis;
    // Exemple : 10m / 1h / 1d avant
    private long offsetMillis;
    private String title;
    private String message;
    // SCHEDULED / FIRED / CANCELED
    @NonNull
    private String status;
    // Pour WorkManager (enqueueUniqueWork)
    private String workName;
    private long createdAtMillis;
    private long lastUpdatedMillis;
    private boolean isSynced;
    public NotificationReminder(
            @NonNull String id,
            @NonNull String targetType,
            String projectId,
            String taskId,
            String paymentId,
            long triggerAtMillis,
            long offsetMillis,
            String title,
            String message,
            @NonNull String status,
            String workName,
            long createdAtMillis,
            long lastUpdatedMillis,
            boolean isSynced
    ) {
        this.id = id;
        this.targetType = targetType;
        this.projectId = projectId;
        this.taskId = taskId;
        this.paymentId = paymentId;
        this.triggerAtMillis = triggerAtMillis;
        this.offsetMillis = offsetMillis;
        this.title = title;
        this.message = message;
        this.status = status;
        this.workName = workName;
        this.createdAtMillis = createdAtMillis;
        this.lastUpdatedMillis = lastUpdatedMillis;
        this.isSynced = isSynced;
    }

    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull public String getTargetType() { return targetType; }
    public void setTargetType(@NonNull String targetType) { this.targetType = targetType; }

    public String getProjectId() { return projectId; }

    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public long getTriggerAtMillis() { return triggerAtMillis; }
    public void setTriggerAtMillis(long triggerAtMillis) { this.triggerAtMillis = triggerAtMillis; }

    public long getOffsetMillis() { return offsetMillis; }
    public void setOffsetMillis(long offsetMillis) { this.offsetMillis = offsetMillis; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @NonNull public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public String getWorkName() { return workName; }
    public void setWorkName(String workName) { this.workName = workName; }

    public long getCreatedAtMillis() { return createdAtMillis; }
    public void setCreatedAtMillis(long createdAtMillis) { this.createdAtMillis = createdAtMillis; }

    public long getLastUpdatedMillis() { return lastUpdatedMillis; }
    public void setLastUpdatedMillis(long lastUpdatedMillis) { this.lastUpdatedMillis = lastUpdatedMillis; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

}
