package com.example.freelance.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

public final class UserDataCleaner {

    private static final String PREFS = "user_scope_prefs";
    private static final String KEY_LAST_UID = "last_uid";

    private UserDataCleaner() {}

    public static void ensureUserScope(Context context, String currentUid, Runnable onDone) {
        Context app = context.getApplicationContext();

        Executors.newSingleThreadExecutor().execute(() -> {
            // On NE wipe PAS la DB, on NE touche PAS aux reminders.

            // Optionnel: stocker le dernier uid (si tu veux garder l’info)
            SharedPreferences sp = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if (currentUid != null) {
                sp.edit().putString(KEY_LAST_UID, currentUid).apply();
            }

            new Handler(Looper.getMainLooper()).post(onDone);
        });
    }
    public static void clearOnLogout(Context context, Runnable onDone) {
        // On NE wipe PAS la DB, on NE cancel PAS les reminders.
        new Handler(Looper.getMainLooper()).post(onDone);
    }
}
