package com.example.freelance.data.mapper;

import com.example.freelance.data.local.entity.Projet;

import java.util.Date;

import data.modele.Project;

public class ProjetMapper {

    // Entity -> Model (si tu veux encore utiliser data.modele.Project quelque part)
    public static Project toModel(Projet e) {
        if (e == null) return null;

        Project p = new Project(e.getIdProjet(), e.getName());
        p.clientName = safe(e.getClientName());
        p.clientEmail = safe(e.getClientEmail());
        p.clientPhone = safe(e.getClientPhone());

        p.status = safe(e.getStatus());
        p.notes = safe(e.getDescription());

        p.billingType = e.getBillingType();
        p.budgetAmount = e.getBudgetAmount();
        p.rate = e.getRate();
        p.estimatedHours = e.getEstimatedHours();
        p.estimatedDays = e.getEstimatedDays();
        p.estimatedMonths = e.getEstimatedMonths();

        p.deadlineMillis = (e.getDeadline() != null) ? e.getDeadline().getTime() : 0L;

        p.reminderEnabled = e.isReminderEnabled();
        p.useDefaultOffsets = e.isUseDefaultOffsets();
        p.customOffsetsMillis = parseOffsetsMillis(e.getCustomOffsets());

        return p;
    }

    // Model -> Entity (pratique si tu crées un Project et tu veux l'insérer en Room)
    public static Projet toEntity(Project m) {
        if (m == null) return null;

        Date deadline = (m.deadlineMillis > 0) ? new Date(m.deadlineMillis) : null;

        return new Projet(
                m.id,
                m.name,
                m.notes,                 // description = notes
                m.clientName,
                m.billingType,
                m.budgetAmount,
                m.rate,
                m.estimatedHours,
                m.estimatedDays,
                m.estimatedMonths,
                deadline,
                m.reminderEnabled,
                m.useDefaultOffsets,
                joinOffsetsMillis(m.customOffsetsMillis),
                m.status,
                false,
                new Date(),
                m.clientEmail,
                m.clientPhone
        );
    }

    // Utilitaire : expected amount basé sur entity
    public static double expectedAmount(Projet e) {
        if (e == null) return 0.0;
        switch (e.getBillingType()) {
            case Project.BILLING_HOUR:  return e.getRate() * e.getEstimatedHours();
            case Project.BILLING_DAY:   return e.getRate() * e.getEstimatedDays();
            case Project.BILLING_MONTH: return e.getRate() * e.getEstimatedMonths();
            case Project.BILLING_PROJECT:
            default:                    return e.getBudgetAmount();
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }

    // --- offsets "long,long,long" en millis ---
    private static long[] parseOffsetsMillis(String s) {
        if (s == null) return new long[0];
        s = s.trim();
        if (s.isEmpty()) return new long[0];

        String[] parts = s.split(",");
        long[] out = new long[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try { out[i] = Long.parseLong(parts[i].trim()); }
            catch (Exception e) { out[i] = 0L; }
        }
        return out;
    }

    private static String joinOffsetsMillis(long[] arr) {
        if (arr == null || arr.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
