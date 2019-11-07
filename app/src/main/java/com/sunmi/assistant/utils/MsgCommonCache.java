package com.sunmi.assistant.utils;

import android.util.LruCache;

import com.sunmi.assistant.mine.model.MessageCountBean;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-27.
 */
public class MsgCommonCache {

    private final String MSG_COUNT_BEAN = "msg_count_bean";

    private static final class Singleton {
        private static final MsgCommonCache INSTANCE = new MsgCommonCache();
    }

    public static MsgCommonCache getInstance() {
        return Singleton.INSTANCE;
    }

    private LruCache<String, Object> lruCache;

    private MsgCommonCache() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        lruCache = new LruCache<>(cacheSize);
    }

    public void setMsgCount(MessageCountBean msgBean) {
        lruCache.put(MSG_COUNT_BEAN, msgBean);
    }

    public MessageCountBean getMsgCount() {
        return (MessageCountBean)lruCache.get(MSG_COUNT_BEAN);
    }
}
