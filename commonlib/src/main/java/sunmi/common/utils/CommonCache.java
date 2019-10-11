package sunmi.common.utils;

import android.util.LruCache;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-27.
 */
public class CommonCache {

    private final String MSG_COUNT_BEAN = "msg_count_bean";

    private static final class Singleton {
        private static final CommonCache INSTANCE = new CommonCache();
    }

    public static CommonCache getInstance() {
        return Singleton.INSTANCE;
    }

    private LruCache<String, Object> lruCache;

    private CommonCache() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        lruCache = new LruCache<>(cacheSize);
    }

    public void setMsgCount(Object msgBean) {
        lruCache.put(MSG_COUNT_BEAN, msgBean);
    }

    public Object getMsgCount() {
        return lruCache.get(MSG_COUNT_BEAN);
    }
}
