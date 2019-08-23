package sunmi.common.receiver;

import android.content.Context;
import android.text.TextUtils;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

import sunmi.common.utils.log.LogCat;

public class MiPushMessageReceiver extends PushMessageReceiver {

    private String TAG = MiPushMessageReceiver.class.getName();
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
            LogCat.e(TAG, "mipush onReceivePassThroughMessage mTopic = " + mTopic);
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
            LogCat.e(TAG, "mipush onReceivePassThroughMessage mAlias = " + mAlias);
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
            LogCat.e(TAG, "mipush onReceivePassThroughMessage mUserAccount = " + mUserAccount);
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
            LogCat.e(TAG, "mipush onNotificationMessageClicked mTopic = " + mTopic);
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
            LogCat.e(TAG, "mipush onNotificationMessageClicked mAlias = " + mAlias);
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
            LogCat.e(TAG, "mipush onNotificationMessageClicked mUserAccount = " + mUserAccount);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
            LogCat.e(TAG, "mipush onNotificationMessageArrived mTopic = " + mTopic);
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
            LogCat.e(TAG, "mipush onNotificationMessageArrived mAlias = " + mAlias);
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
            LogCat.e(TAG, "mipush onNotificationMessageArrived mUserAccount = " + mUserAccount);
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                LogCat.e(TAG, "mipush regid = " + mRegId);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
    }

}