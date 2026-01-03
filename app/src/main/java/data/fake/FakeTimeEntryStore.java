package data.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import data.modele.TimeEntry;

public class FakeTimeEntryStore {
    private static FakeTimeEntryStore INSTANCE;
    private final List<TimeEntry> entries = new ArrayList<>();

    public static FakeTimeEntryStore get() {
        if (INSTANCE == null) INSTANCE = new FakeTimeEntryStore();
        return INSTANCE;
    }

    // ✅ Ajout par projet + tâche
    public void add(String projectId, String taskId, long startMillis, long endMillis) {
        long duration = Math.max(0, endMillis - startMillis);
        entries.add(new TimeEntry(
                UUID.randomUUID().toString(),
                projectId,
                taskId,
                startMillis,
                endMillis,
                duration
        ));
    }

    // ✅ Historique par tâche
    public List<TimeEntry> listByTask(String taskId) {
        List<TimeEntry> out = new ArrayList<>();
        for (TimeEntry e : entries) {
            if (e.taskId != null && e.taskId.equals(taskId)) out.add(e);
        }
        return out;
    }

    // ✅ Historique par projet (toutes tâches)
    public List<TimeEntry> listByProject(String projectId) {
        List<TimeEntry> out = new ArrayList<>();
        for (TimeEntry e : entries) {
            if (e.projectId.equals(projectId)) out.add(e);
        }
        return out;
    }

    // ✅ Total d’une tâche
    public long totalTask(String taskId) {
        long sum = 0;
        for (TimeEntry e : entries) {
            if (e.taskId != null && e.taskId.equals(taskId)) sum += e.durationMillis;
        }
        return sum;
    }

    // ✅ Total projet (somme des tâches)
    public long totalProject(String projectId) {
        long sum = 0;
        for (TimeEntry e : entries) {
            if (e.projectId.equals(projectId)) sum += e.durationMillis;
        }
        return sum;
    }
}
