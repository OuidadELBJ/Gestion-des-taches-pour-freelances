package notifications;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import data.fake.FakeProjectStore;
import data.fake.FakeTaskStore;
import data.modele.Project;
import data.modele.Task;
import data.prefs.ReminderPrefs;

public class ReminderScheduler {

    public static void rescheduleAll(Context c) {
        ReminderPrefs prefs = ReminderPrefs.load(c);

        if (!prefs.enabled) {
            // Attention : ça annule tout WorkManager (ok dans ton cas)
            WorkManager.getInstance(c).cancelAllWork();
            return;
        }

        // Tasks
        if (prefs.remindTasks) {
            for (Task t : FakeTaskStore.get().listAll()) {
                if (!t.reminderEnabled) continue;
                scheduleForTask(c, t, prefs);
            }
        }

        // Projects
        if (prefs.remindProjects) {
            for (Project p : FakeProjectStore.get().list()) {
                if (!p.reminderEnabled) continue;
                scheduleForProject(c, p, prefs);
            }
        }
    }

    public static void scheduleTestInSeconds(Context c, int seconds) {
        String workName = "REMINDER_TEST_" + seconds;

        Data d = new Data.Builder()
                .putString(ReminderWorker.KEY_TITLE, "Test notification")
                .putString(ReminderWorker.KEY_TEXT, "Ça marche ✅ (test " + seconds + "s)")
                .putInt(ReminderWorker.KEY_NOTIF_ID, Math.abs(workName.hashCode()))
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(Math.max(1, seconds), TimeUnit.SECONDS)
                .setInputData(d)
                .build();

        WorkManager.getInstance(c).enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, req);
    }

    // =========================
    // ✅ TASK : priorité offsets
    // Task custom > Project custom > Global prefs
    // =========================
    private static void scheduleForTask(Context c, Task t, ReminderPrefs prefs) {
        if (t.deadlineMillis <= 0L) return;

        Project p = FakeProjectStore.get().getById(t.projectId);
        long[] offsets = resolveOffsetsForTask(t, p, prefs);
        if (offsets.length == 0) return;

        String projectName = (p == null ? t.projectId : p.name);

        for (long offset : offsets) {
            long when = t.deadlineMillis - offset;
            when = adjustTimeMode(when, prefs.atNine);

            enqueue(
                    c,
                    uniqueName("TASK", t.id, offset),
                    "Rappel tâche",
                    "Projet: " + projectName + " • " + t.title,
                    when
            );
        }
    }

    // =========================
    // ✅ PROJECT : offsets projet custom ou global
    // =========================
    private static void scheduleForProject(Context c, Project p, ReminderPrefs prefs) {
        if (p.deadlineMillis <= 0L) return;

        long[] offsets = resolveOffsetsForProject(p, prefs);
        if (offsets.length == 0) return;

        for (long offset : offsets) {
            long when = p.deadlineMillis - offset;
            when = adjustTimeMode(when, prefs.atNine);

            enqueue(
                    c,
                    uniqueName("PROJECT", p.id, offset),
                    "Rappel projet",
                    "Deadline projet : " + p.name,
                    when
            );
        }
    }

    // =====================================================
    // ✅ RESOLUTION OFFSETS (ROBUSTE + COMPAT)
    // =====================================================
    private static long[] resolveOffsetsForTask(Task t, Project p, ReminderPrefs prefs) {
        // 1) Task override
        long[] taskCustom = safeOffsets(t.customOffsetsMillis);
        if (!t.useDefaultOffsets && taskCustom.length > 0) return taskCustom;

        // 2) Project override
        if (p != null) {
            long[] projectCustom = safeOffsets(p.customOffsetsMillis);
            // compat si quelqu’un a rempli notifOffsetsMillis
            if (projectCustom.length == 0) projectCustom = safeOffsets(p.notifOffsetsMillis);

            boolean useProjectDefault = p.useDefaultOffsets; // true => global
            // compat si quelqu’un utilise notifUseDefault
            if (p.notifUseDefault == false) useProjectDefault = false;

            if (!useProjectDefault && projectCustom.length > 0) return projectCustom;
        }

        // 3) Global prefs
        return prefsOffsets(prefs);
    }

    private static long[] resolveOffsetsForProject(Project p, ReminderPrefs prefs) {
        long[] projectCustom = safeOffsets(p.customOffsetsMillis);
        if (projectCustom.length == 0) projectCustom = safeOffsets(p.notifOffsetsMillis);

        boolean useDefault = p.useDefaultOffsets;
        if (p.notifUseDefault == false) useDefault = false;

        if (!useDefault && projectCustom.length > 0) return projectCustom;
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

    private static long[] safeOffsets(long[] arr) {
        if (arr == null || arr.length == 0) return new long[0];
        // on filtre <= 0
        List<Long> ok = new ArrayList<>();
        for (long v : arr) if (v > 0) ok.add(v);
        long[] out = new long[ok.size()];
        for (int i = 0; i < ok.size(); i++) out[i] = ok.get(i);
        return out;
    }

    // =====================================================
    // ✅ ENQUEUE
    // =====================================================
    private static void enqueue(Context c, String name, String title, String text, long whenMillis) {
        long delay = whenMillis - System.currentTimeMillis();
        if (delay < 5_000L) return; // déjà passé => ignore

        Data d = new Data.Builder()
                .putString(ReminderWorker.KEY_TITLE, title)
                .putString(ReminderWorker.KEY_TEXT, text)
                .putInt(ReminderWorker.KEY_NOTIF_ID, Math.abs(name.hashCode()))
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(d)
                .build();

        WorkManager.getInstance(c).enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, req);
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
