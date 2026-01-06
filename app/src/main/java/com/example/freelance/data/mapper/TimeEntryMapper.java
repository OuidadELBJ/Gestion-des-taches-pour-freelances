package com.example.freelance.data.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeEntryMapper {

    // Entity -> UI Model
    public static data.modele.TimeEntry toModel(com.example.freelance.data.local.entity.TimeEntry e) {
        if (e == null) return null;

        long start = (e.getStartTime() != null) ? e.getStartTime().getTime() : 0L;
        long end   = (e.getEndTime() != null) ? e.getEndTime().getTime() : 0L;

        long duration = e.getDuration();
        if (duration <= 0 && start > 0 && end > 0) duration = Math.max(0, end - start);

        return new data.modele.TimeEntry(
                e.getIdTime(),
                e.getProjectId(),
                e.getTaskId(),
                start,
                end,
                duration
        );
    }

    public static List<data.modele.TimeEntry> toModelList(List<com.example.freelance.data.local.entity.TimeEntry> list) {
        List<data.modele.TimeEntry> out = new ArrayList<>();
        if (list == null) return out;
        for (com.example.freelance.data.local.entity.TimeEntry e : list) {
            data.modele.TimeEntry m = toModel(e);
            if (m != null) out.add(m);
        }
        return out;
    }

    // UI Model -> Entity (si besoin plus tard)
    public static com.example.freelance.data.local.entity.TimeEntry toEntity(data.modele.TimeEntry m) {
        if (m == null) return null;

        Date start = (m.startMillis > 0) ? new Date(m.startMillis) : null;
        Date end   = (m.endMillis > 0) ? new Date(m.endMillis) : null;

        return new com.example.freelance.data.local.entity.TimeEntry(
                m.id,
                m.taskId,
                m.projectId,
                start,
                end,
                m.durationMillis,
                false,             // isRunning
                new Date(),         // lastUpdated
                false,             // isSynced
                false,             // isPaused
                0L,                // pausedAccumulated
                null               // note
        );
    }
}
