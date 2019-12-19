package com.sunmi.assistant.mine;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.UserInfoContract;
import com.sunmi.assistant.mine.presenter.UserInfoPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.PhotoUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.StringHelper;
import sunmi.common.view.CircleImage;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * Description:
 *
 * @author bruce
 * @date 2019/1/24
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

    private String localAvatarPath;
    private Uri localPhotoUri;

    private BottomPopMenu choosePhotoMenu;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        initViews();
        mPresenter = new UserInfoPresenter();
        mPresenter.attachView(this);
        localAvatarPath = FileHelper.SDCARD_CACHE_IMAGE_PATH + SpUtils.getUID() + "_avatar.jpg";
    }

    @UiThread
    void initViews() {
        initAvatar(false);
        String nickname = SpUtils.getUsername();
        if (!TextUtils.isEmpty(nickname)) {
            silNickname.setEndContent(nickname);
        }
        String mobile = SpUtils.getMobile();
        if (!TextUtils.isEmpty(mobile)) {
            silPhone.setVisibility(View.VISIBLE);
            silPhone.setEndContent(StringHelper.getEncryptPhone(mobile));
        }
        String email = SpUtils.getEmail();
        if (!TextUtils.isEmpty(email) && email.contains("@")) {
            silEmail.setVisibility(View.VISIBLE);
            silEmail.setEndContent(StringHelper.getEncryptEmail(email));
        }
    }

    @UiThread
    void initAvatar(boolean forceRefresh) {
        ImageUtils.loadImage(context, SpUtils.getAvatarUrl(),
                civAvatar, forceRefresh, R.mipmap.default_avatar);
    }

    @Click(R.id.rl_avatar)
    void avatarClick() {
        chooseImage();
    }

    @Click(R.id.sil_nickname)
    void nicknameClick() {
        ChangeUsernameActivity_.intent(context).start();
    }

    @Override
    public void updateAvatarView(String url) {
        shortTip(R.string.tip_set_complete);
        SpUtils.setAvatarUrl(url);
        initAvatar(true);
        BaseNotification.newInstance().postNotificationName(
                NotificationConstant.updateAvatarSuccess, url);
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
            case PermissionUtils.REQ_PERMISSIONS_CAMERA_STORAGE:
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
            case PermissionUtils.REQ_PERMISSIONS_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, REQUEST_GALLERY);
                } else {
                    shortTip(getString(R.string.str_please_open_sd));
                }
                break;
            default:
        }
    }

    private void chooseImage() {
        if (choosePhotoMenu == null) {
            choosePhotoMenu = new BottomPopMenu.Builder(this)
                    .addItemAction(new PopItemAction(R.string.str_take_photo,
                            PopItemAction.PopItemStyle.Normal, this::takePhoto))
                    .addItemAction(new PopItemAction(R.string.str_choose_from_album,
                            PopItemAction.PopItemStyle.Normal, this::openGallery))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        }
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
        localPhotoUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            localPhotoUri = FileProvider.getUriForFile(UserInfoActivity.this,
                    CommonConstants.FILE_PROVIDER_AUTHORITY, fileUri);
        }
        PhotoUtils.takePicture(this, localPhotoUri, REQUEST_CAMERA);
    }

    private void openGallery() {
        if (!PermissionUtils.checkStoragePermission(UserInfoActivity.this)) {
            return;
        }
        PhotoUtils.openPic(this, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File imageFile = new File(localAvatarPath);
            switch (requestCode) {
                case REQUEST_CAMERA:
                    Uri cropImageUri = Uri.fromFile(imageFile);
                    PhotoUtils.cropImageUri(this, localPhotoUri, cropImageUri, 1, 1,
                            IMAGE_DIMENSION, IMAGE_DIMENSION, REQUEST_CROP_RESULT);
                    break;
                case REQUEST_GALLERY:
                    if (FileUtils.isSDExist()) {
                        cropImageUri = Uri.fromFile(imageFile);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(context, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(context,
                                    CommonConstants.FILE_PROVIDER_AUTHORITY, new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1,
                                IMAGE_DIMENSION, IMAGE_DIMENSION, REQUEST_CROP_RESULT);
                    } else {
                        shortTip(R.string.str_no_sd);
                    }
                    break;
                case REQUEST_CROP_RESULT:
                    mPresenter.updateAvatar(imageFile);
                    break;
                default:
            }
        }
    }

}
