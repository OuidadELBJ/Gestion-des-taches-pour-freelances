package data.modele;

public class Task {

    public final String id;
    public final String projectId;
    public final String title;
    public final long estimatedMillis;

    // =========================
    // ✅ Notifications (PROPRE)
    // =========================
    public long deadlineMillis = 0L;            // deadline tâche
    public boolean reminderEnabled = false;     // activer rappel tâche
    public boolean useDefaultOffsets = true;    // true => utilise projet/global, false => customOffsetsMillis
    public long[] customOffsetsMillis = new long[0]; // offsets custom tâche

    // constructeur complet (recommandé)
    public Task(String id, String projectId, String title,
                long estimatedMillis,
                long deadlineMillis,
                boolean reminderEnabled) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.estimatedMillis = estimatedMillis;
        this.deadlineMillis = deadlineMillis;
        this.reminderEnabled = reminderEnabled;
    }

    // compat : estimation sans notif (si un jour tu l’utilises)
    public Task(String id, String projectId, String title, long estimatedMillis) {
        this(id, projectId, title, estimatedMillis, 0L, false);
    }

    @Override public String toString() { return title; }
}
