package notifications;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.freelance.R;
import service.NotificationChannels;

public class ReminderWorker extends Worker {

    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_NOTIF_ID = "notif_id";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        NotificationChannels.ensure(getApplicationContext());

        String title = getInputData().getString(KEY_TITLE);
        String text = getInputData().getString(KEY_TEXT);
        int notifId = getInputData().getInt(KEY_NOTIF_ID, (int) System.currentTimeMillis());

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(getApplicationContext(), NotificationChannels.TIMER_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_timer)
                        .setContentTitle(title == null ? "Rappel" : title)
                        .setContentText(text == null ? "" : text)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(notifId, b.build());
        return Result.success();
    }
}
