package com.sunmi.sunmiservice;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.webview.SMWebChromeClient;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;
import sunmi.common.view.webview.SsConstants;

@EActivity(resName = "activity_webview")
public class WebViewActivity extends BaseActivity
        implements SMWebChromeClient.Callback, View.OnClickListener {

    private final int timeout = 15_000;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "main_web_view")
    SMWebView webView;
    @ViewById(resName = "pb_web_view")
    ProgressBar pbWebView;
    @ViewById(resName = "layout_network_error")
    View networkError;

    @Extra
    String url;

    private CountDownTimer countDownTimer;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        titleBar.getLeftImg().setOnClickListener(v -> onBackPressed());
        initWebView();
        webView.loadUrl(url);
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(timeout, timeout) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    loadError();
                }
            };
        }
        countDownTimer.start();
    }

    private void closeTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void initWebView() {
        JSCall jsCall = new JSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        SMWebChromeClient smWebChromeClient = new SMWebChromeClient(this);
        smWebChromeClient.setCallback(this);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";sunmi");
        webView.setWebChromeClient(smWebChromeClient);
        webView.setWebViewClient(new SMWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    } catch (Exception e) {
                        new CommonDialog.Builder(context)
                                .setTitle(R.string.dialog_title_install_alipay)
                                .setConfirmButton(R.string.str_install_now, (dialog, which) -> {
                                    Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                    startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                }).setCancelButton(R.string.sm_cancel).create().show();
                    }

                    return true;
                }
                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }

                view.loadUrl(url);
                return true;
            }

            @Override
            protected void receiverError(final WebView view, WebResourceRequest request, WebResourceError error) {
               loadError();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        networkError.setVisibility(View.GONE);
        pbWebView.setVisibility(View.VISIBLE);
        titleBar.getAppTitle().setVisibility(View.VISIBLE);
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.clearCache(true);
            webView.clearHistory();
            webView.reload();
        }
        startTimer();
    }

    @UiThread
    protected void loadError() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }
        networkError.setVisibility(View.VISIBLE);
        pbWebView.setVisibility(View.GONE);
        titleBar.getAppTitle().setVisibility(View.GONE);
        closeTimer();
    }

    @Override
    public void onClick(View v) {
        share();
    }

    @Override
    public void onProgressChanged(int progress) {
        pbWebView.setProgress(progress);
        if (progress == 100) {
            pbWebView.setVisibility(View.GONE);
        } else {
            pbWebView.setVisibility(View.VISIBLE);
            closeTimer();
        }
    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    public void onReceivedTitle(String title) {
        titleBar.setAppTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (webView == null) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    //销毁Webview 防止内存溢出
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private void share() {
        new ShareAction(this)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .addButton(getString(R.string.str_copy_link), "copy_link",
                        "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (share_media == null) {//根据key来区分自定义按钮的类型，并进行对应的操作
                            if (snsPlatform.mKeyword.equals("copy_link")) {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText(webView.getUrl());
                                shortTip(getString(R.string.tip_copy_success) + cm.getText());
                            }
                        } else {//社交平台的分享行为
                            final UMWeb web = new UMWeb(webView.getUrl());
                            web.setTitle(getString(R.string.title_share_page));//标题
                            UMImage thumb = new UMImage(context, R.drawable.icon_support_share);//资源文件
                            web.setThumb(thumb);  //缩略图
                            web.setDescription(getString(R.string.description_share_page));//描述
                            new ShareAction(WebViewActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(new UMShareListener() {
                                        @Override
                                        public void onStart(SHARE_MEDIA share_media) {
                                            LogCat.e(TAG, "ShareAction onStart");
                                        }

                                        @Override
                                        public void onResult(SHARE_MEDIA share_media) {
                                            LogCat.e(TAG, "ShareAction onResult");
                                        }

                                        @Override
                                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                            LogCat.e(TAG, "ShareAction onError");
                                        }

                                        @Override
                                        public void onCancel(SHARE_MEDIA share_media) {
                                            LogCat.e(TAG, "ShareAction onCancel");
                                        }
                                    }).share();
                        }
                    }
                }).open(getShareBoardConfig());
    }

    @NonNull
    private ShareBoardConfig getShareBoardConfig() {
        ShareBoardConfig config = new ShareBoardConfig();//新建ShareBoardConfig
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        config.setTitleText(getString(R.string.str_share_to));
        config.setTitleTextColor(getResources().getColor(R.color.text_main));
        config.setCancelButtonTextColor(getResources().getColor(R.color.text_normal));
        config.setMenuItemTextColor(getResources().getColor(R.color.text_normal));
        config.setShareboardBackgroundColor(getResources().getColor(R.color.c_white));
        config.setIndicatorVisibility(false);
        config.setCancelButtonText(getString(R.string.sm_cancel));
        return config;
    }

}
