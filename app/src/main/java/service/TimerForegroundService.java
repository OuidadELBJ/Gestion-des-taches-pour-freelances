package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
//import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.freelance.R;
public class TimerForegroundService extends Service {

    public static final String ACTION_START = "timer.ACTION_START";
    public static final String ACTION_PAUSE = "timer.ACTION_PAUSE";
    public static final String ACTION_RESUME = "timer.ACTION_RESUME";
    public static final String ACTION_STOP = "timer.ACTION_STOP";
    public static final String ACTION_TICK = "timer.ACTION_TICK";
    public static final String ACTION_STOPPED = "timer.ACTION_STOPPED";

    public static final String EXTRA_PROJECT_ID = "extra_project_id";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_ELAPSED = "extra_elapsed";
    public static final String EXTRA_START = "extra_start";
    public static final String EXTRA_END = "extra_end";
    public static final String EXTRA_DURATION = "extra_duration";

    private static final int NOTIF_ID = 1001;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean running = false;
    private boolean paused = false;

    private String currentProjectId = null;
    private String currentTaskId = null;

    private long startMillis = 0L;
    private long pauseStartedMillis = 0L;
    private long pausedAccumulatedMillis = 0L;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (!running) return;

            long elapsed = getElapsedMillis();
            broadcastTick(elapsed);
            updateNotification(elapsed);

            handler.postDelayed(this, 1000);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        NotificationChannels.ensure(this);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.d("TIMER_SVC", "onStartCommand action=" + (intent == null ? "null" : intent.getAction()));
        if (intent == null) return START_STICKY;

        String action = intent.getAction();

        if (ACTION_START.equals(action)) {
            String projectId = intent.getStringExtra(EXTRA_PROJECT_ID);
            String taskId = intent.getStringExtra(EXTRA_TASK_ID);

            handleStart(projectId, taskId);

        } else if (ACTION_PAUSE.equals(action)) {
            pauseTimer();

        } else if (ACTION_RESUME.equals(action)) {
            resumeTimer();

        } else if (ACTION_STOP.equals(action)) {
            stopTimer();
        }

        return START_STICKY;
    }

    // ✅ switch automatique + start/resume logique
    private void handleStart(String projectId, String taskId) {
        if (projectId == null || taskId == null) return;

        // 1) Si déjà running sur même tâche
        if (running && projectId.equals(currentProjectId) && taskId.equals(currentTaskId)) {
            if (paused) resumeTimer(); // start après pause = resume
            return;
        }

        // 2) Si un timer tourne sur une autre tâche => SWITCH AUTO:
        //    On finalise l’ancienne session puis on démarre la nouvelle.
        if (running) {
            stopTimerInternal(true); // true = ne pas stopSelf(), on continue pour démarrer l'autre
        }

        // 3) Démarrer nouvelle tâche
        startTimer(projectId, taskId);
    }


    private void startTimer(String projectId, String taskId) {
        android.util.Log.d("TIMER_SVC", "startTimer projectId=" + projectId + " taskId=" + taskId);

        currentProjectId = projectId;
        currentTaskId = taskId;

        running = true;
        paused = false;
        pausedAccumulatedMillis = 0L;
        startMillis = System.currentTimeMillis();

        Notification notif = buildNotification(0);

        // ⬇️⬇️⬇️  NOUVELLE VERSION SANS TYPE  ⬇️⬇️⬇️
        startForeground(NOTIF_ID, notif);
        // ⬆️⬆️⬆️  juste cette ligne, pas de troisième paramètre

        handler.removeCallbacks(ticker);
        handler.post(ticker);

        broadcastTick(0);
        updateNotification(0);
    }
    private void pauseTimer() {
        if (!running || paused) return;
        paused = true;
        pauseStartedMillis = System.currentTimeMillis();
        broadcastTick(getElapsedMillis());
        updateNotification(getElapsedMillis());
    }

    private void resumeTimer() {
        if (!running || !paused) return;
        long now = System.currentTimeMillis();
        pausedAccumulatedMillis += (now - pauseStartedMillis);
        paused = false;
        broadcastTick(getElapsedMillis());
        updateNotification(getElapsedMillis());
    }

    private void stopTimer() {
        if (!running) return;
        stopTimerInternal(false); // false => stopSelf
    }

    // ✅ stop interne : si keepAlive=true, on n'arrête pas le service (pour switch auto)
    private void stopTimerInternal(boolean keepAlive) {
        if (!running) return;

        long end = System.currentTimeMillis();
        long duration = getElapsedMillis();

        running = false;
        paused = false;

        handler.removeCallbacks(ticker);

        Intent stopped = new Intent(ACTION_STOPPED);
        stopped.setPackage(getPackageName());
        stopped.putExtra(EXTRA_PROJECT_ID, currentProjectId);
        stopped.putExtra(EXTRA_TASK_ID, currentTaskId);
        stopped.putExtra(EXTRA_START, startMillis);
        stopped.putExtra(EXTRA_END, end);
        stopped.putExtra(EXTRA_DURATION, duration);
        sendBroadcast(stopped);

        if (!keepAlive) {
            stopForeground(true);
            stopSelf();
        }
    }

    private long getElapsedMillis() {
        long now = System.currentTimeMillis();
        long base;
        if (paused) {
            base = pauseStartedMillis - startMillis - pausedAccumulatedMillis;
        } else {
            base = now - startMillis - pausedAccumulatedMillis;
        }
        return Math.max(0L, base);
    }

    private void broadcastTick(long elapsed) {
        Intent tick = new Intent(ACTION_TICK);
        tick.setPackage(getPackageName());
        tick.putExtra(EXTRA_PROJECT_ID, currentProjectId);
        tick.putExtra(EXTRA_TASK_ID, currentTaskId);
        tick.putExtra(EXTRA_ELAPSED, elapsed);
        sendBroadcast(tick);
    }

    private Notification buildNotification(long elapsedMillis) {
        String content = "Temps: " + format(elapsedMillis)
                + " • Task: " + (currentTaskId == null ? "-" : currentTaskId);

        return new NotificationCompat.Builder(this, NotificationChannels.TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle("Timer en cours")
                .setContentText(content)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(long elapsedMillis) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, buildNotification(elapsedMillis));
    }

    private String format(long ms) {
        long totalSeconds = ms / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
