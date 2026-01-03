package data.prefs;



import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ReminderPrefs {

    private static final String PREFS = "reminder_prefs";
    private static final String K_ENABLED = "enabled";
    private static final String K_TASKS = "tasks";
    private static final String K_PROJECTS = "projects";
    private static final String K_OFFSETS = "offsets";
    private static final String K_AT_NINE = "at_nine";

    public boolean enabled = true;
    public boolean remindTasks = true;
    public boolean remindProjects = false;

    public Set<Long> offsetsMillis = new HashSet<>();
    public boolean atNine = false;

    public static ReminderPrefs load(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        ReminderPrefs p = new ReminderPrefs();

        p.enabled = sp.getBoolean(K_ENABLED, true);
        p.remindTasks = sp.getBoolean(K_TASKS, true);
        p.remindProjects = sp.getBoolean(K_PROJECTS, false);
        p.atNine = sp.getBoolean(K_AT_NINE, false);

        Set<String> raw = sp.getStringSet(K_OFFSETS, null);
        if (raw == null || raw.isEmpty()) {
            p.offsetsMillis.add(60 * 60_000L); // défaut 1h
        } else {
            for (String s : raw) {
                try { p.offsetsMillis.add(Long.parseLong(s)); } catch (Exception ignored) {}
            }
        }
        return p;
    }

    public void save(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        Set<String> raw = new HashSet<>();
        for (Long v : offsetsMillis) raw.add(String.valueOf(v));

        sp.edit()
                .putBoolean(K_ENABLED, enabled)
                .putBoolean(K_TASKS, remindTasks)
                .putBoolean(K_PROJECTS, remindProjects)
                .putBoolean(K_AT_NINE, atNine)
                .putStringSet(K_OFFSETS, raw)
                .apply();
    }
}
