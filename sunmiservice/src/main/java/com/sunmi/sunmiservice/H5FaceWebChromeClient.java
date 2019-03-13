package com.sunmi.sunmiservice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Description:增值创新，接入富民银行
 * Created by bruce on 2019/1/22.
 */
public class H5FaceWebChromeClient extends WebChromeClient {
    private Activity activity;
    private Callback callback;

    public H5FaceWebChromeClient(Activity mActivity, Callback callback) {
        this.activity = mActivity;
        this.callback = callback;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (callback != null) callback.onReceivedTitle(title);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    @TargetApi(8)
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    // For Android >= 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        if (WBH5FaceVerifySDK.getInstance().fileChooseForApiBelow21(uploadMsg, acceptType, activity))
            return;

    }

    // For Android >= 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if (WBH5FaceVerifySDK.getInstance().fileChooseForApiBelow21(uploadMsg, acceptType, activity))
            return;
    }

    // For Lollipop 5.0+ Devices
    @TargetApi(21)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (WBH5FaceVerifySDK.getInstance().fileChooseForApi21(webView, filePathCallback, activity, fileChooserParams)) {
            return true;
        }
        return true;
    }

    public interface Callback {

        void onProgressChanged(int progress);

        void onProgressComplete();

        void onReceivedTitle(String title);
    }

}
