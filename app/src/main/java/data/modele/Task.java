package data.modele;

public class Task {

    public final String id;
    public final String projectId;
    public final String title;
    public final long estimatedMillis;

    // ✅ Status (DB a status + DAO updateStatus)
    public String status = "TODO";

    // ✅ Notifications
    public long deadlineMillis = 0L;
    public boolean reminderEnabled = false;
    public boolean useDefaultOffsets = true;
    public long[] customOffsetsMillis = new long[0];

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

    public Task(String id, String projectId, String title, long estimatedMillis) {
        this(id, projectId, title, estimatedMillis, 0L, false);
    }

    @Override public String toString() { return title; }
}
