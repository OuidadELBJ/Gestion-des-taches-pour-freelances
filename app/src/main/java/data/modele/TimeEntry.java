package data.modele;

public class TimeEntry {
    public final String id;
    public final String projectId;
    public final String taskId;
    public final long startMillis;
    public final long endMillis;
    public final long durationMillis;

    public TimeEntry(String id, String projectId, String taskId, long startMillis, long endMillis, long durationMillis) {
        this.id = id;
        this.projectId = projectId;
        this.taskId = taskId;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.durationMillis = durationMillis;
    }
}
