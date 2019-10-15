package sunmi.common.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.commonlibrary.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.webview.SMWebView;

/**
 * 用户协议，隐私
 */
@EActivity(resName = "activity_protocol")
public class ProtocolActivity extends BaseActivity {

    //用户协议
    public final static String PROTOCOL_USER = "https://account.sunmi.com/static/userAgreement.html";
    //隐私协议
    public final static String PROTOCOL_PRIVATE = "https://account.sunmi.com/static/privacyCn.html";
    //用户协议英文
    public final static String PROTOCOL_USER_ENGLISH = "https://account.sunmi.com/static/userAgreement-en.html";
    //隐私协议英文
    public final static String PROTOCOL_PRIVATE_ENGLISH = "https://account.sunmi.com/static/privacyEn.html";
    //微信
    public final static String WX_AUTH_HELP = "https://webapi.wap.sunmi.com/webapi/wap/app/static/wechat/index.html";
    //平台数据协议
    public final static String AUTH_PLATFORM = "file:///android_asset/auth_merchant.html";
    public static final int USER_PROTOCOL = 0;
    public static final int USER_PRIVATE = 1;
    public static final int USER_AP_PROTOCOL = 2;
    public static final int USER_AP_PRIVATE = 3;
    public static final int USER_WX_HELP = 4;
    public static final int USER_AUTH_PLATFORM = 5;
    @ViewById(resName = "wv_protocol")
    SMWebView webView;
    @Extra
    int protocolType;
    private Timer timer;//计时器
    private long timeout = 5000;//超时时间


    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        if (CommonHelper.isGooglePlay()) {
            initGoogle();
        } else {
            initNormal();
        }
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
    }

    private void initNormal() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            loadWebView(TextUtils.equals("en_us", CommonHelper.getLanguage()) ? PROTOCOL_USER_ENGLISH : PROTOCOL_USER);
        } else if (protocolType == USER_PRIVATE) {
            loadWebView(TextUtils.equals("en_us", CommonHelper.getLanguage()) ? PROTOCOL_PRIVATE_ENGLISH : PROTOCOL_PRIVATE);
        } else if (protocolType == USER_AP_PROTOCOL) { //快速配置路由器协议
            loadWebView(TextUtils.equals("en_us", CommonHelper.getLanguage()) ? PROTOCOL_USER_ENGLISH : PROTOCOL_USER);
        } else if (protocolType == USER_AP_PRIVATE) {
            loadWebView(TextUtils.equals("en_us", CommonHelper.getLanguage()) ? PROTOCOL_PRIVATE_ENGLISH : PROTOCOL_PRIVATE);
        } else if (protocolType == USER_WX_HELP) {
            loadWebView(WX_AUTH_HELP);
        } else if (protocolType == USER_AUTH_PLATFORM) {//获取平台授权协议
            loadWebView(AUTH_PLATFORM);
        }
    }

    private void initGoogle() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            loadWebView("file:///android_asset/Sunmi_user.html");
        } else if (protocolType == USER_PRIVATE) {
            loadWebView("file:///android_asset/Sunmi_private.html");
        } else if (protocolType == USER_AP_PROTOCOL) { //快速配置路由器协议
            loadWebView("file:///android_asset/Sunmi_user.html");
        } else if (protocolType == USER_AP_PRIVATE) {
            loadWebView("file:///android_asset/Sunmi_private.html");
        } else if (protocolType == USER_WX_HELP) {
            loadWebView(WX_AUTH_HELP);
        } else if (protocolType == USER_AUTH_PLATFORM) {//获取平台授权协议
            loadWebView(AUTH_PLATFORM);
        }
    }

    @Click(resName = "btnImage")
    public void onClick(View v) {
        closeTimer();
        finish();
        this.overridePendingTransition(0, R.anim.activity_close_up_down);
    }

    private void loadLocalHtml() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            loadWebView("file:///android_asset/pro_sunmi.html");
        } else if (protocolType == USER_PRIVATE) {
            loadWebView("file:///android_asset/private_sunmi.html");
        } else if (protocolType == USER_AP_PROTOCOL) { //快速配置路由器协议
            loadWebView("file:///android_asset/pro_sunmi.html");
        } else if (protocolType == USER_AP_PRIVATE) {
            loadWebView("file:///android_asset/private_sunmi.html");
        } else if (protocolType == USER_WX_HELP) {
            loadWebView(AUTH_PLATFORM);
        }
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                /* * 超时后,首先判断页面加载是否小于100,就执行超时后的动作 */
                if (webView.getProgress() < 100) {
                    loadLocalHtml();
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(tt, timeout, 1);
    }

    private void closeTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webView.getSettings().setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                startTimer();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                closeTimer();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                hideLoadingDialog();
                loadLocalHtml();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
        });
    }

}
