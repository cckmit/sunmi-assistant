package sunmi.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;

import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.mqtt.MqttManager;
import sunmi.common.utils.log.LogCat;

public class CommonHelper {

    public static String getLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();
        return locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase();
    }

    /**
     * 获取类名称
     *
     * @param e 异常类对象(eg:new Exception())
     * @return 返回类名称
     */
    public static String getCName(Exception e) {
        return e.getStackTrace()[0].getClassName();
    }

    /**
     * 获取手机屏幕的宽高
     * activity 上下文
     *
     * @return 返回屏幕的宽高点（即右下角的点）
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    /**
     * 获取手机屏幕的宽度（像素）
     *
     * @param activity 上下文
     * @return 返回手机屏幕的宽度
     */
    public static int getScreenWidth(Context activity) {
        return getScreenSize(activity).x;
    }

    /**
     * 获取手机屏幕的高度（像素）
     *
     * @param activity 上下文
     * @return 返回手机屏幕高度
     */
    public static int getScreenHeight(Context activity) {
        return getScreenSize(activity).y;
    }

    /**
     * 根据手机的分辨率从单位dp转成为单位 px(像素)
     *
     * @param context 上下文
     * @param dpValue 设置的dp值
     * @return 返回转换后的px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从单位px(像素)转成单位dp
     *
     * @param context 上下文
     * @param pxValue 设置的px值
     * @return 返回转换之后的dp值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断当前手机是否有Root权限
     *
     * @return true-有;false-无
     */
    public static boolean isRoot() {
        boolean result = false;

        try {
            result = (new File("/system/bin/su").exists()) || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        return result;
    }

    /**
     * 获取应用程序包名称
     *
     * @param context 上下文
     * @return 返回应用程序包名称
     */
    public static String getAppPackageName(Context context) {
        String appInfo = "";

        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.packageName;
        }
        return appInfo;
    }

    /**
     * 获取应用程序版本名称
     *
     * @param context 上下文
     * @return 返回应用程序版本名称
     */
    public static String getAppVersionName(Context context) {
        String appInfo = "";

        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.versionName;
        }
        return appInfo;
    }

    /**
     * 获取应用程序信息
     * context 上下文
     *
     * @return 返回应用程序版本代码
     */
    public static int getAppVersionCode(Context pContext) {
        int appInfo = -1;

        PackageManager mPackageManager = pContext.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(pContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.versionCode;
        }
        return appInfo;
    }

    /**
     * 拨打电话
     *
     * @param activity 当前Activity
     * @param phoneNO  电话号码
     */
    public static void tel(Activity activity, String phoneNO) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNO));
        activity.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param activity 当前Activity
     * @param to
     * @param content
     */

    public static void sendSMS(Activity activity, String to, String content) {
        Uri smsToUri = Uri.parse("smsto:" + to);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", content);
        activity.startActivity(intent);
    }

    public static void installShortcut(Context context, String appName, Class<?> mainClass, Bitmap icon) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setClass(context, mainClass);
        installShortcut(context, appName, mainIntent, icon);
    }

    public static void installShortcut(Context context, String appName, Intent mainIntent, Bitmap icon) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加  
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
//        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));  
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        context.sendBroadcast(shortcutIntent);
    }

    public static void sendBadgeNumber(Context context, String packageName, String lancherActivityClassName, String number) {
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
//            int numInt = Integer.valueOf(number);
//            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, packageName, lancherActivityClassName, number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSony(context, packageName, lancherActivityClassName, number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSamsumg(context, packageName, lancherActivityClassName, number);
        } else {
        }
    }

    private static void sendToXiaoMi(Context context, String packageName, String lancherActivityClassName, String number) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有" + number + "未读消息");
            builder.setTicker("您有" + number + "未读消息");
            builder.setAutoCancel(true);
