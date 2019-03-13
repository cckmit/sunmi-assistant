package sunmi.common.base;

import android.content.Context;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public abstract class BaseBootLoader {
    //开发
    public static final String ENV_DEV = "ENV_DEV";
    //测试
    public static final String ENV_TEST = "ENV_TEST";
    //生产
    public static final String ENV_RELEASE = "ENV_RELEASE";

    protected abstract void init(Context context, String env);

}
