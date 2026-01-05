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

    private Date dueDate;      // date d’échéance
    private boolean paid;      // payé ou non
    private String clientEmail;
    private String clientPhone;
    private String invoiceRef;

    public Paiement(@NonNull String idPaiement,
                    @NonNull String projectId,
                    double amount,
                    Date date,
                    String method,
                    String note,
                    Date lastUpdated,
                    boolean isSynced,
                    Date dueDate,
                    boolean paid,
                    String clientEmail,
                    String clientPhone,
                    String invoiceRef) {
        this.idPaiement = idPaiement;
        this.projectId = projectId;
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.note = note;
        this.lastUpdated = lastUpdated;
        this.isSynced = isSynced;
        this.dueDate = dueDate;
        this.paid = paid;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.invoiceRef = invoiceRef;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
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

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }
}