//            builder.setSmallIcon(R.drawable.common_icon_lamp_light_red);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
//            Class<?> miuiNotificationClass = Class.forName("android.app.MiuiNotification");
//            Object miuiNotification = miuiNotificationClass.newInstance();
//            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
//            field.setAccessible(true);
//            field.set(miuiNotification, number);// 设置信息数
//            field = notification.getClass().getField("extraNotification"); 
//            field.setAccessible(true);
//            field.set(notification, miuiNotification);
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, NumberHelper.toInt(number));
        } catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name", packageName + "/" + lancherActivityClassName);
            localIntent.putExtra("android.intent.extra.update_application_message_text", number);
            context.sendBroadcast(localIntent);
        } finally {
            if (notification != null && isMiUIV6) {
                //miui6以上版本需要使用通知发送
//            nm.notify(101010, notification); 
                nm.notify(0, notification);
            }
        }

    }

    private static void sendToSony(Context context, String packageName, String lancherActivityClassName, String number) {
        boolean isShow = true;
        if (TextUtils.equals(number, "0")) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", lancherActivityClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", packageName);//包名
        context.sendBroadcast(localIntent);
    }

    private static void sendToSamsumg(Context context, String packageName, String lancherActivityClassName, String number) {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);//数字
        localIntent.putExtra("badge_count_package_name", packageName);//包名
        localIntent.putExtra("badge_count_class_name", lancherActivityClassName); //启动页
        context.sendBroadcast(localIntent);
    }

    public static void openFile(Activity activity, File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        activity.startActivity(intent);
    }

    public static String getMIMEType(File f) {
        String end = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
        String type = "";
        if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
                || end.equals("amr") || end.equals("mpeg") || end.equals("mp4")
                || end.equals("m4a")) {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg")) {
            type = "image";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }

    //根据字符串型的资源名获取对应资源id
    public static int getResource(Context ctx, String imageName, int defResId) {
        int resId = ctx.getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        if (resId == 0) {  //如果没有在"mipmap"下找到imageName,将会返回0
            resId = defResId;//设置默认
        }
        return resId;
    }

    /**
     * @param button  button
     * @param isClick 按钮是否可点击
     */
    public static void isCanClick(Button button, boolean isClick) {
        if (isClick) {
            button.setAlpha(1f);
            button.setEnabled(true);
        } else {
            button.setAlpha(0.5f);
            button.setEnabled(false);
        }
    }

    /**
     * 如果小数点后为零显示整数否则保留
     *
     * @param num
     * @return
     */
    public static String floatTrans(float num) {
        if (Math.round(num) - num == 0) {
            return String.valueOf((int) num);
        }
        return String.valueOf(num);
    }

    public static void saveLoginInfo(UserInfoBean bean) {
        if (bean == null) {
            return;
        }
        SpUtils.setMobile(bean.getPhone());
        SpUtils.setUID(bean.getId() + "");
        if (!TextUtils.isEmpty(bean.getUsername())) {
            SpUtils.setUsername(bean.getUsername());
        }
        if (!TextUtils.isEmpty(bean.getEmail())) {
            SpUtils.setEmail(bean.getEmail());
        }
        BaseApplication.isCheckedToken = true;
        SpUtils.setLoginStatus("Y");
        if (!TextUtils.isEmpty(bean.getOrigin_icon())) {
            SpUtils.setAvatarUrl(bean.getOrigin_icon());
        }
        DBUtils.switchDb(SpUtils.getUID());
        CrashReport.setUserId(SpUtils.getUID());
        MiPushClient.setAlias(BaseApplication.getInstance(), SpUtils.getUID(), null);
    }

    public static void saveCompanyShopInfo(int companyId, String companyName, int saasExist, int shopId, String shopName) {
        SpUtils.setCompanyId(companyId);
        SpUtils.setCompanyName(companyName);
        SpUtils.setSaasExist(saasExist);
        SpUtils.setShopId(shopId);
        SpUtils.setShopName(shopName);
    }

    public static void logout() {
        MiPushClient.unsetAlias(BaseApplication.getInstance(), SpUtils.getUID(), null);
        SpUtils.setLoginStatus("");
        SpUtils.setSsoToken("");
        SpUtils.setStoreToken("");
        SpUtils.setUsername("");
        SpUtils.setAvatarUrl("");
        SpUtils.setUID("");
        SpUtils.setCompanyId(-1);
        SpUtils.setCompanyName("");
        SpUtils.setShopId(-1);
        SpUtils.setShopName("");
        SpUtils.setRemindUnreadMsg(-1);
        SpUtils.setUnreadMsg(-1);
        SpUtils.setUnreadSystemMsg(-1);
        SpUtils.setUnreadDeviceMsg(-1);
        BaseApplication.isCheckedToken = false;
        ShortcutBadger.applyCount(BaseApplication.getInstance(), 0);
//        MQTTManager.getInstance().disconnect();
        MqttManager.getInstance().disconnect();
    }

    public static boolean isGooglePlay() {
        return TextUtils.equals(WalleChannelReader.getChannel(BaseApplication.getInstance()),
                CommonConstants.GOOGLE_PLAY);
    }


}
