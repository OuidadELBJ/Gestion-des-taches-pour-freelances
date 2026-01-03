package data.modele;

public class Project {

    public static final int BILLING_PROJECT = 0; // forfait
    public static final int BILLING_HOUR = 1;
    public static final int BILLING_DAY = 2;
    public static final int BILLING_MONTH = 3;

    public String id;
    public String name;

    // =========================
    // ✅ Contact client (RELANCE)
    // =========================
    public String clientPhone = ""; // ex: +33612345678
    public String clientEmail = ""; // ex: client@mail.com

    // =========================
    // ✅ Notifications (PROPRE)
    // =========================
    public long deadlineMillis = 0L;              // deadline projet (millis)
    public boolean reminderEnabled = false;       // activer rappel projet (rappel "projet")
    public boolean useDefaultOffsets = true;      // true => ReminderPrefs, false => customOffsetsMillis
    public long[] customOffsetsMillis = new long[0]; // offsets custom si useDefaultOffsets=false

    // =========================
    // 🔁 Compat avec tes anciens champs (si jamais utilisés ailleurs)
    // =========================
    @Deprecated public boolean notifUseDefault = true;
    @Deprecated public boolean notifEnabled = false;
    @Deprecated public long[] notifOffsetsMillis = new long[0];

    // =========================
    // 💰 Facturation
    // =========================
    public int billingType = BILLING_PROJECT;

    public double budgetAmount = 0.0;

    public double rate = 0.0;
    public double estimatedHours = 0.0;
    public double estimatedDays = 0.0;
    public double estimatedMonths = 0.0;

    // constructeur simple
    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // montant attendu (dashboard finance)
    public double expectedAmount() {
        switch (billingType) {
            case BILLING_HOUR:  return rate * estimatedHours;
            case BILLING_DAY:   return rate * estimatedDays;
            case BILLING_MONTH: return rate * estimatedMonths;
            case BILLING_PROJECT:
            default:            return budgetAmount;
        }
    }

    @Override
    public String toString() { // utile pour Spinner
        return name;
    }
}
