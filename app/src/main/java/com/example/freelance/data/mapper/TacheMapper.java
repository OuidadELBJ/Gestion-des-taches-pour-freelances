package com.example.freelance.data.mapper;

import com.example.freelance.data.local.entity.Tache;

import java.util.Date;

import data.modele.Task;

public class TacheMapper {

    public static Task toModel(Tache e) {
        if (e == null) return null;

        String id = e.getIdTache();
        String projectId = e.getProjectId();
        String title = e.getTitle();

        long estimatedMillis = e.getEstimatedMillis();
        long deadlineMillis = (e.getDeadline() != null) ? e.getDeadline().getTime() : 0L;

        // reminderEnabled dans ton modèle Task = boolean
        Task t = new Task(
                id,
                projectId,
                title,
                estimatedMillis,
                deadlineMillis,
                e.isReminderEnabled()
        );

        // notifications offsets
        t.useDefaultOffsets = e.isUseDefaultOffsets();
        t.customOffsetsMillis = parseOffsetsCsvToLongArray(e.getCustomOffsets());

        return t;
    }

    public static Tache toEntity(Task m) {
        if (m == null) return null;

        Date now = new Date();
        Date deadline = (m.deadlineMillis > 0) ? new Date(m.deadlineMillis) : null;

        // Champs que ton modèle Task n’a pas encore
        String description = null;
        String priority = null;

        // ✅ Important : status existe en DB -> on met un défaut
        String status = "TODO"; // ou "À faire"

        Boolean isSynced = false;

        return new Tache(
                m.id,
                m.projectId,
                m.title,
                description,
                priority,
                status,
                deadline,
                now,   // createdAt
                now,   // lastUpdated
                isSynced,
                m.estimatedMillis,
                m.reminderEnabled,
                m.useDefaultOffsets,
                offsetsLongArrayToCsv(m.customOffsetsMillis)
        );
    }

    // ---------------------------
    // Helpers offsets "csv"
    // ---------------------------
    private static long[] parseOffsetsCsvToLongArray(String csv) {
        if (csv == null) return new long[0];
        String s = csv.trim();
        if (s.isEmpty()) return new long[0];

        try {
            String[] parts = s.split(",");
            long[] out = new long[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String p = parts[i].trim();
                out[i] = p.isEmpty() ? 0L : Long.parseLong(p);
            }
            return out;
        } catch (Exception ex) {
            return new long[0];
        }
    }

    private static String offsetsLongArrayToCsv(long[] arr) {
        if (arr == null || arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
