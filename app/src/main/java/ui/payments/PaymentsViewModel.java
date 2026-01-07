package ui.payments;

import android.content.Context;

import com.example.freelance.data.local.entity.Paiement;
import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.repository.PaiementRepository;
import com.example.freelance.data.local.repository.ProjetRepository;
import com.example.freelance.data.mapper.PaymentMapper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.modele.Payment;

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

    private final PaiementRepository paiementRepo;
    private final ProjetRepository projetRepo;

    public PaymentsViewModel(Context context) {
        Context app = context.getApplicationContext();
        paiementRepo = new PaiementRepository(app);
        projetRepo = new ProjetRepository(app);
    }

    // ✅ charge LIST + TOTAL (reçu) + EXPECTED (depuis Projet Room)
    public void loadPayments(String projectId, Integer month0to11, Integer year, Callback<UiState> cb) {
        UiState state = new UiState();

        // 1) Charger le projet depuis Room pour expected
        projetRepo.getById(projectId, projet -> {
            state.expected = expectedAmount(projet);

            // 2) Charger les paiements + totaux
            if (month0to11 != null && year != null) {
                state.filtered = true;

                Date start = monthStart(year, month0to11);
                Date end = monthEnd(year, month0to11);

                paiementRepo.getByProjectBetween(projectId, start, end, entities -> {
                    state.list = mapList(entities);

                    paiementRepo.getTotalPaidByProjectBetween(projectId, start, end, total -> {
                        state.received = safe(total);
                        state.remaining = remaining(state.expected, state.received);
                        state.progressPercent = progressPercent(state.expected, state.received);
                        cb.onResult(state);
                    });
                });

            } else {
                state.filtered = false;

                paiementRepo.getByProject(projectId, entities -> {
                    state.list = mapList(entities);

                    paiementRepo.getTotalPaidByProject(projectId, total -> {
                        state.received = safe(total);
                        state.remaining = remaining(state.expected, state.received);
                        state.progressPercent = progressPercent(state.expected, state.received);
                        cb.onResult(state);
                    });
                });
            }
        });
    }

    // ===== Expected amount depuis Projet entity Room =====
    private double expectedAmount(Projet p) {
        if (p == null) return 0.0;

        int billingType = p.getBillingType();
        double budgetAmount = p.getBudgetAmount();
        double rate = p.getRate();
        double hours = p.getEstimatedHours();
        double days = p.getEstimatedDays();
        double months = p.getEstimatedMonths();

        // 0 = PROJECT, 1 = HOUR, 2 = DAY, 3 = MONTH (selon ton commentaire)
        switch (billingType) {
            case 1: return rate * hours;
            case 2: return rate * days;
            case 3: return rate * months;
            case 0:
            default:
                return budgetAmount;
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

    public String reminderMessage(Payment p) {
        String d = formatDate(p.dueDateMillis);
        return "Bonjour, petit rappel : la facture de " + money(p.amount) +
                " arrive à échéance le " + d + ". Merci 🙂";
    }
}
