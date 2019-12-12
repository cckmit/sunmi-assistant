package sunmi.common.view.webview;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.StatusBarUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-11.
 */
public class BaseJSCall {

    protected BaseActivity context;
    protected SMWebView webView;

    public BaseJSCall(BaseActivity context, SMWebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @JavascriptInterface
    public void lastPageBack(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            int result = jsonObject.getInt("subscribeResult");
            if (result == 1) {
                BaseNotification.newInstance().postNotificationName(CommonNotifications.cloudStorageChange);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.finish();
    }

    @JavascriptInterface
    public void lastPageBack() {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.cloudStorageChange);
        context.finish();
    }

    @JavascriptInterface
    public void setStatusBarDefaultColor(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            final String color = jsonObject.getString("color");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(color, SsConstants.JS_COLOR_WHITE)) {
                        StatusBarUtils.setStatusBarFullTransparent(context);
                    } else {
                        StatusBarUtils.StatusBarLightMode(context);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
