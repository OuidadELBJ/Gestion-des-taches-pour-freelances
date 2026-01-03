package service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannels {
    public static final String TIMER_CHANNEL_ID = "timer_channel";
    public static final String DEADLINE_CHANNEL_ID = "deadline_channel";
    public static void ensure(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel timer = new NotificationChannel(
                TIMER_CHANNEL_ID, "Timer", NotificationManager.IMPORTANCE_LOW);
        nm.createNotificationChannel(timer);

        NotificationChannel deadline = new NotificationChannel(
                DEADLINE_CHANNEL_ID, "Deadlines", NotificationManager.IMPORTANCE_DEFAULT);
        nm.createNotificationChannel(deadline);
    }
}
