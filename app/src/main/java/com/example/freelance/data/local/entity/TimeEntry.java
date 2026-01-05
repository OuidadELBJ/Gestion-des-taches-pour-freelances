package com.example.freelance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "time_entries")
public class TimeEntry {
    @PrimaryKey
    @NonNull
    private String idTime;
    @NonNull
    private String taskId;
    private String projectId; // accès direct au projet
    private Date startTime;
    private Date endTime;
    private long duration;
    private boolean isRunning;
    private Date lastUpdated;
    private boolean isSynced;
    private boolean isPaused;
    private long pausedAccumulated; // temps total en pause

    private String note;

    public TimeEntry(@NonNull String idTime,
                     @NonNull String taskId,
                     String projectId,
                     Date startTime,
                     Date endTime,
                     long duration,
                     boolean isRunning,
                     Date lastUpdated,
                     boolean isSynced,
                     boolean isPaused,
                     long pausedAccumulated,
                     String note) {
        this.idTime = idTime;
        this.taskId = taskId;
        this.projectId = projectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.isRunning = isRunning;
        this.lastUpdated = lastUpdated;
        this.isSynced = isSynced;
        this.isPaused = isPaused;
        this.pausedAccumulated = pausedAccumulated;
        this.note = note;
    }

    @NonNull
    public String getIdTime() {
        return idTime;
    }

    public void setIdTime(@NonNull String idTime) {
        this.idTime = idTime;
    }

    @NonNull
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(@NonNull String taskId) {
        this.taskId = taskId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public long getPausedAccumulated() {
        return pausedAccumulated;
    }

    public void setPausedAccumulated(long pausedAccumulated) {
        this.pausedAccumulated = pausedAccumulated;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

