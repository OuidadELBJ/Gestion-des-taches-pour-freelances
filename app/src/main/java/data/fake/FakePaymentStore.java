package data.fake;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import data.modele.Payment;

public class FakePaymentStore {

    private static FakePaymentStore INSTANCE;
    private final List<Payment> payments = new ArrayList<>();

    public static FakePaymentStore get() {
        if (INSTANCE == null) INSTANCE = new FakePaymentStore();
        return INSTANCE;
    }

    private FakePaymentStore() {
        // ✅ Fake data
        long now = System.currentTimeMillis();

        // p1
        payments.add(new Payment(UUID.randomUUID().toString(), "p1", 450, now - days(30), now - days(5), true, "Acompte"));
        payments.add(new Payment(UUID.randomUUID().toString(), "p1", 300, now - days(10), now + days(3), false, "Reste 1"));

        // p2
        payments.add(new Payment(UUID.randomUUID().toString(), "p2", 200, now - days(7), now - days(1), false, "Facture dev"));

        // p3
        payments.add(new Payment(UUID.randomUUID().toString(), "p3", 900, now - days(20), now - days(10), true, "Mois 1"));
    }

    public void add(String projectId, double amount, long dateMillis, long dueDateMillis, boolean paid, String note) {
        payments.add(new Payment(UUID.randomUUID().toString(), projectId, amount, dateMillis, dueDateMillis, paid, note));
    }

    public List<Payment> listByProject(String projectId) {
        List<Payment> out = new ArrayList<>();
        for (Payment p : payments) if (p.projectId.equals(projectId)) out.add(p);

        // tri du + récent au + ancien
        Collections.sort(out, (a, b) -> Long.compare(b.dateMillis, a.dateMillis));
        return out;
    }

    public List<Payment> listByProjectAndMonth(String projectId, int month0to11, int year) {
        List<Payment> out = new ArrayList<>();
        for (Payment p : payments) {
            if (!p.projectId.equals(projectId)) continue;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(p.dateMillis);
            int m = c.get(Calendar.MONTH);
            int y = c.get(Calendar.YEAR);
            if (m == month0to11 && y == year) out.add(p);
        }
        Collections.sort(out, (a, b) -> Long.compare(b.dateMillis, a.dateMillis));
        return out;
    }

    // ✅ total "reçu" = paiements payés seulement
    public double totalPaidByProject(String projectId) {
        double sum = 0;
        for (Payment p : payments) if (p.projectId.equals(projectId) && p.paid) sum += p.amount;
        return sum;
    }

    public double totalPaidByProjectAndMonth(String projectId, int month0to11, int year) {
        double sum = 0;
        for (Payment p : listByProjectAndMonth(projectId, month0to11, year)) {
            if (p.paid) sum += p.amount;
        }
        return sum;
    }

    private static long days(int d) {
        return d * 24L * 60L * 60L * 1000L;
    }
}
