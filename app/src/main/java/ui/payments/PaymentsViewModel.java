package ui.payments;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.fake.FakePaymentStore;
import data.fake.FakeProjectStore;
import data.modele.Payment;
import data.modele.Project;

public class PaymentsViewModel {

    public List<Payment> list(String projectId) {
        return FakePaymentStore.get().listByProject(projectId);
    }

    public List<Payment> listByMonth(String projectId, int month0to11, int year) {
        return FakePaymentStore.get().listByProjectAndMonth(projectId, month0to11, year);
    }

    public double totalReceived(String projectId) {
        return FakePaymentStore.get().totalPaidByProject(projectId);
    }

    public double totalReceivedByMonth(String projectId, int month0to11, int year) {
        return FakePaymentStore.get().totalPaidByProjectAndMonth(projectId, month0to11, year);
    }

    // ✅ attendu (budget) selon le mode du projet
    public double expectedAmount(String projectId) {
        Project p = FakeProjectStore.get().getById(projectId);
        if (p == null) return 0;
        return p.expectedAmount();
    }

    public double remaining(double expected, double received) {
        return Math.max(0, expected - received);
    }

    public int progressPercent(double expected, double received) {
        if (expected <= 0) return 0;
        return (int) Math.min(100, (received / expected) * 100);
    }

    public String money(double value) {
        return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value);
    }

    public String formatDate(long ms) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date(ms));
    }

    // ✅ statut auto
    public String status(Payment p) {
        if (p.paid) return "✅ Payé";

        long now = System.currentTimeMillis();
        long diff = p.dueDateMillis - now;
        long sevenDays = 7L * 24 * 60 * 60 * 1000;

        if (diff < 0) return "🔴 En retard";
        if (diff <= sevenDays) return "🟠 Bientôt";
        return "⏳ En attente";
    }

    // ✅ message de relance
    public String reminderMessage(Payment p) {
        String d = formatDate(p.dueDateMillis);
        return "Bonjour, petit rappel : la facture de " + money(p.amount) +
                " arrive à échéance le " + d + ". Merci 🙂";
    }
}
