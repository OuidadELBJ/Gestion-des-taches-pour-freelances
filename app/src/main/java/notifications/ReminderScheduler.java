package notifications;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.freelance.data.local.AppDatabase;
import com.example.freelance.data.local.entity.Paiement;
import com.example.freelance.data.local.entity.Projet;
import com.example.freelance.data.local.entity.Tache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import data.prefs.ReminderPrefs;

public class ReminderScheduler {

    private static final String TAG_REMINDER_ALL = "REMINDER_ALL";

    private static String tagTask(String taskId) { return "REMINDER_TASK_" + taskId; }
    private static String tagProject(String projectId) { return "REMINDER_PROJECT_" + projectId; }

    // ✅ NEW
    private static String tagPayment(String paymentId) { return "REMINDER_PAYMENT_" + paymentId; }

    // =========================
    // ✅ API publique (hooks)
    // =========================

    /** Appelle après insert/update d'une tâche */
    public static void onTaskUpsert(Context c, Tache t) {
        Context app = c.getApplicationContext();

        cancelTask(app, t.getIdTache());

        Executors.newSingleThreadExecutor().execute(() -> {
            ReminderPrefs prefs = ReminderPrefs.load(app);
            if (!prefs.enabled || !prefs.remindTasks) return;

            if (!t.isReminderEnabled() || t.getDeadline() == null) return;

            AppDatabase db = AppDatabase.getInstance(app);
            Projet p = db.projetDao().getById(t.getProjectId());

            scheduleForTask(app, t, p, prefs);
        });
    }

    /** Appelle après delete d'une tâche */
    public static void onTaskDeleted(Context c, String taskId) {
        cancelTask(c.getApplicationContext(), taskId);
    }

    /** Appelle après insert/update d'un projet */
    public static void onProjectUpsert(Context c, Projet p) {
        Context app = c.getApplicationContext();

        cancelProject(app, p.getIdProjet());

        Executors.newSingleThreadExecutor().execute(() -> {
            ReminderPrefs prefs = ReminderPrefs.load(app);
            if (!prefs.enabled || !prefs.remindProjects) return;

            if (!p.isReminderEnabled() || p.getDeadline() == null) return;

            scheduleForProject(app, p, prefs);
        });
    }

    /** Appelle après delete d'un projet */
    public static void onProjectDeleted(Context c, String projectId) {
        cancelProject(c.getApplicationContext(), projectId);
    }

    // ✅ NEW : Paiement upsert/delete/cancel
    public static void onPaymentUpsert(Context c, Paiement pay) {
        Context app = c.getApplicationContext();

        // toujours nettoyer avant
        cancelPayment(app, pay.getIdPaiement());

        Executors.newSingleThreadExecutor().execute(() -> {
            ReminderPrefs prefs = ReminderPrefs.load(app);
            if (!prefs.enabled) return;

            // si payé ou pas d'échéance => pas de rappel
            if (pay.isPaid() || pay.getDueDate() == null) return;

            scheduleForPayment(app, pay, prefs);
        });
    }

    public static void onPaymentDeleted(Context c, String paymentId) {
        cancelPayment(c.getApplicationContext(), paymentId);
    }

    public static void cancelPayment(Context c, String paymentId) {
        WorkManager.getInstance(c).cancelAllWorkByTag(tagPayment(paymentId));
    }

    /** Cancel tous les reminders d’une tâche */
    public static void cancelTask(Context c, String taskId) {
        WorkManager.getInstance(c).cancelAllWorkByTag(tagTask(taskId));
    }

    /** Cancel tous les reminders d’un projet */
    public static void cancelProject(Context c, String projectId) {
        WorkManager.getInstance(c).cancelAllWorkByTag(tagProject(projectId));
    }

    // =========================
    // ✅ Reschedule global (Room) - safe thread
    // =========================
    public static void rescheduleAll(Context c) {
        Context app = c.getApplicationContext();

        // On annule UNIQUEMENT les reminders
        WorkManager.getInstance(app).cancelAllWorkByTag(TAG_REMINDER_ALL);

        Executors.newSingleThreadExecutor().execute(() -> {
            ReminderPrefs prefs = ReminderPrefs.load(app);
            if (!prefs.enabled) return;

            AppDatabase db = AppDatabase.getInstance(app);

            // Tasks
            if (prefs.remindTasks) {
                List<Tache> tasks = db.tacheDao().getAll();
                for (Tache t : tasks) {
                    if (!t.isReminderEnabled()) continue;
                    if (t.getDeadline() == null) continue;

                    Projet p = db.projetDao().getById(t.getProjectId());
                    scheduleForTask(app, t, p, prefs);
                }
            }

            // Projects
            if (prefs.remindProjects) {
                List<Projet> projets = db.projetDao().getAll();
                for (Projet p : projets) {
                    if (!p.isReminderEnabled()) continue;
                    if (p.getDeadline() == null) continue;

                    scheduleForProject(app, p, prefs);
                }
            }

            // ✅ NEW : Payments
            List<Paiement> pays = db.paiementDao().getAll();
            for (Paiement pay : pays) {
                if (pay.isPaid()) continue;
                if (pay.getDueDate() == null) continue;
                scheduleForPayment(app, pay, prefs);
            }
        });
    }

