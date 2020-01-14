package sunmi.common.view.webview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.commonlibrary.R;

import java.io.File;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * Class  Name: SMWebChromeClient
 * Description: 自定义实现webView选择本地图片
 * Created by Bruce on 18/11/27
 */
public class SMWebChromeClient extends WebChromeClient {
    private static final int REQUEST_GALLERY = 0xa0;
    private static final int REQUEST_CAMERA = 0xa1;
    private ValueCallback<Uri> filePathCallback;
    private ValueCallback<Uri[]> filePathCallbacks;
    private BaseActivity mActivity;

    private boolean mIsInjectedJS;
    private Callback callback;
    private JsCallJava mJsCallJava;
    private BottomPopMenu choosePhotoMenu;
    private Uri imgUri;

    public SMWebChromeClient(BaseActivity activity) {
        mActivity = activity;
    }

    public SMWebChromeClient(BaseActivity activity, String injectedName, Class injectedCls) {
        this.mActivity = activity;
        mJsCallJava = new JsCallJava(injectedName, injectedCls);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (callback != null) callback.onReceivedTitle(title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (callback != null) {
            callback.onProgressChanged(newProgress);
            if (newProgress == 100)
                callback.onProgressComplete();
        }

        //为什么要在这里注入JS
        //1 OnPageStarted中注入有可能全局注入不成功，导致页面脚本上所有接口任何时候都不可用
        //2 OnPageFinished中注入，虽然最后都会全局注入成功，但是完成时间有可能太晚，当页面在初始化调用接口函数时会等待时间过长
        //3 在进度变化时注入，刚好可以在上面两个问题中得到一个折中处理
        //为什么是进度大于25%才进行注入，因为从测试看来只有进度大于这个数字页面才真正得到框架刷新加载，保证100%注入成功
        if (newProgress <= 25) {
            mIsInjectedJS = false;
        } else if (!mIsInjectedJS) {
            if (view instanceof SMWebView) {
                SMWebView smWebView = (SMWebView) view;
                smWebView.injectJavascriptInterfaces();
            }
            if (mJsCallJava != null) {
                view.loadUrl(mJsCallJava.getPreloadInterfaceJS());
            }
            mIsInjectedJS = true;
        }
        super.onProgressChanged(view, newProgress);
    }

    // js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
    // Android > 4.1.1 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if (filePathCallback != null)
            return;
        filePathCallback = uploadMsg;
        selectImage();
    }

    // 3.0 + 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        filePathCallback = uploadMsg;
        selectImage();
    }

    // Android < 3.0 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        filePathCallback = uploadMsg;
        selectImage();
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        filePathCallbacks = filePathCallback;
        selectImage();
        return true;
    }

    private void selectImage() {
        if (choosePhotoMenu == null) {
            choosePhotoMenu = new BottomPopMenu.Builder(mActivity)
                    .addItemAction(new PopItemAction(R.string.str_take_photo,
                            PopItemAction.PopItemStyle.Normal, this::takePhoto))
                    .addItemAction(new PopItemAction(R.string.str_choose_from_album,
                            PopItemAction.PopItemStyle.Normal, this::openGallery))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel, this::cancelCallback))
                    .create();
        }
        choosePhotoMenu.show();
    }

    private void takePhoto() {
        if (!PermissionUtils.checkSDCardCameraPermission(mActivity)) {
            return;
        }

        File fileUri = new File(FileHelper.SDCARD_CACHE_IMAGE_PATH + "/web_image.jpg");
        imgUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            imgUri = FileProvider.getUriForFile(mActivity,
                    CommonConstants.FILE_PROVIDER_AUTHORITY, fileUri);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        mActivity.startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        if (!PermissionUtils.checkStoragePermission(mActivity)) {
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    /**
     * 所有需要上传图片的地方必须在对应的onActivityResult实现此方法
     */
    public void uploadImage(int requestCode, int resultCode, Intent data) {
        Uri result = null;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    result = imgUri;
                    break;
                case REQUEST_GALLERY:
                    result = data == null ? null : data.getData();
                    break;
                default:
                    break;
            }
        }
        if (filePathCallback != null) {
            if (result != null) {
                filePathCallback.onReceiveValue(result);
            } else {
                filePathCallback.onReceiveValue(null);
            }
        }
        if (filePathCallbacks != null) {
            if (result != null) {
                filePathCallbacks.onReceiveValue(new Uri[]{result});
            } else {
                filePathCallbacks.onReceiveValue(null);
            }
        }

        filePathCallback = null;
        filePathCallbacks = null;
    }

    public void onPermissionResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQ_PERMISSIONS_CAMERA_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    mActivity.shortTip(R.string.str_please_open_camera);
                }
                break;
            case PermissionUtils.REQ_PERMISSIONS_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }
                break;
            default:
        }
    }

    /**
     * 防止点击dialog的取消按钮之后，就不再次响应点击事件了
     */
    public void cancelCallback() {
        if (filePathCallback != null) {
            filePathCallback.onReceiveValue(null);
        }

        if (filePathCallbacks != null) {
            filePathCallbacks.onReceiveValue(null);
        }
    }

    public interface Callback {

        void onProgressChanged(int progress);

        void onProgressComplete();

        void onReceivedTitle(String title);
    }

}
