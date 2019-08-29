package com.sunmi.assistant.utils;

import com.sunmi.assistant.mine.model.MessageCountBean;

import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.base.BaseApplication;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/8/29.
 */
public class PushUtils {

    public static void resetUnReadCount(MessageCountBean data) {
        int unreadMsg = data.getUnreadCount();
        int remindUnreadMsg = data.getRemindUnreadCount();
        SpUtils.setUnreadDeviceMsg(data.getModelCountList().get(0).getUnreadCount());
        SpUtils.setUnreadSystemMsg(data.getModelCountList().get(1).getUnreadCount());
        MsgCommonCache.getInstance().setMsgCount(data);
        if (SpUtils.getUnreadMsg() != unreadMsg || SpUtils.getRemindUnreadMsg() != remindUnreadMsg) {
            SpUtils.setUnreadMsg(unreadMsg);
            SpUtils.setRemindUnreadMsg(remindUnreadMsg);
            ShortcutBadger.applyCount(BaseApplication.getInstance(), SpUtils.getRemindUnreadMsg());
        }
    }

}
