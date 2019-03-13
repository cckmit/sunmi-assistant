package sunmi.common.view.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Message;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.commonlibrary.R;

import sunmi.common.utils.log.LogCat;

public abstract class SMWebViewClient extends WebViewClient {

    public Activity mContext;

//    private JumpUtil jumpUtil;

    public SMWebViewClient(Activity context) {
        super();
        this.mContext = context;
    }

//    public SMWebViewClient(Activity context, JumpUtil jumpUtil) {
//        super();
//        this.mContext = context;
//        this.jumpUtil = jumpUtil;
//        if (null == this.jumpUtil) {
//            this.jumpUtil = new JumpUtil(mContext);
//        }
//    }

    /**
     * (1) 当请求的方式是"POST"方式时这个回调是不会通知的。
     * (2) 当我们访问的地址需要我们应用程序自己处理的时候，可以在这里截获，比如我们发现跳转到的是一个market的链接，那么我们可以直接跳转到应用市场，或者其他app。
     *
     * @param view 接收WebViewClient的那个实例，前面看到webView.setWebViewClient(new MyAndroidWebViewClient())，即是这个webview。
     * @param url  即将要被加载的url
     * @return true 当前应用程序要自己处理这个url， 返回false则不处理。
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //默认用true,防止ERR_UNKNOW_URL_SCHEME错误
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                   SslError error) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.str_ssl_error);
        builder.setPositiveButton(mContext.getString(R.string.str_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
        builder.setNegativeButton(mContext.getString(R.string.str_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * goBack时重新发送POST数据
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        resend.sendToTarget();
    }

    /**
     * 加载异常
     *
     * @param view
     * @param request
     * @param error
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        receiverError(view, request, error);
    }

    protected abstract void receiverError(WebView view, WebResourceRequest request, WebResourceError error);
}