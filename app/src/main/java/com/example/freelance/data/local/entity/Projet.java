package com.example.freelance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "projets")
public class Projet {

    @PrimaryKey
    @NonNull
    private String idProjet;
    @NonNull
    private String name;
    private String description;
    private String clientName;
    private double hourlyRate;
    private Date startDate;
    private Date endDate;
    private boolean isSynced;
    private Date lastUpdated;
    private String status;


    public Projet(@NonNull String idProjet,
                  @NonNull String name,
                  String description,
                  String clientName,
                  double hourlyRate,
                  Date startDate,
                  Date endDate,
                  boolean isSynced,
                  Date lastUpdated,
                  String status) {
        this.idProjet = idProjet;
        this.name = name;
        this.description = description;
        this.clientName = clientName;
        this.hourlyRate = hourlyRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isSynced = isSynced;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }
    @NonNull
    public String getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(@NonNull String idProjet) {
        this.idProjet = idProjet;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

