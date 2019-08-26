package sunmi.common.receiver;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import sunmi.common.model.MiPushMsgBean;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.log.LogCat;

public class MiPushMessageReceiver extends PushMessageReceiver {

    private String TAG = MiPushMessageReceiver.class.getName();
    private String mMessage;
    private String mAlias;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        LogCat.e(TAG, "mipush onReceivePassThroughMessage mMessage = " + mMessage);
        if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
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
            LogCat.e(TAG, "mipush onNotificationMessageClicked mAlias = " + mAlias);
            LogCat.e(TAG, "mipush onNotificationMessageClicked params = " + params);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        LogCat.e(TAG, "mipush onNotificationMessageArrived mMessage = " + mMessage);
        if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {

    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {

    }

}