    // (optionnel)
    public static void scheduleTestInSeconds(Context c, int seconds) {
        String workName = "REMINDER_TEST_" + seconds;

        Data d = new Data.Builder()
                .putString(ReminderWorker.KEY_TITLE, "Test notification")
                .putString(ReminderWorker.KEY_TEXT, "Ça marche ✅ (test " + seconds + "s)")
                .putInt(ReminderWorker.KEY_NOTIF_ID, Math.abs(workName.hashCode()))
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .addTag(TAG_REMINDER_ALL)
                .setInitialDelay(Math.max(1, seconds), TimeUnit.SECONDS)
                .setInputData(d)
                .build();

        WorkManager.getInstance(c).enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, req);
    }

    // =========================
    // ✅ Scheduling - TASK / PROJECT / PAYMENT
    // =========================

    private static void scheduleForTask(Context c, Tache t, Projet p, ReminderPrefs prefs) {
        long deadlineMillis = t.getDeadline().getTime();
        long[] offsets = resolveOffsetsForTask(t, p, prefs);
        if (offsets.length == 0) return;

        String projectName = (p == null ? t.getProjectId() : p.getName());

        for (long offset : offsets) {
            long when = deadlineMillis - offset;
            when = adjustTimeMode(when, prefs.atNine);

            enqueue(
                    c,
                    uniqueName("TASK", t.getIdTache(), offset),
                    "Rappel tâche",
                    "Projet: " + projectName + " • " + t.getTitle(),
                    when,
                    TAG_REMINDER_ALL,
                    tagTask(t.getIdTache()),
                    tagProject(t.getProjectId())
            );
        }
    }

    private static void scheduleForProject(Context c, Projet p, ReminderPrefs prefs) {
        long deadlineMillis = p.getDeadline().getTime();
        long[] offsets = resolveOffsetsForProject(p, prefs);
        if (offsets.length == 0) return;

        for (long offset : offsets) {
            long when = deadlineMillis - offset;
            when = adjustTimeMode(when, prefs.atNine);

            enqueue(
                    c,
                    uniqueName("PROJECT", p.getIdProjet(), offset),
                    "Rappel projet",
                    "Deadline projet : " + p.getName(),
                    when,
                    TAG_REMINDER_ALL,
                    tagProject(p.getIdProjet())
            );
        }
    }

    // ✅ NEW
    private static void scheduleForPayment(Context c, Paiement pay, ReminderPrefs prefs) {
        long dueMillis = pay.getDueDate().getTime();

        long[] offsets = prefsOffsets(prefs);
        if (offsets.length == 0) return;

        for (long offset : offsets) {
            long when = dueMillis - offset;
            when = adjustTimeMode(when, prefs.atNine);

            enqueue(
                    c,
                    uniqueName("PAYMENT", pay.getIdPaiement(), offset),
                    "Rappel paiement",
                    "Paiement : " + pay.getAmount() + "€",
                    when,
                    TAG_REMINDER_ALL,
                    tagPayment(pay.getIdPaiement()),
                    tagProject(pay.getProjectId()) // optionnel mais utile
            );
        }
    }

    // =========================
    // ✅ Offsets
    // =========================

    private static long[] resolveOffsetsForTask(Tache t, Projet p, ReminderPrefs prefs) {
        long[] taskCustom = parseOffsetsToMillis(t.getCustomOffsets());
        if (!t.isUseDefaultOffsets() && taskCustom.length > 0) return taskCustom;

        if (p != null) {
            long[] projectCustom = parseOffsetsToMillis(p.getCustomOffsets());
            if (!p.isUseDefaultOffsets() && projectCustom.length > 0) return projectCustom;
        }

        return prefsOffsets(prefs);
    }

    private static long[] resolveOffsetsForProject(Projet p, ReminderPrefs prefs) {
        long[] projectCustom = parseOffsetsToMillis(p.getCustomOffsets());
        if (!p.isUseDefaultOffsets() && projectCustom.length > 0) return projectCustom;
        return prefsOffsets(prefs);
    }

    private static long[] prefsOffsets(ReminderPrefs prefs) {
        if (prefs.offsetsMillis == null || prefs.offsetsMillis.isEmpty()) {
            return new long[]{ TimeUnit.HOURS.toMillis(1) };
        }
        long[] out = new long[prefs.offsetsMillis.size()];
        int i = 0;
        for (Long v : prefs.offsetsMillis) out[i++] = v;
        return out;
    }

    /**
     * customOffsets stocké comme String: "7,3,1" (souvent en jours).
     */
    private static long[] parseOffsetsToMillis(String customOffsets) {
        if (customOffsets == null || customOffsets.trim().isEmpty()) return new long[0];

        String[] parts = customOffsets.split(",");
        List<Long> ok = new ArrayList<>();

        for (String s : parts) {
            try {
                long v = Long.parseLong(s.trim());
                if (v <= 0) continue;

                long millis = (v < 10_000_000L) ? TimeUnit.DAYS.toMillis(v) : v;
                ok.add(millis);
            } catch (Exception ignored) {}
        }

        long[] out = new long[ok.size()];
        for (int i = 0; i < ok.size(); i++) out[i] = ok.get(i);
        return out;
    }

    // =========================
    // ✅ Enqueue WorkManager
    // =========================
    private static void enqueue(Context c, String name, String title, String text, long whenMillis, String... tags) {
        long delay = whenMillis - System.currentTimeMillis();
        if (delay < 5_000L) return;

        Data d = new Data.Builder()
                .putString(ReminderWorker.KEY_TITLE, title)
                .putString(ReminderWorker.KEY_TEXT, text)
                .putInt(ReminderWorker.KEY_NOTIF_ID, Math.abs(name.hashCode()))
                .build();

        OneTimeWorkRequest.Builder b = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(d);

        if (tags != null) {
            for (String t : tags) {
                if (t != null && !t.trim().isEmpty()) b.addTag(t);
            }
        }

        WorkManager.getInstance(c).enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, b.build());
    }

    private static String uniqueName(String type, String id, long offset) {
        return "REMINDER_" + type + "_" + id + "_OFF_" + offset;
    }

    private static long adjustTimeMode(long whenMillis, boolean atNine) {
        if (!atNine) return whenMillis;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(whenMillis);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}