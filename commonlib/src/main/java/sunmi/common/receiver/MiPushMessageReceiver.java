package sunmi.common.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {

    private String TAG = MiPushMessageReceiver.class.getName();

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v(TAG, "onReceivePassThroughMessage, content -> " + message.getContent());
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//            LogCatLog.v(TAG, "through message topic is : " + message.getTopic());
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//            LogCatLog.v(TAG, "through message alias is : " + message.getAlias());
//        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(TAG, "onNotificationMessageClicked, content -> " + message.getContent());
        boolean isAppRunning = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
//            if (info.topActivity.getPackageName().equals(ENVController.APP_NAME) &&
//                    info.baseActivity.getPackageName().equals(ENVController.APP_NAME)) {
//                isAppRunning = true;
//                break;
//            }
        }

        String ffid = message.getExtra().get("ffid");  //contactFigureId
        String fid = message.getExtra().get("fid");    //contactUserId
        String tfid = message.getExtra().get("tfid");  //接受的角色id
        String gid = message.getExtra().get("gid");

        Log.v(TAG, "onNotificationMessageClicked, isAppRunning -> " + isAppRunning);
//        if (!isAppRunning) {
//            Intent intent = new Intent(context, LaunchActivity_.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (!TextUtils.isEmpty(gid)) {
//                intent.putExtra(MainActivity_.GROUP_ID_EXTRA, gid);
//                intent.putExtra(MainActivity_.FFID_EXTRA, ffid);
//                intent.putExtra(MainActivity_.FID_EXTRA, fid);
//            } else if (!TextUtils.isEmpty(tfid)) {
//                intent.putExtra(MainActivity_.TFID_EXTRA, tfid);
//                intent.putExtra(MainActivity_.FFID_EXTRA, ffid);
//                intent.putExtra(MainActivity_.FID_EXTRA, fid);
//            }
//            context.startActivity(intent);
//        } else {
//            if (!TextUtils.isEmpty(gid)) {
//                LocalGroupDTO localGroupDTO = GroupCache.getInstance().getGroup(gid);
//                if (localGroupDTO != null) {
//                    Intent intent = new Intent(context, GroupChatActivity_.class);
//                    intent.putExtra(GroupChatActivity_.GROUP_DTO_EXTRA, localGroupDTO);
//                    context.startActivity(intent);
//                } else {
//                    MainActivity_.intent(context).start();
//                }
//            } else if (!TextUtils.isEmpty(tfid)) {
//                Contact contact = ContactManager.getInstance().getContact(ffid, tfid);
//                if (null != contact) {
//                    Intent intent = new Intent(context, ChatChatActivity_.class);
//                    intent.putExtra(ChatChatActivity_.CONTACT_FIGURE_ID_EXTRA, contact.contactFigureId);
//                    intent.putExtra(ChatChatActivity_.CURRENT_FIGURE_ID_EXTRA, contact.figureId);
//                    intent.putExtra(ChatChatActivity_.CONTACT_NAME_EXTRA, contact.username);
//                    intent.putExtra(ChatChatActivity_.CONTACT_USER_ID_EXTRA, contact.contactUserId);
//                    intent.putExtra(ChatChatActivity_.RELATION_TYPE_EXTRA, contact.getRelationShip(context));
//                    context.startActivity(intent);
//                } else {
//                    MainActivity_.intent(context).start();
//                }
//            } else {
//                MainActivity_.intent(context).start();
//            }
//            LongLinkServiceManager.getInstance().getLongLinkService().longLinkInit();
//        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(TAG, "onNotificationMessageArrived, content -> " + message.getContent());
//        LongLinkServiceManager.getInstance().checkLongLinkState();
        String num = message.getExtra().get("count");
        if (!TextUtils.isEmpty(num)) {
//            ShortcutBadger.applyCount(HHApplication.context, HHUtils.parseInt(num));
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(TAG, "onCommandResult is called. " + message.toString());
//        List<String> arguments = message.getCommandArguments();
//        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
//        if (MiPushClient.COMMAND_REGISTER.equals(message.getCommand())) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                LogCatLog.v(TAG, " MiPush has register successful.");
//                LogCatLog.e(TAG, " MiPush regId is: " + cmdArg1+" time is "+ System.currentTimeMillis());
//            } else {
//                LogCatLog.v(TAG, " MiPush has register fail.");
//            }
//        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(TAG, "onReceiveRegisterResult is called. " + message.toString());
//        List<String> arguments = message.getCommandArguments();
//        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
//        if (MiPushClient.COMMAND_REGISTER.equals(message.getCommand())) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                LogCatLog.v(TAG, " MiPush has register successful.");
//                LogCatLog.e(TAG, " MiPush regId is: " + cmdArg1+" time is "+ System.currentTimeMillis());
//                DeviceInfo.getInstance().setPushId(cmdArg1);
//            } else {
//                LogCatLog.v(TAG, " MiPush has register fail.");
//            }
//        }
    }

}
