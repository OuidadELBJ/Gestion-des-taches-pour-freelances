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

        Project p1 = new Project("p1", "E-Commerce Website");
        p1.billingType = Project.BILLING_PROJECT;
        p1.budgetAmount = 2000;
        p1.deadlineMillis = now + TimeUnit.DAYS.toMillis(12);
        p1.status = "En cours";
        p1.clientName = "Client 1";
        p1.clientPhone = "+33611111111";
        p1.clientEmail = "client1@gmail.com";
        syncLegacy(p1);

        Project p2 = new Project("p2", "Mobile App Development");
        p2.billingType = Project.BILLING_HOUR;
        p2.rate = 50;
        p2.estimatedHours = 40;
        p2.deadlineMillis = now + TimeUnit.DAYS.toMillis(20);
        p2.status = "En retard";
        p2.clientName = "Client 2";
        p2.clientPhone = "+33622222222";
        p2.clientEmail = "client2@gmail.com";
        syncLegacy(p2);

        Project p3 = new Project("p3", "Design Mensuel");
        p3.billingType = Project.BILLING_MONTH;
        p3.rate = 900;
        p3.estimatedMonths = 2;
        p3.deadlineMillis = now + TimeUnit.DAYS.toMillis(30);
        p3.status = "Terminé";
        p3.clientName = "Client 3";
        p3.clientPhone = "+33633333333";
        p3.clientEmail = "client3@gmail.com";
        syncLegacy(p3);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);
    }

    private void syncLegacy(Project p) {
        p.notifEnabled = p.reminderEnabled;
        p.notifUseDefault = p.useDefaultOffsets;
        p.notifOffsetsMillis = p.customOffsetsMillis;
    }

    public List<Project> list() {
        return new ArrayList<>(projects);
    }

    public Project getById(String projectId) {
        if (projectId == null) return null;
        for (Project p : projects) {
            if (projectId.equals(p.id)) return p;
        }
        return null;
    }

    public void add(Project p) {
        if (p == null) return;
        syncLegacy(p);
        projects.add(0, p); // ✅ en haut
    }
}