package sunmi.common.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sunmi.common.utils.GotoActivityUtils;

/**
 * Description:
 * Created by bruce on 2019/8/30.
 */
public class NotifyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GotoActivityUtils.gotoMsgCenterActivity(context);
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

}
