package sunmi.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

/**
 * @author yinhui
 * @date 2020-01-15
 */
public class CacheManager {

    public static final String CACHE_AGE_NAME = "cache_age_name";

    /**
     * Cache size = 4 MiB
     */
    private static final int CACHE_SIZE = 4 * 1024 * 1024;

    private LruCache<String, CacheItem> cache;

    private CacheManager() {
        cache = new LruCache<String, CacheItem>(CACHE_SIZE) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull CacheItem value) {
                return value.size;
            }
        };
    }

    private static final class Holder {
        private static final CacheManager INSTANCE = new CacheManager();
    }

    public static CacheManager get() {
        return Holder.INSTANCE;
    }

    @Nullable
    public <T> T get(String key) {
        CacheItem item = cache.get(key);
        if (item == null) {
            return null;
        } else {
            //noinspection unchecked
            return (T) item.item;
        }
    }

    public <T> void put(String key, T value, int size) {
        cache.put(key, new CacheItem<>(value, size));
    }

    private static class CacheItem<ItemT> {

        private ItemT item;
        private int size;

        private CacheItem(ItemT item, int size) {
            this.item = item;
            this.size = size;
        }

    }
}
