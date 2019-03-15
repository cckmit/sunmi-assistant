package com.sunmi.sunmiservice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.PermissionUtils;

import static android.app.Activity.RESULT_OK;

public class WBH5FaceVerifySDK {
    //没有网络连接
    private static final String NETWORK_NONE = "NETWORK_NONE";
    //wifi连接
    private static final String NETWORK_WIFI = "NETWORK_WIFI";
    //手机网络数据连接类型
    private static final String NETWORK_2G = "NETWORK_2G";
    private static final String NETWORK_3G = "NETWORK_3G";
    private static final String NETWORK_4G = "NETWORK_4G";
    private static final String NETWORK_MOBILE = "NETWORK_MOBILE";

    private static final int REQ_TAKE_PHOTO = 0x001081;
    private static final int REQ_VIDEO = 0x001082;

    private static WBH5FaceVerifySDK instance;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private Uri imgUri;

    public static synchronized WBH5FaceVerifySDK getInstance() {
        if (instance == null) {
            synchronized (WBH5FaceVerifySDK.class) {
                if (instance == null) {
                    instance = new WBH5FaceVerifySDK();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化webSettings
     */
    public void setWebViewSettings(WebView mWebView, Context context) {
        if (null == mWebView) return;
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setTextZoom(100);
        String ua = webSetting.getUserAgentString();
        try {
            webSetting.setUserAgentString(ua + ";webank/h5face;webank/1.0" + ";netType:" +
                    getNetWorkState(context) + ";appVersion:" +
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode
                    + ";packageName:" + context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            webSetting.setUserAgentString(ua + ";webank/h5face;webank/1.0");
            e.printStackTrace();
        }
    }

    /**
     * 拍照录像完成回调回来的处理，把文件uri给h5
     */
    public boolean receiveH5FaceVerifyResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TAKE_PHOTO) { //根据请求码判断返回的是否是拍照
            if (null == mUploadMessage && null == mUploadCallbackAboveL) {
                return true;
            }
            if (mUploadCallbackAboveL != null) {
                Uri[] uris = new Uri[]{imgUri};
                mUploadCallbackAboveL.onReceiveValue(uris);
                setmUploadCallbackAboveL(null);
            } else {
                mUploadMessage.onReceiveValue(imgUri);
                setmUploadMessage(null);
            }
            return true;
        } else if (requestCode == REQ_VIDEO) { //根据请求码判断返回的是否是h5刷脸结果
            if (null == mUploadMessage && null == mUploadCallbackAboveL) {
                return true;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            Uri[] uris = result == null ? null : new Uri[]{result};
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(uris);
                setmUploadCallbackAboveL(null);
            } else {
                mUploadMessage.onReceiveValue(result);
                setmUploadMessage(null);
            }
            return true;
        }
        return false;
    }

    public boolean fileChooseForApiBelow21(ValueCallback<Uri> uploadMsg,
                                           String acceptType, Activity activity) {
        if (TextUtils.equals("image/*", acceptType)) {
            setmUploadMessage(uploadMsg);
            openCamera(activity);
            return true;
        } else if (TextUtils.equals("video/webank", acceptType)) {
            setmUploadMessage(uploadMsg);
            recordVideo(activity);
            return true;
        }
        return false;
    }

    @TargetApi(21)
    public boolean fileChooseForApi21(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                      Activity activity, WebChromeClient.FileChooserParams fileChooserParams) {
        if (!PermissionUtils.checkCameraPermission(activity)) return false;
        if (TextUtils.equals("image/*", fileChooserParams.getAcceptTypes()[0])) {
            if ((webView.getUrl().contains(".fbank.com"))) {//上传身份证
                setmUploadCallbackAboveL(filePathCallback);
                openCamera(activity);
                return true;
            }
        } else if ("video/webank".equals(fileChooserParams.getAcceptTypes()[0])) { //是h5刷脸
            setmUploadCallbackAboveL(filePathCallback);
            recordVideo(activity);
            return true;
        }
        return false;
    }

    /**
     * 调用系统摄像头拍照
     */
    private void openCamera(Activity activity) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/fmb_image.jpg");
            if (Build.VERSION.SDK_INT >= 24) {//android7.0及以上
                imgUri = FileProvider.getUriForFile(activity, CommonConstants.FILE_PROVIDER_AUTHORITY, file);
            } else {
                imgUri = Uri.fromFile(file);
            }
            //启动相机程序
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            activity.startActivityForResult(intent, REQ_TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统前置摄像头进行视频录制
     */
    private void recordVideo(Activity activity) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0); //调用前置摄像头 TODO-此处无效
            activity.startActivityForResult(intent, REQ_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setmUploadMessage(ValueCallback<Uri> uploadMessage) {
        mUploadMessage = uploadMessage;
    }

    private void setmUploadCallbackAboveL(ValueCallback<Uri[]> uploadCallbackAboveL) {
        mUploadCallbackAboveL = uploadCallbackAboveL;
    }

    /**
     * 获取当前网络连接类型
     */
    private static String getNetWorkState(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络
        if (null == connManager)
            return NETWORK_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_4G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") ||
                                    strSubTypeName.equalsIgnoreCase("WCDMA") ||
                                    strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORK_3G;
                            } else {
                                return NETWORK_MOBILE;
                            }
                    }
                }
        }
        return NETWORK_NONE;
    }

}
