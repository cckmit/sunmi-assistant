package com.sunmi.assistant.ui.activity.setting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.contract.UserInfoContract;
import com.sunmi.apmanager.model.UserInfo;
import com.sunmi.apmanager.presenter.UserInfoPresenter;
import com.sunmi.apmanager.utils.FileHelper;
import com.sunmi.apmanager.utils.PhotoUtils;
import sunmi.common.utils.SpUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.StringHelper;
import sunmi.common.view.CircleImage;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * Description:
 * Created by bruce on 2019/1/24.
 */
@EActivity(R.layout.activity_user_info)
public class UserInfoActivity extends BaseMvpActivity<UserInfoPresenter>
        implements UserInfoContract.View {

    @ViewById(R.id.ci_avatar)
    CircleImage civAvatar;
    @ViewById(R.id.sil_nickname)
    SettingItemLayout silNickname;
    @ViewById(R.id.sil_phone)
    SettingItemLayout silPhone;
    @ViewById(R.id.sil_email)
    SettingItemLayout silEmail;

    private static final int REQUEST_GALLERY = 0xa0;
    private static final int REQUEST_CAMERA = 0xa1;
    private static final int REQUEST_CROP_RESULT = 0xa2;
    private static final int IMAGE_DIMENSION = 480;

    private String cropPath;//裁剪后图片的本地地址
    private Uri imageUri;
    private BottomPopMenu choosePhotoMenu;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        cropPath = FileHelper.SDCARD_CACHE_IMAGE_PATH + SpUtils.getUID() + "_avatar.jpg";
        initViews();
        mPresenter = new UserInfoPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserInfo();
    }

    @UiThread
    void initViews() {
        initAvatar(false);
        String nickname = SpUtils.getUsername();
        if (!TextUtils.isEmpty(nickname)) {
            silNickname.setRightText(nickname);
        }
        String mobile = SpUtils.getMobile();
        if (!TextUtils.isEmpty(mobile)) {
            silPhone.setVisibility(View.VISIBLE);
            silPhone.setRightText(StringHelper.getEncryptPhone(mobile));
        }
        String email = SpUtils.getEmail();
        if (!TextUtils.isEmpty(email) && email.contains("@")) {
            silEmail.setVisibility(View.VISIBLE);
            silEmail.setRightText(StringHelper.getEncryptEmail(email));
        }
    }

    @UiThread
    void initAvatar(boolean forceRefresh) {
        ImageUtils.loadImage(context, SpUtils.getAvatarUrl(),
                civAvatar, forceRefresh, R.mipmap.default_avatar);
    }

    @Click(R.id.rl_avatar)
    void avatarClick() {
        showChoosePhoto();
    }

    @Click(R.id.sil_nickname)
    void nicknameClick() {
        ChangeUsernameActivity_.intent(context).start();
    }

    @Override
    public void updateSuccess(String bean) {
        shortTip(R.string.tip_set_complete);
        try {
            JSONObject jsonObject = new JSONObject(bean);
            if (jsonObject.has("origin_icon")) {
                String imgUrl = jsonObject.getString("origin_icon");
                SpUtils.setAvatarUrl(imgUrl);
                initAvatar(true);
                BaseNotification.newInstance().postNotificationName(
                        NotificationConstant.updateAvatarSuccess, imgUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFail(int code, String msg) {
        shortTip(R.string.tip_set_fail);
    }

    @Override
    public void getUserInfoSuccess(UserInfo bean) {
        if (!TextUtils.equals(SpUtils.getUsername(), bean.getUsername())) {
            BaseNotification.newInstance().postNotificationName(
                    NotificationConstant.updateUsernameSuccess, bean.getUsername());
        }
        if (!TextUtils.equals(SpUtils.getAvatarUrl(), bean.getOrigin_icon())) {
            BaseNotification.newInstance().postNotificationName(
                    NotificationConstant.updateAvatarSuccess, bean.getOrigin_icon());
        }
        mPresenter.saveUserInfo(bean);
        initViews();
    }

    @Override
    public void getUserInfoFail(int code, String msg) {

    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.updateUsernameSuccess};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationConstant.updateUsernameSuccess) {
            initViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQ_PERMISSIONS_CAMERA_STORAGE: //调用系统相机申请拍照权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (FileUtils.isSDExist()) {
                        takePhoto();
                    } else {
                        shortTip(getString(R.string.str_no_sd));
                    }
                } else {
                    shortTip(getString(R.string.str_please_open_camera));
                }
                break;
            case PermissionUtils.REQ_PERMISSIONS_STORAGE: //调用系统相册申请Sdcard权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, REQUEST_GALLERY);
                } else {
                    shortTip(getString(R.string.str_please_open_sd));
                }
                break;
            default:
        }
    }

    void showChoosePhoto() {
        if (choosePhotoMenu == null)
            choosePhotoMenu = new BottomPopMenu.Builder(this)
                    .addItemAction(new PopItemAction(R.string.str_take_photo,
                            PopItemAction.PopItemStyle.Normal,
                            new PopItemAction.OnClickListener() {
                                @Override
                                public void onClick() {
                                    takePhoto();
                                }
                            }))
                    .addItemAction(new PopItemAction(R.string.str_choose_from_album,
                            PopItemAction.PopItemStyle.Normal,
                            new PopItemAction.OnClickListener() {
                                @Override
                                public void onClick() {
                                    openAlbum();
                                }
                            }))
                    .addItemAction(new PopItemAction(R.string.str_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        choosePhotoMenu.show();
    }

    private void takePhoto() {
        if (!PermissionUtils.checkSDCardCameraPermission(UserInfoActivity.this)) {
            return;
        }
        if (!FileUtils.isSDExist()) {
            shortTip(R.string.str_no_sd);
            return;
        }
        File fileUri = new File(FileHelper.SDCARD_CACHE_IMAGE_PATH + "avatar.jpg");
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            imageUri = FileProvider.getUriForFile(UserInfoActivity.this,
                    CommonConstants.FILE_PROVIDER_AUTHORITY, fileUri);//通过FileProvider创建一个content类型的Uri
        PhotoUtils.takePicture(this, imageUri, REQUEST_CAMERA);
    }

    private void openAlbum() {
        if (!PermissionUtils.checkStoragePermission(UserInfoActivity.this)) {
            return;
        }
        PhotoUtils.openPic(this, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File fileCropUri = new File(cropPath);
            switch (requestCode) {
                case REQUEST_CAMERA://拍照完成回调
                    Uri cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1,
                            IMAGE_DIMENSION, IMAGE_DIMENSION, REQUEST_CROP_RESULT);
                    break;
                case REQUEST_GALLERY://访问相册完成回调
                    if (FileUtils.isSDExist()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this,
                                    CommonConstants.FILE_PROVIDER_AUTHORITY, new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1,
                                IMAGE_DIMENSION, IMAGE_DIMENSION, REQUEST_CROP_RESULT);
                    } else {
                        shortTip(R.string.str_no_sd);
                    }
                    break;
                //裁剪后图片回调
                case REQUEST_CROP_RESULT:
                    mPresenter.updateIcon("avatar.png", fileCropUri);
                    break;
                default:
            }
        }
    }

}
