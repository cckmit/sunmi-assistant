package sunmi.common.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import org.litepal.LitePalApplication;

import java.util.LinkedList;
import java.util.List;

import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogHelper;

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
            LogHelper.exportLog(CommonHelper.getCName(new Exception()), CommonHelper.getMName(new Exception()),
                    e.getMessage(), true);
        } finally {
            System.exit(0);
        }
    }

    /**
     * 创建桌面快捷方式
     *
     * @param context 上下文
     * @param appName 应用程序名称
     * @param appIcon 应用程序图标
     */
    public void addShortcuts(Activity context, int appName, int appIcon) {
        Intent shortcuts = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");// 快捷方式的名称
        shortcuts.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appName));
        shortcuts.putExtra("duplicate", false); // 不允许重复创建

        ComponentName comp = new ComponentName(context.getPackageName(), context.getPackageName() + "."
                + context.getLocalClassName());// 确保通过快捷方式和桌面图标打开的是一个应用程序
        Intent shortcutsIntent = new Intent(Intent.ACTION_MAIN);
        shortcutsIntent.setComponent(comp);
        shortcutsIntent.setClassName(context, context.getClass().getName());

        shortcuts.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutsIntent);

        // 快捷方式的图标
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(context, appIcon);
        shortcuts.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        context.sendBroadcast(shortcuts);
    }

    /**
     * 判断快捷方式是否存在
     *
     * @param context 上下文
     * @param appName 应用程序名称
     * @return 返回是否创建:true-已创建;false-未创建
     */
    public boolean isExistsShortcuts(Activity context, int appName) {
        boolean isInstallShortcuts = false;

        final String strUri;
        if (Build.VERSION.SDK_INT < 8)
            strUri = "content://com.android.launcher.settings/favorites?notify=true";
        else
            strUri = "content://com.android.launcher2.settings/favorites?notify=true";

        final Uri CONTENT_URI = Uri.parse(strUri);

        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(CONTENT_URI, null, "title=?", new String[]{context.getString(appName).trim()}, null);
        if (c != null && c.getCount() > 0) {
            isInstallShortcuts = true;
        }

        return isInstallShortcuts;
    }

    /**
     * 删除快捷方式
     *
     * @param context 上下文
     * @param appName 应用程序名称
     */
    public void deleteShortcuts(Activity context, int appName) {
        Intent shortcuts = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

        // 快捷方式的名称
        shortcuts.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appName));
        String appClass = context.getPackageName() + "." + context.getLocalClassName();
        ComponentName comp = new ComponentName(context.getPackageName(), appClass);
        shortcuts.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

        context.sendBroadcast(shortcuts);
    }
}
