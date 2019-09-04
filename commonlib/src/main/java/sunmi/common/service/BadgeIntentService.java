package sunmi.common.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.commonlibrary.R;

import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.receiver.NotifyBroadcastReceiver;

/**
 * Description:
 * Created by bruce on 2019/8/29.
 */
public class BadgeIntentService extends IntentService {

    private static final String NOTIFICATION_CHANNEL = "me.leolin.shortcutbadger.example";

    private int notificationId = 0;

    public BadgeIntentService() {
        super("BadgeIntentService");
    }

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int badgeCount = intent.getIntExtra("badgeCount", 0);
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("description");
            mNotificationManager.cancel(notificationId);
            notificationId++;

            Notification.Builder builder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(getString(R.string.notice_unread_message, badgeCount))
                    .setSmallIcon(R.mipmap.ic_logo);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupNotificationChannel();
                builder.setChannelId(NOTIFICATION_CHANNEL);
            }

            Intent intent1 = new Intent(getApplicationContext(), NotifyBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            ShortcutBadger.applyNotification(getApplicationContext(), notification, badgeCount);
            mNotificationManager.notify(notificationId, notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setupNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "ShortcutBadger Sample",
                NotificationManager.IMPORTANCE_DEFAULT);

        mNotificationManager.createNotificationChannel(channel);
    }

}