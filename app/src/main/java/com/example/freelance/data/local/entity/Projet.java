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

    // Type de facturation
    private int billingType;
// ex: 0 = PROJECT, 1 = HOUR, 2 = DAY, 3 = MONTH

    private double budgetAmount;

    private double rate; // taux utilisé selon billingType

    private double estimatedHours;
    private double estimatedDays;
    private double estimatedMonths;

    private Date deadline;

    private boolean reminderEnabled;
    private boolean useDefaultOffsets;

    // offsets personnalisés (ex: "7,3,1")
    private String customOffsets;
    private String clientEmail;
    private String clientPhone;


    public Projet(
            @NonNull String idProjet,
            @NonNull String name,
            String description,
            String clientName,
            int billingType,
            double budgetAmount,
            double rate,
            double estimatedHours,
            double estimatedDays,
            double estimatedMonths,
            Date deadline,
            boolean reminderEnabled,
            boolean useDefaultOffsets,
            String customOffsets,
            String status,
            boolean isSynced,
            Date lastUpdated,
            String clientEmail,
            String clientPhone
    ) {
        this.idProjet = idProjet;
        this.name = name;
        this.description = description;
        this.clientName = clientName;
        this.billingType = billingType;
        this.budgetAmount = budgetAmount;
        this.rate = rate;
        this.estimatedHours = estimatedHours;
        this.estimatedDays = estimatedDays;
        this.estimatedMonths = estimatedMonths;
        this.deadline = deadline;
        this.reminderEnabled = reminderEnabled;
        this.useDefaultOffsets = useDefaultOffsets;
        this.customOffsets = customOffsets;
        this.status = status;
        this.isSynced = isSynced;
        this.lastUpdated = lastUpdated;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
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

    public int getBillingType() {
        return billingType;
    }

    public void setBillingType(int billingType) {
        this.billingType = billingType;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public double getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(double estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    public double getEstimatedMonths() {
        return estimatedMonths;
    }

    public void setEstimatedMonths(double estimatedMonths) {
        this.estimatedMonths = estimatedMonths;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public boolean isUseDefaultOffsets() {
        return useDefaultOffsets;
    }

    public void setUseDefaultOffsets(boolean useDefaultOffsets) {
        this.useDefaultOffsets = useDefaultOffsets;
    }

    public String getCustomOffsets() {
        return customOffsets;
    }

    public void setCustomOffsets(String customOffsets) {
        this.customOffsets = customOffsets;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
}

