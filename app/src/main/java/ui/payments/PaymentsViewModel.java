package ui.payments;

import android.content.Context;

import com.example.freelance.data.local.repository.PaiementRepository;
import com.example.freelance.data.mapper.PaymentMapper;
import com.example.freelance.data.local.entity.Paiement;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.fake.FakeProjectStore;
import data.modele.Payment;
import data.modele.Project;

public class PaymentsViewModel {

    public interface Callback<T> { void onResult(T value); }

    public static class UiState {
        public List<Payment> list = new ArrayList<>();
        public double received = 0;
        public double expected = 0;
        public double remaining = 0;
        public int progressPercent = 0;
        public boolean filtered = false;
    }

    private final PaiementRepository repo;

    public PaymentsViewModel(Context context) {
        repo = new PaiementRepository(context.getApplicationContext());
    }

    // ✅ charge LIST + TOTAL (reçu) en 1 méthode
    public void loadPayments(String projectId, Integer month0to11, Integer year, Callback<UiState> cb) {
        UiState state = new UiState();

        // attendu (pour l’instant tu gardes FakeProjectStore)
        Project p = FakeProjectStore.get().getById(projectId);
        state.expected = (p != null) ? p.expectedAmount() : 0;

        if (month0to11 != null && year != null) {
            state.filtered = true;

            Date start = monthStart(year, month0to11);
            Date end = monthEnd(year, month0to11);

            repo.getByProjectBetween(projectId, start, end, entities -> {
                state.list = mapList(entities);

                repo.getTotalPaidByProjectBetween(projectId, start, end, total -> {
                    state.received = safe(total);
                    state.remaining = remaining(state.expected, state.received);
                    state.progressPercent = progressPercent(state.expected, state.received);
                    cb.onResult(state);
                });
            });

        } else {
            state.filtered = false;

            repo.getByProject(projectId, entities -> {
                state.list = mapList(entities);

                repo.getTotalPaidByProject(projectId, total -> {
                    state.received = safe(total);
                    state.remaining = remaining(state.expected, state.received);
                    state.progressPercent = progressPercent(state.expected, state.received);
                    cb.onResult(state);
                });
            });
        }
    }

    private List<Payment> mapList(List<Paiement> entities) {
        List<Payment> out = new ArrayList<>();
        if (entities == null) return out;
        for (Paiement e : entities) {
            Payment m = PaymentMapper.toModel(e);
            if (m != null) out.add(m);
        }
        return out;
    }

    private double safe(Double d) {
        return (d == null) ? 0.0 : d;
    }

    private Date monthStart(int year, int month0to11) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month0to11);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date monthEnd(int year, int month0to11) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month0to11);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
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

    public void debugInsertTestPayment(String projectId) {
        if (projectId == null) return;

        long now = System.currentTimeMillis();
        long due = now + 2 * 24L * 60L * 60L * 1000L;

        Payment test = new Payment(
                "test_" + now,
                projectId,
                123.45,
                now,
                due,
                false,
                "TEST ROOM"
        );

        Paiement entity = PaymentMapper.toEntity(test);
        repo.insert(entity);
    }

}
