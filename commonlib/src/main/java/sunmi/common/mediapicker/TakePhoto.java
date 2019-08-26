package sunmi.common.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.commonlibrary.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.mediapicker.picker.PickerActivity;
import sunmi.common.mediapicker.util.ActivityUtils;
import sunmi.common.mediapicker.util.CompressAsyncTask;
import sunmi.common.mediapicker.util.Constant;
import sunmi.common.mediapicker.util.ContextWrap;
import sunmi.common.mediapicker.util.OnCompressListener;
import sunmi.common.mediapicker.util.PermissionManager;
import sunmi.common.mediapicker.util.Utils;


/**
 * 照片选择器主入口
 *
 * @author Jacob
 * @date 17-11-23
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class TakePhoto implements TakePhotoAgent {

    private static final String TAG = "TakePhoto";

    public static final String STATE_CONFIG = "config";

    private ContextWrap mContext;
    private Config mConfig;
    private TakePhotoListener mListener;
    private File mCurrentPhoto;
    private int mFrom;

    private Runnable waitRunnable;
    private PermissionManager.PermissionRequestCallback mPermissionCallback =
            new OnPermissionCallback();
    private OnCompressListener mCompressCallback = new OnCompressCallback();

    private TakePhoto(ContextWrap context, Config config, TakePhotoListener l) {
        this.mContext = context;
        this.mConfig = config;
        this.mListener = l;
    }

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static Builder with(Fragment fragment) {
        return new Builder(fragment);
    }

    @Override
    public void pickSinglePhoto() {
        mFrom = TakePhotoAgent.FROM_SINGLE_PICK;
        int result = PermissionManager.checkPermission(mContext,
                new String[]{Constant.PER_STORAGE});
        if (result == PermissionManager.STATE_REQUEST) {
            waitRunnable = new Runnable() {
                @Override
                public void run() {
                    pickSinglePhoto();
                }
            };
            return;
        }
        // Start activity for pick photos.
        Intent i = new Intent(mContext.getContext(), PickerActivity.class);
        i.putExtra(Constant.INTENT_EXTRA_CONFIG, mConfig);
        i.putExtra(Constant.INTENT_EXTRA_IS_MULTI, false);
        ActivityUtils.startActivityForResult(mContext, i, Constant.REQUEST_PICK_PHOTO);
    }

    @Override
    public void pickMultiPhotos(final Result haveSelected) {
        mFrom = TakePhotoAgent.FROM_MULTI_PICK;
        int result = PermissionManager.checkPermission(mContext,
                new String[]{Constant.PER_STORAGE});
        if (result == PermissionManager.STATE_REQUEST) {
            waitRunnable = new Runnable() {
                @Override
                public void run() {
                    pickMultiPhotos(haveSelected);
                }
            };
            return;
        }
        // Start activity for pick photos.
        Intent i = new Intent(mContext.getContext(), PickerActivity.class);
        i.putExtra(Constant.INTENT_EXTRA_CONFIG, mConfig);
        i.putExtra(Constant.INTENT_EXTRA_IS_MULTI, true);
        i.putExtra(Constant.INTENT_EXTRA_IMAGES, haveSelected);
        ActivityUtils.startActivityForResult(mContext, i, Constant.REQUEST_PICK_PHOTO);
    }

    @Override
    public void takePhoto() {
        mFrom = TakePhotoAgent.FROM_TAKE_PHOTO;
        int result = PermissionManager.checkPermission(mContext,
                new String[]{Constant.PER_STORAGE, Constant.PER_CAMERA});
        if (result == PermissionManager.STATE_REQUEST) {
            waitRunnable = new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            };
            return;
        }
        try {
            mCurrentPhoto = Utils.getNewImage(mContext.getContext(), mConfig.isPhotoSave);
            Utils.takePhoto(mContext, mCurrentPhoto);
        } catch (IOException e) {
            Log.e(TAG, "Create file failed. " + e.getMessage());
            mListener.onError(Constant.ERROR_CODE_CREATE_FAIL, TakePhotoAgent.FROM_TAKE_PHOTO,
                    "Create file failed. " + e.getMessage());
        }
    }

    @Override
    public void setPickLimit(int limit) {
        mConfig.pickLimit = limit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Config c = savedInstanceState.getParcelable(STATE_CONFIG);
            if (c != null) {
                mConfig = c;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_PICK_PHOTO) {
            if (resultCode == Activity.RESULT_CANCELED) {
                mListener.onCancel(mFrom);
                return;
            }
            if (resultCode == Constant.RESULT_ERROR) {
                int code = data.getIntExtra(Constant.INTENT_EXTRA_CODE, -1);
                String msg = data.getStringExtra(Constant.INTENT_EXTRA_MESSAGE);
                mListener.onError(code, mFrom, msg);
                return;
            }
            if (resultCode != Activity.RESULT_OK) {
                mListener.onError(Constant.ERROR_CODE_UNKNOWN, mFrom, "Unknown error.");
                return;
            }
            // From pick photo.
            if (data == null) {
                mListener.onError(22001, mFrom, "No data.");
                return;
            }
            Result result = data.getParcelableExtra(Constant.INTENT_EXTRA_IMAGES);
            if (result == null) {
                mListener.onError(22002, mFrom, "No data.");
                return;
            }
//            mListener.onProcessStart(result.clone());
            if (mConfig.enableCompress) {
                Image[] images = new Image[result.getImages().size()];
                CompressAsyncTask task = new CompressAsyncTask(mContext.getContext(),
                        mCompressCallback);
                task.execute(result.getImages().toArray(images));
            } else {
                mListener.onSuccess(mFrom, result);
            }
        } else if (requestCode == Constant.REQUEST_TAKE_PHOTO) {
            if (resultCode != Activity.RESULT_OK) {
                mListener.onCancel(TakePhotoAgent.FROM_TAKE_PHOTO);
                return;
            }
            // From take photo.
            if (mConfig.isPhotoSave()) {
                Utils.galleryAddPic(mContext.getContext(), Uri.fromFile(mCurrentPhoto));
            }
            Image image = Image.fromPhoto(mCurrentPhoto);
            Result result = new Result(image);
            if (mConfig.isEnableClip()) {
//                Intent intent = new Intent(mContext.getContext(), SinglePreviewActivity.class);
//                intent.putExtra(Constant.INTENT_EXTRA_IMAGE, image);
//                intent.putExtra(Constant.INTENT_EXTRA_CONFIG, mConfig);
//                ActivityUtils.startActivityForResult(mContext, intent,
//                        Constant.REQUEST_TAKE_PHOTO_PREVIEW);
            } else if (mConfig.enableCompress) {
//                mListener.onProcessStart(result.clone());
                CompressAsyncTask task = new CompressAsyncTask(mContext.getContext(),
                        mCompressCallback);
                task.execute(image);
            } else {
//                mListener.onProcessStart(result.clone());
                mListener.onSuccess(TakePhotoAgent.FROM_TAKE_PHOTO, result);
            }
        } else if (requestCode == Constant.REQUEST_TAKE_PHOTO_PREVIEW) {
            if (resultCode != Activity.RESULT_OK) {
                mListener.onCancel(mFrom);
                return;
            }
            // From take photo preview.
            if (data == null) {
                mListener.onError(21011, mFrom, "No data.");
                return;
            }
            Image image = data.getParcelableExtra(Constant.INTENT_EXTRA_IMAGE);
            if (image == null) {
                mListener.onError(21012, mFrom, "No data.");
                return;
            }
            Result result = new Result(image);
//            mListener.onProcessStart(result.clone());
            if (mConfig.enableCompress) {
                CompressAsyncTask task = new CompressAsyncTask(mContext.getContext(),
                        mCompressCallback);
                task.execute(image);
            } else {
                mListener.onSuccess(mFrom, result);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CONFIG, mConfig);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Config parcelable = savedInstanceState.getParcelable(STATE_CONFIG);
            mConfig = parcelable == null ? mConfig : parcelable;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults,
                mPermissionCallback);
    }

    public interface TakePhotoListener {

        /**
         * 成功选取照片
         *
         * @param result 选择结果
         */
        void onSuccess(int from, Result result);

        /**
         * 发生错误的回调
         *
         * @param errorCode 错误码
         * @param msg       错误信息
         */
        void onError(int errorCode, int from, String msg);

        /**
         * 取消选择的回调
         */
        void onCancel(int from);
    }

    public static class Builder {
        private ContextWrap context;
        private TakePhotoListener listener;
        private Config config = new Config();

        Builder(Activity activity) {
            context = new ContextWrap(activity);
        }

        Builder(Fragment fragment) {
            context = new ContextWrap(fragment);
        }

        public Builder setTakePhotoListener(TakePhotoListener l) {
            this.listener = l;
            return this;
        }

        public Builder setThemeId(@StyleRes int themeId) {
            config.themeId = themeId;
            return this;
        }

        public Builder isPhotoSave(boolean isSave) {
            config.isPhotoSave = isSave;
            return this;
        }

        public Builder enableCompress(boolean enableCompress) {
            config.enableCompress = enableCompress;
            return this;
        }

        public Builder enableCaptureInPick(boolean enableCaptureInPick) {
            config.enableCapture = enableCaptureInPick;
            return this;
        }

        public Builder enableCaptureInPickReturn(boolean enableCaptureInPickReturn) {
            config.enableCaptureInPickReturn = enableCaptureInPickReturn;
            return this;
        }

        public Builder enableAlbumSelect(boolean enableAlbumSelect) {
            config.enableAlbumSelect = enableAlbumSelect;
            return this;
        }

        /**
         * Set the maximum number of photos to choose.
         * NOTE: Only work in multi pick mode {@link TakePhoto#pickMultiPhotos(Result)}.
         *
         * @param limit maximum number of photos to choose
         * @return config builder
         * @see TakePhoto#pickMultiPhotos(Result)
         */
        public Builder setPickLimit(int limit) {
            config.pickLimit = limit;
            return this;
        }

        public Builder enableEdit(boolean enableEdit) {
            config.enableEdit = enableEdit;
            return this;
        }

        /**
         * Enable free clip for the picture selected.
         * NOTE: Only work in {@link TakePhoto#pickSinglePhoto()} & {@link TakePhoto#takePhoto()}.
         *
         * @param type Shape of clip for.
         * @return config builder
         * @see #enableClip(int, float)
         * @see TakePhoto#pickSinglePhoto(), TakePhoto#takePhoto()
         */
        public Builder enableClip(int type) {
            return this.enableClip(type, -1);
        }

        /**
         * Enable clip of fixed ratio for the picture selected.
         * NOTE: Only work in {@link TakePhoto#pickSinglePhoto()} & {@link TakePhoto#takePhoto()}.
         *
         * @param type  Shape of clip for.
         * @param ratio Fixed ratio of clip for.
         * @return config builder
         * @see TakePhoto#pickSinglePhoto(), TakePhoto#takePhoto()
         */
        public Builder enableClip(int type, float ratio) {
            config.clipType = type;
            config.clipRatio = ratio;
            return this;
        }

        public TakePhotoAgent build() {
            return new TakePhoto(context, config, listener);
        }
    }

    private class OnPermissionCallback implements PermissionManager.PermissionRequestCallback {
        @Override
        public void onPermissionGranted() {
            if (waitRunnable != null) {
                waitRunnable.run();
            }
        }

        @Override
        public void onPermissionDenied(List<String> permissions) {
            Log.d(TAG, "Permission denied!");
            Toast.makeText(mContext.getContext(), mContext.getContext()
                    .getString(R.string.picker_toast_no_permission), Toast.LENGTH_SHORT).show();
            for (String per : permissions) {
                Log.d(TAG, "Permission: " + per);
            }
            mListener.onError(10002, mFrom, "Permission denied!");
        }
    }

    private class OnCompressCallback implements OnCompressListener {

        @Override
        public void onStart() {
            Log.d(TAG, "Compress start.");
        }

        @Override
        public void onSuccess(List<Image> images) {
            Log.d(TAG, "Compress SUCCESS.");
            mListener.onSuccess(mFrom, new Result(images));
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Image compress error: " + e.getLocalizedMessage());
            e.printStackTrace();
            mListener.onError(10001, mFrom, "Image compress error:"
                    + e.getLocalizedMessage());
        }

    }
}
