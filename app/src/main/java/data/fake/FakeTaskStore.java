package data.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.modele.Task;

public class FakeTaskStore {

    private static FakeTaskStore INSTANCE;
    private final List<Task> tasks = new ArrayList<>();

    private FakeTaskStore() {
        long now = System.currentTimeMillis();

        // Projet p1 (global)
        Task t1 = new Task("t1", "p1", "Créer maquettes UI",
                TimeUnit.HOURS.toMillis(2),
                now + TimeUnit.MINUTES.toMillis(2), true);
        // utilise offsets par défaut (global)
        t1.useDefaultOffsets = true;

        Task t2 = new Task("t2", "p1", "Écran Timer + Service",
                TimeUnit.HOURS.toMillis(3),
                now + TimeUnit.MINUTES.toMillis(5), true);
        // override tâche : 2 minutes avant
        t2.useDefaultOffsets = false;
        t2.customOffsetsMillis = new long[]{ TimeUnit.MINUTES.toMillis(2) };

        Task t3 = new Task("t3", "p1", "Tests / Debug",
                TimeUnit.HOURS.toMillis(1),
                now + TimeUnit.MINUTES.toMillis(10), true);
        t3.useDefaultOffsets = true;

        // Projet p2 (projet override = 1h dans FakeProjectStore)
        Task t4 = new Task("t4", "p2", "CRUD tâches",
                TimeUnit.HOURS.toMillis(4),
                now + TimeUnit.MINUTES.toMillis(3), true);
        // pas de custom tâche -> prendra offsets projet (1h) puis sinon global
        t4.useDefaultOffsets = true;

        Task t5 = new Task("t5", "p2", "Notifications deadlines",
                TimeUnit.HOURS.toMillis(2),
                now + TimeUnit.MINUTES.toMillis(7), true);
        // override tâche : 10 minutes avant
        t5.useDefaultOffsets = false;
        t5.customOffsetsMillis = new long[]{ TimeUnit.MINUTES.toMillis(10) };

        // Projet p3 (projet override = 10min + 1h)
        Task t6 = new Task("t6", "p3", "Écran paiements",
                TimeUnit.HOURS.toMillis(2),
                now + TimeUnit.MINUTES.toMillis(4), true);
        t6.useDefaultOffsets = true;

        Task t7 = new Task("t7", "p3", "Total + stats",
                TimeUnit.HOURS.toMillis(1),
                now + TimeUnit.MINUTES.toMillis(8), true);
        t7.useDefaultOffsets = true;

        tasks.add(t1); tasks.add(t2); tasks.add(t3);
        tasks.add(t4); tasks.add(t5);
        tasks.add(t6); tasks.add(t7);
    }

    public static FakeTaskStore get() {
        if (INSTANCE == null) INSTANCE = new FakeTaskStore();
        return INSTANCE;
    }

    public List<Task> listByProject(String projectId) {
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.projectId.equals(projectId)) out.add(t);
        }
        return out;
    }

    public List<Task> listAll() {
        return new ArrayList<>(tasks);
    }

    public Task getById(String taskId) {
        for (Task t : tasks) {
            if (t.id.equals(taskId)) return t;
        }
        return null;
    }
    public void add(Task t) {
        tasks.add(t);
    }
}
