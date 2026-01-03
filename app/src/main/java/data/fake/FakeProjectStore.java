package data.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.modele.Project;

public class FakeProjectStore {

    private static FakeProjectStore INSTANCE;
    private final List<Project> projects = new ArrayList<>();

    public static FakeProjectStore get() {
        if (INSTANCE == null) INSTANCE = new FakeProjectStore();
        return INSTANCE;
    }

    private FakeProjectStore() {
        long now = System.currentTimeMillis();

        // ✅ Fake data : 3 projets avec différents modes
        Project p1 = new Project("p1", "E-Commerce Website");
        p1.billingType = Project.BILLING_PROJECT;
        p1.budgetAmount = 2000;
        p1.deadlineMillis = now + TimeUnit.MINUTES.toMillis(12);
        p1.reminderEnabled = true;
        p1.useDefaultOffsets = true;          // => global prefs
        p1.clientPhone = "+33611111111";
        p1.clientEmail = "client1@gmail.com";

        Project p2 = new Project("p2", "Mobile App Development");
        p2.billingType = Project.BILLING_HOUR;
        p2.rate = 50;
        p2.estimatedHours = 40;
        p2.deadlineMillis = now + TimeUnit.MINUTES.toMillis(20);
        p2.reminderEnabled = true;
        p2.useDefaultOffsets = false;         // => custom
        p2.customOffsetsMillis = new long[]{ TimeUnit.HOURS.toMillis(1) };
        p2.clientPhone = "+33622222222";
        p2.clientEmail = "client2@gmail.com";

        Project p3 = new Project("p3", "Design Mensuel");
        p3.billingType = Project.BILLING_MONTH;
        p3.rate = 900;
        p3.estimatedMonths = 2;
        p3.deadlineMillis = now + TimeUnit.MINUTES.toMillis(30);
        p3.reminderEnabled = true;
        p3.useDefaultOffsets = false;         // => custom
        p3.customOffsetsMillis = new long[]{
                TimeUnit.MINUTES.toMillis(10),
                TimeUnit.HOURS.toMillis(1)
        };
        p3.clientPhone = "+33633333333";
        p3.clientEmail = "client3@gmail.com";

        // ✅ compat champs anciens (si jamais un écran les lit)
        syncLegacy(p1); syncLegacy(p2); syncLegacy(p3);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);
    }

    private void syncLegacy(Project p) {
        // garde cohérence si du vieux code lit notifEnabled/notifOffsetsMillis
        p.notifEnabled = p.reminderEnabled;
        p.notifUseDefault = p.useDefaultOffsets;
        p.notifOffsetsMillis = p.customOffsetsMillis;
    }

    public List<Project> list() {
        return new ArrayList<>(projects);
    }

    public Project getById(String projectId) {
        for (Project p : projects) {
            if (p.id.equals(projectId)) return p;
        }
        return null;
    }
}
