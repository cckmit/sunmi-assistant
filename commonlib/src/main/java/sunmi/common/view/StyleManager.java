package sunmi.common.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import sunmi.common.utils.CommonHelper;

import java.lang.reflect.Field;

/**
 * 样式设置工具类
 */
public class StyleManager {

    /**
     * 高度大于0时 表明在4.4中开启沉浸式状态栏
     */
    public static int statusBarHeight = 0;

    private static StyleManager mInstance;

    private StyleManager() {
        init();
    }

    /**
     * 获取StyleManager实例
     *
     * @return StyleManager
     */
    public static synchronized StyleManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StyleManager();
        }
        return mInstance;
    }

    /**
     * 初始化
     */
    private void init() {
    }

    /**
     * 获取状态栏高度-
     */
    private static int getStatusBarHeight(Context context) {
        if (statusBarHeight > 0) return statusBarHeight;

        Class<?> c;
        Object obj;
        Field field;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (statusBarHeight <= 0) {
                statusBarHeight = CommonHelper.dp2px(context, 25);//默认高度
            }
        }
        return statusBarHeight;
    }

    /**
     * 设置状态栏样式
     */
    @TargetApi(19)
    public void setStatusBarStyle(Activity activity) {
        View contentView = activity.findViewById(android.R.id.content);
        if (contentView != null) {
            ViewGroup viewGroup = (ViewGroup) contentView;
            View childView = viewGroup.getChildAt(0);
            if (childView != null) {
                childView.setFitsSystemWindows(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);

 /*		    SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.app_title_bg);*/
            statusBarHeight = getStatusBarHeight(activity);
        }
    }

    /**
     * 设置半透明状态
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on, Activity activity) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
