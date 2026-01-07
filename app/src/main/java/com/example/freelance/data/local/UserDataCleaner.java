package com.example.freelance.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

import notifications.ReminderScheduler;

public final class UserDataCleaner {

    private static final String PREFS = "user_scope_prefs";
    private static final String KEY_LAST_UID = "last_uid";

    private UserDataCleaner() {}

    /**
     * Appelle ça après login (ou au démarrage si déjà connecté).
     * Si l'uid a changé => on wipe toute la DB locale + reminders.
     */
    public static void ensureUserScope(Context context, String currentUid, Runnable onDone) {
        Context app = context.getApplicationContext();

        Executors.newSingleThreadExecutor().execute(() -> {
            SharedPreferences sp = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String lastUid = sp.getString(KEY_LAST_UID, null);

            boolean changed = (lastUid == null) || (!lastUid.equals(currentUid));

            if (changed) {
                // 1) wipe Room
                AppDatabase.getInstance(app).clearAllTables();

                // 2) wipe reminders (WorkManager)
                // rescheduleAll() commence par cancel TAG_REMINDER_ALL -> parfait
                ReminderScheduler.rescheduleAll(app);

                // 3) save new uid
                sp.edit().putString(KEY_LAST_UID, currentUid).apply();
            }

            new Handler(Looper.getMainLooper()).post(onDone);
        });
    }

    /**
     * Appelle ça au logout => wipe DB + remove uid cache.
     */
    public static void clearOnLogout(Context context, Runnable onDone) {
        Context app = context.getApplicationContext();

        Executors.newSingleThreadExecutor().execute(() -> {
            // wipe Room
            AppDatabase.getInstance(app).clearAllTables();

            // cancel reminders
            ReminderScheduler.rescheduleAll(app);

            // remove uid saved
            SharedPreferences sp = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            sp.edit().remove(KEY_LAST_UID).apply();

            new Handler(Looper.getMainLooper()).post(onDone);
        });
    }
}