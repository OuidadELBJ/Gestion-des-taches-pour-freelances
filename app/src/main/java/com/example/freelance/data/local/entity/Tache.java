package com.example.freelance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "taches")
public class Tache {

    @PrimaryKey
    @NonNull
    private String idTache;
    @NonNull
    private String projectId;
    @NonNull
    private String title;
    private String description;
    private String priority;
    private String status;
    private Date deadline;
    private Date createdAt;
    private Date lastUpdated;
    private Boolean isSynced;


    public Tache(@NonNull String idTache,
                 @NonNull String projectId,
                 @NonNull String title,
                 String description,
                 String priority,
                 String status,
                 Date deadline,
                 Date createdAt,
                 Date lastUpdated,
                 Boolean isSynced) {
        this.idTache = idTache;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getIdTache() {
        return idTache;
    }

    public void setIdTache(@NonNull String idTache) {
        this.idTache = idTache;
    }

    @NonNull
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NonNull String projectId) {
        this.projectId = projectId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getSynced() {
        return isSynced;
    }

    public void setSynced(Boolean synced) {
        isSynced = synced;
    }
}

