package com.sunmi.sunmiservice;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.xiaojinzi.component.impl.Router;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.AppApi;
import sunmi.common.router.IpcApi;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.webview.SMWebView;

public class JSCall {

    private BaseActivity context;
    private SMWebView webView;
    private Handler handler = new Handler();
    private IWXAPI api;

    public JSCall(BaseActivity context, SMWebView webView) {
        this.context = context;
        this.webView = webView;
    }

    public void setApi(IWXAPI api) {
        this.api = api;
    }

    @JavascriptInterface
    public void openMiniProgram(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            String userName = jsonObject.getString("userName");
            String miniProgramType = jsonObject.getString("miniProgramType");
            String path = jsonObject.getString("path");
            LogCat.e("JSCall", "openMiniProgram, jsonObject = " + jsonObject);
            launchMiniProgram(userName, path, miniProgramType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void pushWindow(String arg) {
//        url: URL; // 要打开的⻚页⾯面url
//        data?: object; // url的参数，会以 query string 跟在 url 后⾯面。
        try {
            JSONObject jsonObject = new JSONObject(arg);
            LogCat.e("JSCall", "pushWindow, jsonObject = " + jsonObject);
            if (jsonObject.has("url"))
                startWebViewActivity(jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void callUp(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            if (jsonObject.has("phone")) {
                LogCat.e("JSCall", "callUp, jsonObject = " + jsonObject);
                callPhone(jsonObject.getString("phone"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showLoading(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            LogCat.e("JSCall", "showLoading, jsonObject = " + jsonObject);
            if (jsonObject.has("content")) {
                context.showDarkLoading(jsonObject.getString("content"));
            } else {
                context.showDarkLoading();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void hideLoading() {
        context.hideLoadingDialog();
    }

    @JavascriptInterface
    public void showToast(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            LogCat.e("JSCall", "showToast, jsonObject = " + jsonObject);
            if (jsonObject.has("content"))
                context.shortTip(jsonObject.getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void nativeCallJavaScript(final String callbackName) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.callJavascript("App.nativeCallbacks." + callbackName, null);
            }
        });
    }

    @JavascriptInterface
    public void pushWindowWithClose(final String arg) {//pushWindowWithClose(params: PushWindowOption): void;
        try {
            JSONObject jsonObject = new JSONObject(arg);
            LogCat.e("JSCall", "pushWindow, jsonObject = " + jsonObject);
            if (jsonObject.has("url"))
                startWebViewWithCloseActivity(jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void pushToSunmiStore(final String arg) {//商城
        try {
            JSONObject jsonObject = new JSONObject(arg);
            LogCat.e("JSCall", "pushToSunmiStore, jsonObject = " + jsonObject);
            if (jsonObject.has("url"))
                startWebViewSunmiMallActivity(jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void popSunmiStore(final String arg) {//关闭商城
        LogCat.e("JSCall", "popSunmiStore, close = " + arg);
        context.finish();
    }

    @JavascriptInterface
    public String getUserInfo() {
        LogCat.e("JSCall", "getUserInfo");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SpUtils.getUID());
            jsonObject.put("token", SpUtils.getSsoToken());
            jsonObject.put("source", "5");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("appId", SunmiServiceConfig.FUMINBAO_APP_ID);
            jsonObject1.put("appSecret", SunmiServiceConfig.FUMINBAO_SECRET);
            jsonObject.put("sunmiOpen", jsonObject1);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
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

    @JavascriptInterface
    public void jumpPage(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            final String url = jsonObject.getString("url");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.equals(url, SsConstants.JS_BIND_SS)) {
                        Router.withApi(IpcApi.class).goToIpcStartConfig(context, CommonConstants.TYPE_IPC_SS);
                    } else if (TextUtils.equals(url, SsConstants.JS_BIND_SAAS)) {
                        Router.withApi(AppApi.class).gotoImportOrderPreview(context);
                    } else if (TextUtils.equals(url, SsConstants.JS_MALL_ORDER)) {
                        WebViewSunmiMallActivity_.intent(context).mUrl(SunmiServiceConfig.SUNMI_MALL_HOST
                                + "my-order?channel=2&subchannel=4").start();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void cashVideoSubscribe() {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.cashVideoSubscribe);
        context.finish();
    }

    @JavascriptInterface
    public void copy(String arg) {
        try {
            JSONObject jsonObject = new JSONObject(arg);
            String content = jsonObject.getString("content");
            if (content != null) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("content", content);
                cm.setPrimaryClip(clipData);
                context.shortTip(R.string.tip_copy_success);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void launchMiniProgram(String userName, String path, String miniProgramType) {
        if (api == null) return;
        if (!api.isWXAppInstalled()) {
            context.shortTip(R.string.tip_wechat_not_installed);
            return;
        }

        int miniProgramTypeInt = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        try {
            miniProgramTypeInt = Integer.parseInt(miniProgramType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WXLaunchMiniProgram.Req miniProgramReq = new WXLaunchMiniProgram.Req();
        miniProgramReq.userName = userName;// 小程序原始id
        miniProgramReq.path = path; //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        miniProgramReq.miniprogramType = miniProgramTypeInt;// 可选打开 开发版，体验版和正式版
        api.sendReq(miniProgramReq);
    }

    /**
     * 拨打电话
     *
     * @param phoneNum 电话号码
     */
    private void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);//直接拨打电话（ACTION_DIAL是拨号盘）
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 弹dialog
     */
    private void showDialog(String title, String msg, String btnSure) {
        new CommonDialog.Builder(context).setTitle(title).setMessage(msg)
                .setConfirmButton(btnSure).create().show();
    }

    /**
     * 加载网页
     */
    private void startWebViewActivity(String url) {
        WebViewActivity_.intent(context).url(url).start();
    }

    /**
     * 加载富民宝网页
     */
    private void startWebViewWithCloseActivity(String url) {
        WebViewWithCloseActivity_.intent(context).url(url).start();
    }

    /**
     * 加载商米商城网页
     */
    private void startWebViewSunmiMallActivity(String url) {
        WebViewSunmiMallActivity_.intent(context).mUrl(url).start();
    }

}
