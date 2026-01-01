package com.example.freelance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "paiements")
public class Paiement {
    @PrimaryKey
    @NonNull
    private String idPaiement;
    @NonNull
    private String projectId;
    private double amount;
    private Date date;
    private String method;
    private String note;
    private Date lastUpdated;
    private boolean isSynced;

    public Paiement(@NonNull String idPaiement,
                    @NonNull String projectId,
                    double amount,
                    Date date,
                    String method,
                    String note,
                    Date lastUpdated,
                    boolean isSynced) {
        this.idPaiement = idPaiement;
        this.projectId = projectId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.note = note;
        this.lastUpdated = lastUpdated;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(@NonNull String idPaiement) {
        this.idPaiement = idPaiement;
    }

    @NonNull
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NonNull String projectId) {
        this.projectId = projectId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}

