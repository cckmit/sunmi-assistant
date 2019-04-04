package sunmi.common.rpc.mqtt;

import android.util.LruCache;

import sunmi.common.rpc.sunmicall.RequestBean;
import sunmi.common.utils.log.LogCat;

public class MessageCache {
    private static final String TAG = "MessageCache";
    private static MessageCache mInstance;

    public static MessageCache getInstance() {
        if (mInstance == null) {
            synchronized (MessageCache.class) {
                if (mInstance == null) {
                    mInstance = new MessageCache();
                }
            }
        }
        return mInstance;
    }

    private LruCache<String, RequestBean> lruCache;

    private MessageCache() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        lruCache = new LruCache<>(cacheSize);
    }

    // 把message对象加入到缓存中
    public void addMessage(String key, RequestBean requestBean) {
        if (getMessage(key) == null) {
            lruCache.put(key, requestBean);
        }
    }

    // 从缓存中得到message对象
    public RequestBean getMessage(String key) {
        LogCat.e(TAG, "lruCache size: " + lruCache.size());
        return lruCache.get(key);
    }

    // 从缓存中删除指定的message
    public void removeMessage(String key) {
        lruCache.remove(key);
    }

}
