package sunmi.common.base;

import android.app.Activity;

import org.litepal.LitePalApplication;

import java.util.LinkedList;
import java.util.List;

import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * 完全退出应用程序类（在每个activity的onCreate方法中调用addActivity方法，在应用程序退出时调用exit方法，就可以完全退出）
 */
public class BaseApplication extends LitePalApplication {
    private static BaseApplication instance = null;
    private List<Activity> activityList = new LinkedList<>();
    public static boolean isCheckedToken;//是否有网络启动

    public synchronized static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public void finishActivities() {
        for (Activity activity : activityList) {
            if (activity != null)
                activity.finish();
        }
        activityList.clear();
    }

    public void quit() {
        try {
            finishActivities();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

}
