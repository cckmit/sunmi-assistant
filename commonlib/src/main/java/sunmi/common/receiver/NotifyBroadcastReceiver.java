package sunmi.common.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaojinzi.component.impl.Router;

import sunmi.common.router.AppApi;
import sunmi.common.utils.GotoActivityUtils;

/**
 * Description:
 * Created by bruce on 2019/8/30.
 */
public class NotifyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Router.withApi(AppApi.class).goToMsgCenter(context);
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

}
