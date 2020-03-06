package sunmi.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;
import java.util.Random;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class Utils {

    private static Random mRandom = new Random();

    /**
     * 获取manifest文件meta值
     */
    public static String getMetaValue(Context context, String keyName, String defValue) {
        Object value = null;
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.get(keyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value != null ? value.toString() : defValue;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getWebViewStatusBarHeight(Context context) {
        int result = 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return (int) (result / scale + 0.5f);
    }

    public static String getMsgId() {
        if (mRandom == null) {
            mRandom = new Random();
        }
        mRandom.setSeed(System.currentTimeMillis());
        return (mRandom.nextInt(11) + 10) + String.valueOf(System.currentTimeMillis()).substring(5);
    }

    /**
     * 判断手机是否安装某个应用
     *
     * @param pkgName 应用包名
     * @return true：安装，false：未安装
     */
    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pInfo != null) {
                int pInfoSize = pInfo.size();
                for (int i = 0; i < pInfoSize; i++) {
                    String pn = pInfo.get(i).packageName;
                    if (TextUtils.equals(pkgName, pn)) {
                        packageInfo = pInfo.get(i);
                    }
                }
            }
            e.printStackTrace();
        }
        return packageInfo != null;
    }

}
