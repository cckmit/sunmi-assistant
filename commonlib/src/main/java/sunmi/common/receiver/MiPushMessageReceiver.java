package sunmi.common.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.MiPushMsgBean;
import sunmi.common.notification.BaseNotification;
import sunmi.common.service.BadgeIntentService;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

public class MiPushMessageReceiver extends PushMessageReceiver {

    private String TAG = MiPushMessageReceiver.class.getName();
    private String mMessage;
    private String mAlias;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        LogCat.e(TAG, "mipush onNotificationMessageClicked mMessage = " + mMessage);
        if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
            String params = message.getExtra().get("params");
            MiPushMsgBean msg = new Gson().fromJson(params, MiPushMsgBean.class);
            GotoActivityUtils.gotoMsgDetailActivity(context, msg.getModelId(), msg.getModelName());
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getAlias())) {
            SpUtils.setRemindUnreadMsg(SpUtils.getRemindUnreadMsg() + 1);
            SpUtils.setUnreadMsg(SpUtils.getUnreadMsg() + 1);
            LogCat.e(TAG, "mipush onNotificationMessageArrived unreadCount = " + SpUtils.getRemindUnreadMsg());
            ShortcutBadger.applyCount(BaseApplication.getInstance(), SpUtils.getRemindUnreadMsg());
            context.startService(new Intent(context, BadgeIntentService.class)
                    .putExtra("badgeCount", SpUtils.getRemindUnreadMsg())
                    .putExtra("title", message.getTitle())
                    .putExtra("description", message.getDescription()));
//            String params = message.getExtra().get("params");
//            MiPushMsgBean msg = new Gson().fromJson(params, MiPushMsgBean.class);
//            LogCat.e(TAG, "mipush onNotificationMessageClicked msg = " + msg.toString());
            BaseNotification.newInstance().postNotificationName(CommonNotifications.pushMsgArrived);
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {

    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {

    }

}