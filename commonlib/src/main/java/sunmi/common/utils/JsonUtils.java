package sunmi.common.utils;


import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Json转换工具类
 *
 * @author yinhui
 * @date 18-6-1
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "ObsoleteSdkInt"})
public final class JsonUtils {

    private static final Gson GSON = new Gson();

    private JsonUtils() {
        throw new UnsupportedOperationException("Utils CANNOT be instantiated!");
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GSON.toJson(src, typeOfSrc);
    }

    public static void toJson(Object src, Appendable writer) {
        GSON.toJson(src, writer);
    }

    public static void toJson(Object src, Type typeOfSrc, Appendable writer) {
        GSON.toJson(src, typeOfSrc, writer);
    }

}
