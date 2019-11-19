package sunmi.common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.ToastUtils;

public class BaseFragment extends Fragment implements BaseNotification.NotificationCenterDelegate {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity mActivity;
    private long lastClickTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        GotoActivityUtils.gotoLoginActivity(TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeObserver();
    }

    /**
     * 根据资源ID获取资源的字符串值
     *
     * @param resId 资源ID
     * @return 返回获取到的值
     */
    protected String getStringById(int resId) {
        return this.getResources().getString(resId);
    }

    /**
     * 设置TextView的字体加粗
     *
     * @param textView TextView组件
     * @param text     要显示的文字
     */
    protected void setBoldTextForTextView(TextView textView, String text) {
        TextPaint textPaint = textView.getPaint();
        textPaint.setFakeBoldText(true);
        textView.setText(text);
    }

    public void shortTip(final String msg) {
        if (mActivity == null) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastForShort(mActivity, msg);
            }
        });
    }

    public void shortTip(final int resId) {
        if (mActivity == null) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastForShort(mActivity, resId);
            }
        });
    }

    public void showLoadingDialog() {
        if (mActivity != null) {
            mActivity.showLoadingDialog();
        }
    }

    public void showLoadingDialog(final String content) {
        if (mActivity != null) {
            mActivity.showLoadingDialog(content);
        }
    }

    public void showLoadingDialog(final String content, @ColorInt final int textColor) {
        if (mActivity != null) {
            mActivity.showLoadingDialog(content, textColor);
        }
    }

    public void showDarkLoading() {
        if (mActivity != null) {
            mActivity.showDarkLoading();
        }
    }

    public void showDarkLoading(final String content) {
        if (mActivity != null) {
            mActivity.showDarkLoading(content);
        }
    }

    public void showDarkLoading(final String content, @ColorInt final int textColor) {
        if (mActivity != null) {
            mActivity.showDarkLoading(content, textColor);
        }
    }

    public void hideLoadingDialog() {
        if (mActivity != null) {
            mActivity.hideLoadingDialog();
        }
    }

    /**
     * 防止多次点击事件处理
     */
    public boolean isFastClick(int intervalTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < intervalTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    // ==============================================
    // ========== Activity redirect ===============
    // ==============================================

    /**
     * Activity之间的跳转
     *
     * @param context 当前上下文
     * @param cls     要跳转到的Activity类
     */
    protected void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

    /**
     * Activity之间的跳转
     *
     * @param context 当前上下文
     * @param cls     要跳转到的Activity类
     * @param bundle  跳转时传递的参数
     */
    protected void openActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Activity之间获取结果的跳转
     *
     * @param context     当前上下文
     * @param cls         要跳转到的Activity类
     * @param requestCode 获取结果时的请求码
     */
    protected void openActivityForResult(Context context, Class<?> cls, int requestCode) {
        openActivityForResult(context, cls, requestCode, null);
    }

    /**
     * Activity之间获取结果的跳转（需要API16版本以上才可以使用）
     *
     * @param context     当前上下文
     * @param cls         要跳转到的Activity类
     * @param requestCode 获取结果时的请求码
     * @param bundle      跳转时传递的参数
     */
    protected void openActivityForResult(Context context, Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    // ============================= 通知相关 begin=============================

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    /**
     * 返回需要注册的通知的ID，默认为null，注册但不独占该通知
     * {@link BaseNotification#addStickObserver(Object, int)}
     */
    public int[] getStickNotificationId() {
        return null;
    }

    /**
     * 返回需要注册的通知的ID，默认为null，注册并独占该通知
     * {@link BaseNotification#addObserver(Object, int)} (Object, int)}
     */
    public int[] getUnStickNotificationId() {
        return null;
    }

    /**
     * 注册通知，该方法为public，允许根据需求手动调用
     */
    public final void addObserver() {
        int[] notificationIds = getUnStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().addObserver(this, notificationId);
            }
        }

        notificationIds = getStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().addStickObserver(this, notificationId);
            }
        }
    }

    /**
     * 反注册通知
     */
    public final void removeObserver() {
        // 1. 反注册
        int[] notificationIds = getUnStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().removeObserver(this, notificationId);
            }
        }

        // 2. 反注册
        notificationIds = getStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().removeObserver(this, notificationId);
            }
        }
    }

    // ============================= 通知相关 end=============================

}
