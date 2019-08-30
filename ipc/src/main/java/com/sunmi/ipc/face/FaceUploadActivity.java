package com.sunmi.ipc.face;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceUploadContract;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.model.UploadImage;
import com.sunmi.ipc.face.presenter.FaceUploadPresenter;
import com.sunmi.ipc.face.util.Constants;
import com.sunmi.ipc.face.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseRecyclerAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.base.recycle.listener.OnViewClickListener;
import sunmi.common.mediapicker.TakePhoto;
import sunmi.common.mediapicker.TakePhotoAgent;
import sunmi.common.mediapicker.data.model.Image;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @date 2019-08-27
 */
@EActivity(resName = "face_activity_upload_list")
public class FaceUploadActivity extends BaseMvpActivity<FaceUploadPresenter>
        implements FaceUploadContract.View {

    private static final String STATE_IMAGES = "state_images";

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "rv_face_list")
    RecyclerView mRvFaceList;

    @Extra
    int mShopId;
    @Extra
    FaceGroup mFaceGroup;
    @Extra
    ArrayList<UploadImage> mImages;
    @Extra
    int mRemain;

    private List<UploadImage> mValidImages = new ArrayList<>();
    private List<UploadImage> mInvalidImages = new ArrayList<>();
    private int mUploadCount;
    private int mCompleteCount;

    private BottomPopMenu mPickerDialog;
    private Dialog mRetryDialog;
    private ImageAdapter mAdapter;
    private TakePhotoAgent mPickerAgent;
    private int mPickerLimit;

    @AfterViews
    void init() {
        initTitle();
        initRecycler();
        mPickerLimit = Math.min(mRemain, Constants.IMAGE_PICKER_LIMIT);
        mPickerAgent = TakePhoto.with(this)
                .setTakePhotoListener(new PickerResult())
                .build();
        mPresenter = new FaceUploadPresenter(mShopId, mFaceGroup);
        mPresenter.attachView(this);
    }

    private void initTitle() {
        mTitleBar.getRightText().setEnabled(true);
        mTitleBar.setRightTextViewColor(R.color.colorText);
        mTitleBar.setRightTextViewText(R.string.ipc_face_upload_btn);
        mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void initRecycler() {
        mAdapter = new ImageAdapter(mImages);
        mAdapter.add(0, new UploadImage());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRvFaceList.setLayoutManager(layoutManager);
        mRvFaceList.addItemDecoration(new ImageItemDecoration());
        mRvFaceList.setAdapter(mAdapter);
    }

    private void takePhoto() {
        mPickerAgent.takePhoto();
    }

    private void pickImage() {
        List<Image> result = new ArrayList<>(mImages.size());
        for (UploadImage image : mImages) {
            result.add(Image.fromPath(image.getFile()));
        }
        mPickerAgent.setPickLimit(mPickerLimit);
        mPickerAgent.pickMultiPhotos(new Result(result));
    }

    private void upload() {
        mTitleBar.getRightText().setEnabled(false);
        mTitleBar.setRightTextViewColor(R.color.colorText_40);
        for (UploadImage image : mImages) {
            image.setState(UploadImage.STATE_UPLOADING);
        }
        List<UploadImage> data = mAdapter.getData();
        if (data.size() > 0 && data.get(0).getState() == UploadImage.STATE_ADD) {
            data.remove(0);
        }
        mAdapter.notifyDataSetChanged();
        mUploadCount = mImages.size();
        mCompleteCount = 0;
        for (UploadImage image : mImages) {
            compress(image);
        }
    }

    private void save() {
        showLoadingDialog();
        mPresenter.save(mValidImages);
    }

    private void resetView() {
        initTitle();
        mImages.clear();
        mValidImages.clear();
        mInvalidImages.clear();
        mAdapter.clear();
        mAdapter.add(new UploadImage());
    }

    private void uploadComplete() {
        mTitleBar.setRightTextViewText(R.string.ipc_setting_save);
        if (mValidImages.isEmpty()) {
            boolean networkFailed = false;
            for (UploadImage image : mInvalidImages) {
                if (image.getState() == UploadImage.STATE_FAILED_NET) {
                    networkFailed = true;
                    break;
                }
            }
            mTitleBar.getRightText().setEnabled(false);
            mTitleBar.setRightTextViewColor(R.color.colorText_40);
            if (mRetryDialog == null) {
                mRetryDialog = new CommonDialog.Builder(this)
                        .setTitle(R.string.ipc_face_error_upload)
                        .setMessage(networkFailed ? R.string.ipc_face_error_image_all_network
                                : R.string.ipc_face_error_image_all)
                        .setConfirmButton(R.string.ipc_face_pick_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetView();
                                pickImage();
                            }
                        })
                        .create();
            }
            mRetryDialog.show();
        } else {
            mTitleBar.getRightText().setEnabled(true);
            mTitleBar.setRightTextViewColor(R.color.colorText);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
            shortTip(R.string.ipc_face_tip_album_upload_complete);
        }
    }

    @Background
    void compress(UploadImage image) {
        File file = Utils.imageCompress(this, new File(image.getFile()));
        if (file == null) {
            LogCat.e(TAG, "Compress face image Failed.");
            uploadFailed(image);
        } else {
            image.setCompressed(file.getPath());
            mPresenter.upload(image);
        }
    }

    @Override
    public void saveComplete(int count) {
        hideLoadingDialog();
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void saveFailed() {
        hideLoadingDialog();
        shortTip(R.string.toast_network_error);
    }

    @Override
    public void uploadSuccess(UploadImage image) {
        mValidImages.add(image);
        image.setState(UploadImage.STATE_SUCCESS);
        mAdapter.notifyDataSetChanged();
        mCompleteCount++;
        if (mCompleteCount >= mUploadCount) {
            uploadComplete();
        }
    }

    @UiThread
    @Override
    public void uploadFailed(UploadImage image) {
        mInvalidImages.add(image);
        mAdapter.notifyDataSetChanged();
        mCompleteCount++;
        if (mCompleteCount >= mUploadCount) {
            uploadComplete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPickerAgent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPickerAgent.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPickerAgent.onSaveInstanceState(outState);
        ArrayList<UploadImage> list = new ArrayList<>(mAdapter.getData());
        outState.putParcelableArrayList(STATE_IMAGES, list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerAgent.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<UploadImage> list = savedInstanceState.getParcelableArrayList(STATE_IMAGES);
            if (list != null) {
                mAdapter.setData(list);
                mImages = new ArrayList<>(list);
                if (list.size() > 0 && list.get(0).getState() == UploadImage.STATE_ADD) {
                    mImages.remove(0);
                }
            }
        }
    }

    private class PickerResult implements TakePhoto.TakePhotoListener {
        @Override
        public void onSuccess(int from, Result result) {
            List<Image> images = result.getImages();
            List<UploadImage> addList = new ArrayList<>();
            for (Image image : images) {
                UploadImage item = new UploadImage(image);
                if (!mImages.contains(item)) {
                    addList.add(item);
                }
            }
            if (mImages.size() + addList.size() >= Constants.IMAGE_PICKER_LIMIT) {
                mAdapter.remove(0);
            }
            mAdapter.add(addList);
            mImages.addAll(addList);
        }

        @Override
        public void onError(int errorCode, int from, String msg) {
        }

        @Override
        public void onCancel(int from) {
        }
    }

    private class ImageAdapter extends SimpleArrayAdapter<UploadImage> {

        public ImageAdapter(List<UploadImage> data) {
            super(data);
            addOnViewClickListener(R.id.iv_face_item_image, new OnViewClickListener<UploadImage>() {
                @Override
                public void onClick(BaseRecyclerAdapter<UploadImage> adapter, BaseViewHolder<UploadImage> holder,
                                    View v, UploadImage model, int position) {
                    if (model.getState() != UploadImage.STATE_ADD) {
                        return;
                    }
                    if (mImages.size() >= mPickerLimit) {
                        shortTip(R.string.ipc_face_error_select_limit);
                        return;
                    }
                    if (mPickerDialog == null) {
                        mPickerDialog = new BottomPopMenu.Builder(FaceUploadActivity.this)
                                .addItemAction(new PopItemAction(R.string.ipc_face_take_photo,
                                        PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                                    @Override
                                    public void onClick() {
                                        takePhoto();
                                    }
                                }))
                                .addItemAction(new PopItemAction(R.string.ipc_face_album_choose,
                                        PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                                    @Override
                                    public void onClick() {
                                        pickImage();
                                    }
                                }))
                                .addItemAction(new PopItemAction(R.string.sm_cancel,
                                        PopItemAction.PopItemStyle.Cancel))
                                .create();
                    }
                    mPickerDialog.show();
                }
            });
            addOnViewClickListener(R.id.v_face_item_region, new OnViewClickListener<UploadImage>() {
                @Override
                public void onClick(BaseRecyclerAdapter<UploadImage> adapter, BaseViewHolder<UploadImage> holder,
                                    View v, UploadImage model, int position) {
                    boolean isAtLimit = mImages.size() >= Constants.IMAGE_PICKER_LIMIT;
                    remove(position);
                    if (isAtLimit) {
                        mAdapter.add(0, new UploadImage());
                    }
                    mImages.remove(model);
                }
            });
        }

        @Override
        public int getLayoutId() {
            return R.layout.face_item_upload_image;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<UploadImage> holder, UploadImage model, int position) {
            updateVisible(holder, model.getState());
            ImageView image = holder.getView(R.id.iv_face_item_image);
            TextView tip = holder.getView(R.id.tv_face_item_tip);
            if (model.getState() == UploadImage.STATE_ADD) {
                image.setActivated(mImages.size() < mPickerLimit);
                image.setScaleType(ImageView.ScaleType.CENTER);
                image.setImageResource(R.drawable.face_ic_add);
            } else {
                image.setActivated(true);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(holder.getContext()).load(model.getFile()).into(image);
            }
            if (model.getState() == UploadImage.STATE_FAILED_NET) {
                tip.setText(R.string.ipc_face_error_photo_network);
            } else {
                tip.setText(R.string.ipc_face_error_photo);
            }

        }

        private void updateVisible(@NonNull BaseViewHolder<UploadImage> holder, int state) {
            ImageView delete = holder.getView(R.id.iv_face_item_delete);
            View region = holder.getView(R.id.v_face_item_region);
            TextView tip = holder.getView(R.id.tv_face_item_tip);
            ProgressBar loading = holder.getView(R.id.pb_face_item_loading);
            switch (state) {
                case UploadImage.STATE_INIT:
                    delete.setVisibility(View.VISIBLE);
                    region.setVisibility(View.VISIBLE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    break;
                case UploadImage.STATE_UPLOADING:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    break;
                case UploadImage.STATE_SUCCESS:
                case UploadImage.STATE_ADD:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    break;
                case UploadImage.STATE_FAILED:
                case UploadImage.STATE_FAILED_NET:
                    delete.setVisibility(View.GONE);
                    region.setVisibility(View.GONE);
                    tip.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    break;
                default:
            }
        }
    }

    private static class ImageItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.set(4, 4, 4, 4);
        }
    }

}
