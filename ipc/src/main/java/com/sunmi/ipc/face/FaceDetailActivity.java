package com.sunmi.ipc.face;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceDetailContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceDetailPresenter;
import com.sunmi.ipc.face.util.GlideRoundCrop;
import com.sunmi.ipc.face.util.Utils;
import com.sunmi.ipc.model.FaceAgeRangeResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.mediapicker.TakePhoto;
import sunmi.common.mediapicker.TakePhotoAgent;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

import static sunmi.common.utils.DateTimeUtils.secondToDate;


/**
 * @author yangShiJie
 * @date 2019/8/27
 */
@EActivity(resName = "face_activity_photo_detail")
public class FaceDetailActivity extends BaseMvpActivity<FaceDetailPresenter>
        implements FaceDetailContract.View {
    private static final String DATE_FORMAT_REGISTER = "yyyy/MM/dd";
    public static final String DATE_FORMAT_ENTER_SHOP = "yyyy/MM/dd  HH:mm";
    private static final int IPC_NAME_MAX_LENGTH = 36;
    private static final int UPDATE_INDEX_ID = 0;
    private static final int UPDATE_INDEX_GENDER = 1;
    private static final int UPDATE_INDEX_AGE = 2;

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "iv_face_image")
    ImageView ivFaceImage;
    @ViewById(resName = "sil_face_name")
    SettingItemLayout silFaceName;
    @ViewById(resName = "sil_face_id")
    SettingItemLayout silFaceId;
    @ViewById(resName = "sil_face_sex")
    SettingItemLayout silFaceSex;
    @ViewById(resName = "sil_face_age")
    SettingItemLayout silFaceAge;
    @ViewById(resName = "sil_face_enter_shop_num")
    SettingItemLayout silFaceEnterShopNum;
    @ViewById(resName = "sil_face_register_time")
    SettingItemLayout silFaceRegisterTime;
    @ViewById(resName = "sil_face_new_enter_shop_time")
    SettingItemLayout silFaceNewEnterShopTime;
    @ViewById(resName = "sil_face_enter_shop_time_list")
    SettingItemLayout silFaceEnterShopTimeList;

    @Extra
    int mShopId;
    @Extra
    Face mFace;
    @Extra
    FaceGroup mFaceGroup;
    private int groupId, targetGroupId, gender, ageRangeCode;
    private String groupName, ageRange;
    private List<FaceAge> faceAgesList;
    private List<FaceGroup> groupList;

    private BottomPopMenu mPickerDialog;
    private CommonDialog mUploadDialog;
    private CommonDialog mDeleteDialog;
    private TakePhotoAgent mPickerAgent;

    private GlideRoundCrop mRoundTransform;
    private int mFrom;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new FaceDetailPresenter(mShopId, mFace);
        mPresenter.attachView(this);
        mPresenter.faceAgeRange();
        mPresenter.loadGroup();
        initFaceInfo();
        mPickerAgent = TakePhoto.with(this)
                .setTakePhotoListener(new PickerResult())
                .build();
        mRoundTransform = new GlideRoundCrop((int) getResources().getDimension(R.dimen.dp_6));
    }

    private void initFaceInfo() {
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(this)
                .load(mFace.getImgUrl())
                .apply(requestOptions)
                .into(ivFaceImage);
        if (mFaceGroup != null) {
            silFaceId.setRightText(Utils.getGroupName(this, mFaceGroup));
            targetGroupId = mFaceGroup.getGroupId();
        }
        if (mFace != null) {
            groupId = mFace.getGroupId();
            gender = mFace.getGender();
            silFaceName.getRightText().setSingleLine();
            silFaceName.getRightText().setEllipsize(TextUtils.TruncateAt.END);
            silFaceName.setRightText(mFace.getName());
            silFaceSex.setRightText(mFace.getGender() == 1 ? context.getString(R.string.ipc_face_gender_male) :
                    context.getString(R.string.ipc_face_gender_female));
            silFaceEnterShopNum.setRightText(mFace.getArrivalCount() + "");
            silFaceEnterShopNum.setRightImage(null);
            if (mFace.getCreateTime() != 0) {
                silFaceRegisterTime.setRightText(secondToDate(mFace.getCreateTime(), DATE_FORMAT_REGISTER));
            }
            if (mFace.getLastArrivalTime() != 0) {
                silFaceNewEnterShopTime.setRightText(secondToDate(mFace.getLastArrivalTime(), DATE_FORMAT_ENTER_SHOP));
            }
        }
        titleBar.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteDialog == null) {
                    mDeleteDialog = new CommonDialog.Builder(FaceDetailActivity.this)
                            .setTitle(R.string.ipc_face_tip_delete)
                            .setConfirmButton(R.string.ipc_setting_delete, R.color.common_orange,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mPresenter.delete();
                                        }
                                    })
                            .setCancelButton(R.string.sm_cancel)
                            .create();
                }
                mDeleteDialog.show();
            }
        });
    }

    private void takePhoto() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.ipc_face_tip_album_title)
                .setMessage(R.string.ipc_face_tip_album_content)
                .setMessageDrawable(0, 0, 0, R.mipmap.face_tip_image)
                .setMessageDrawablePadding(R.dimen.dp_12)
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickerAgent.takePhoto();
                    }
                })
                .create()
                .show();
    }

    private void openPicker() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.ipc_face_tip_album_title)
                .setMessage(R.string.ipc_face_tip_album_content)
                .setMessageDrawable(0, 0, 0, R.mipmap.face_tip_image)
                .setMessageDrawablePadding(R.dimen.dp_12)
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickerAgent.pickSinglePhoto();
                    }
                })
                .create()
                .show();
    }

    @Background
    void compress(File file) {
        file = Utils.imageCompress(this, file);
        if (file == null) {
            mUploadDialog.dismiss();
            shortTip(R.string.toast_network_error);
        } else {
            mPresenter.upload(file);
        }
    }

    @Click(resName = "iv_face_image")
    void imageClick() {
        if (mPickerDialog == null) {
            mPickerDialog = new BottomPopMenu.Builder(this)
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
                            openPicker();
                        }
                    }))
                    .addItemAction(new PopItemAction(R.string.sm_cancel,
                            PopItemAction.PopItemStyle.Cancel))
                    .create();
        }
        mPickerDialog.show();
    }

    @Click(resName = "sil_face_name")
    void nameClick() {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_face_name)
                .setHint(getString(R.string.ipc_face_input_name_tip))
                .setInitInputContent(silFaceName.getRightText().getText().toString().trim())
                .setInputWatcher(new InputDialog.TextChangeListener() {
                    @Override
                    public void onTextChange(EditText view, Editable s) {
                        if (TextUtils.isEmpty(s.toString())) {
                            return;
                        }
                        String name = s.toString().trim();
                        if (name.length() > IPC_NAME_MAX_LENGTH) {
                            shortTip(getString(R.string.ipc_face_name_length_tip));
                            do {
                                name = name.substring(0, name.length() - 1);
                            }
                            while (name.length() > IPC_NAME_MAX_LENGTH);
                            view.setText(name);
                            view.setSelection(name.length());
                        }
                    }
                })
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        if (input.length() > IPC_NAME_MAX_LENGTH) {
                            shortTip(getString(R.string.ipc_face_name_length_tip));
                            return;
                        }
                        if (input.trim().length() == 0) {
                            shortTip(getString(R.string.ipc_face_input_name_tip));
                            return;
                        }
                        mPresenter.updateName(input);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Click(resName = "sil_face_id")
    void idClick() {
        selectDialog(getString(R.string.ipc_face_selected_group), UPDATE_INDEX_ID);
    }

    @Click(resName = "sil_face_sex")
    void sexClick() {
        selectDialog(getString(R.string.ipc_face_selected_gender), UPDATE_INDEX_GENDER);
    }

    @Click(resName = "sil_face_age")
    void ageClick() {
        selectDialog(getString(R.string.ipc_face_selected_age_range), UPDATE_INDEX_AGE);
    }

    @Click(resName = "sil_face_enter_shop_time_list")
    void timeListClick() {
        FaceDetailRecordActivity_.intent(context)
                .mShopId(mShopId)
                .mFace(mFace)
                .start();
    }

    @Override
    public void updateImageSuccessView(String url) {
        mUploadDialog.dismiss();
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .into(ivFaceImage);
        setResult(RESULT_OK);
    }

    @Override
    public void updateImageFailed(final int code, String file) {
        int size = (int) getResources().getDimension(R.dimen.dp_90);
        Glide.with(this)
                .load(file)
                .apply(RequestOptions.bitmapTransform(mRoundTransform))
                .into(new CustomTarget<Drawable>(size, size) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        mUploadDialog.dismiss();
                        final boolean fromTakePhoto = mFrom == TakePhotoAgent.FROM_TAKE_PHOTO;
                        new CommonDialog.Builder(context)
                                .setTitle(R.string.ipc_face_error_upload)
                                .setMessage(code == 5526 ? R.string.ipc_face_error_photo
                                        : R.string.ipc_face_error_photo_network)
                                .setMessageDrawablePadding(R.dimen.dp_12)
                                .setMessageDrawable(null, null, null, resource)
                                .setCancelButton(R.string.sm_cancel)
                                .setConfirmButton(fromTakePhoto ? R.string.ipc_face_tack_photo_again
                                                : R.string.ipc_face_pick_again,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (fromTakePhoto) {
                                                    mPickerAgent.takePhoto();
                                                } else {
                                                    mPickerAgent.pickSinglePhoto();
                                                }
                                            }
                                        })
                                .create().show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public void updateNameSuccessView(String name) {
        silFaceName.setRightText(name);
        setResult(RESULT_OK);
    }

    @Override
    public void updateIdentitySuccessView(int targetGroupId) {
        silFaceId.setRightText(groupName);
        groupId = targetGroupId;//设置成功后的targetGroupId转化为groupId
        mPresenter.loadGroup();//刷新分组
        setResult(RESULT_OK);
    }

    @Override
    public void updateGenderSuccessView(int gender) {
        silFaceSex.setRightText(gender == 1 ? context.getString(R.string.ipc_face_gender_male) :
                context.getString(R.string.ipc_face_gender_female));
        setResult(RESULT_OK);
    }

    @Override
    public void updateAgeSuccessView(int ageRangeCode) {
        for (FaceAge age : faceAgesList) {
            if (ageRangeCode == age.getCode()) {
                silFaceAge.setRightText(age.getName());
            }
        }
        setResult(RESULT_OK);
    }

    @Override
    public void faceAgeRangeSuccessView(FaceAgeRangeResp data) {
        faceAgesList = data.getAgeRangeList();
        for (FaceAge age : faceAgesList) {
            if (mFace.getAgeRangeCode() == age.getCode()) {
                silFaceAge.setRightText(age.getName());
                ageRangeCode = age.getCode();
            }
        }
    }

    @Override
    public void loadGroupSuccessView(List<FaceGroup> list) {
        groupList = list;
    }

    @Override
    public void deleteSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 选择更新dialog
     *
     * @param title
     * @param updateIndex
     */
    private void selectDialog(String title, final int updateIndex) {
        int viewHeight = 0;
        if (updateIndex == UPDATE_INDEX_ID) {
            viewHeight = 600;
            groupName = silFaceId.getRightText() == null || TextUtils.isEmpty(silFaceId.getRightText().getText().toString())
                    ? "" : silFaceId.getRightText().getText().toString();
        } else if (updateIndex == UPDATE_INDEX_GENDER) {
            viewHeight = 350;
            gender = TextUtils.equals(context.getString(R.string.ipc_face_gender_male), silFaceSex.getRightText().getText().toString()) ? 1 : 2;
        } else if (updateIndex == UPDATE_INDEX_AGE) {
            viewHeight = 600;
            ageRange = silFaceAge.getRightText() == null || TextUtils.isEmpty(silFaceAge.getRightText().getText().toString())
                    ? "" : silFaceAge.getRightText().getText().toString();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context, com.commonlibrary.R.style.Son_dialog);
        assert inflater != null;
        final View layout = inflater.inflate(com.commonlibrary.R.layout.dialog_common_list, null);
        dialog.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tvTitle = layout.findViewById(com.commonlibrary.R.id.tv_title);
        tvTitle.setText(title);
        final RecyclerView rvContent = layout.findViewById(com.commonlibrary.R.id.rv_content);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight);
        rvContent.setLayoutParams(params);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        if (updateIndex == UPDATE_INDEX_ID) {
            rvContent.setAdapter(new IdentityAdapter(context, R.layout.item_common, groupList));
        } else if (updateIndex == UPDATE_INDEX_GENDER) {
            final List<String> list = new ArrayList<>();
            list.add(context.getString(R.string.ipc_face_gender_male));
            list.add(context.getString(R.string.ipc_face_gender_female));
            rvContent.setAdapter(new GenderAdapter(context, R.layout.item_common, list));
        } else if (updateIndex == UPDATE_INDEX_AGE) {
            rvContent.setAdapter(new AgeAdapter(context, R.layout.item_common, faceAgesList));
        }
        layout.findViewById(com.commonlibrary.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateIndex == UPDATE_INDEX_ID) {
                    mPresenter.updateIdentity(groupId, targetGroupId);
                } else if (updateIndex == UPDATE_INDEX_GENDER) {
                    mPresenter.updateGender(gender);
                } else if (updateIndex == UPDATE_INDEX_AGE) {
                    mPresenter.updateAge(ageRangeCode);
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerAgent.onRestoreInstanceState(savedInstanceState);
    }

    private class PickerResult implements TakePhoto.TakePhotoListener {

        @Override
        public void onSuccess(int from, Result result) {
            mFrom = from;
            final File file = new File(result.getImage().getPath());
            int size = (int) getResources().getDimension(R.dimen.dp_90);
            Glide.with(context)
                    .load(file)
                    .apply(RequestOptions.bitmapTransform(mRoundTransform))
                    .into(new CustomTarget<Drawable>(size, size) {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            mUploadDialog = new CommonDialog.Builder(context)
                                    .setTitle(R.string.ipc_face_tip_photo_uploading_title)
                                    .setMessage(R.string.ipc_face_tip_photo_uploading_content)
                                    .setMessageDrawablePadding(R.dimen.dp_12)
                                    .setMessageDrawable(null, null, null, resource)
                                    .create();
                            mUploadDialog.show();
                            compress(file);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        @Override
        public void onError(int errorCode, int from, String msg) {
        }

        @Override
        public void onCancel(int from) {
        }
    }

    class GenderAdapter extends CommonListAdapter<String> {

        /**
         * @param context  上下文
         * @param layoutId layout
         * @param list     列表数据
         */
        int selectedIndex = -1;

        GenderAdapter(Context context, int layoutId, List<String> list) {
            super(context, layoutId, list);
        }

        @Override
        public void convert(final ViewHolder holder, final String data) {
            SettingItemLayout item = holder.getView(R.id.sil_item);
            item.setLeftText(data);
            String genderName = gender == 1 ? context.getString(R.string.ipc_face_gender_male) : context.getString(R.string.ipc_face_gender_female);
            if (TextUtils.equals(genderName, data)) {
                selectedIndex = holder.getAdapterPosition();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = holder.getAdapterPosition();
                    gender = TextUtils.equals(context.getString(R.string.ipc_face_gender_male), data) ? 1 : 2;
                    notifyDataSetChanged();
                }
            });
            selectedItem(selectedIndex, holder, item);
        }
    }

    class IdentityAdapter extends CommonListAdapter<FaceGroup> {

        /**
         * @param context  上下文
         * @param layoutId layout
         * @param list     列表数据
         */
        int selectedIndex = -1;

        IdentityAdapter(Context context, int layoutId, List<FaceGroup> list) {
            super(context, layoutId, list);
        }

        @Override
        public void convert(final ViewHolder holder, final FaceGroup data) {
            final SettingItemLayout item = holder.getView(R.id.sil_item);
            final int count = data.getCapacity() - data.getCount();
            item.setLeftText(Utils.getGroupName(context, data) + getString(R.string.ipc_face_need_max_num, count));
            if (TextUtils.equals(Utils.getGroupName(context, data), groupName) && count != 0) {
                selectedIndex = holder.getAdapterPosition();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count == 0) {
                        shortTip(getString(R.string.ipc_face_full_capacity));
                        return;
                    }
                    selectedIndex = holder.getAdapterPosition();
                    targetGroupId = data.getGroupId();
                    groupName = Utils.getGroupName(context, data);
                    notifyDataSetChanged();
                }
            });
            selectedItem(selectedIndex, holder, item);
            if (count == 0) {
                item.setLeftTextColor(ContextCompat.getColor(context, R.color.text_disable));
                item.setRightImage(null);
            }
        }
    }

    class AgeAdapter extends CommonListAdapter<FaceAge> {

        /**
         * @param context  上下文
         * @param layoutId layout
         * @param list     列表数据
         */
        int selectedIndex = -1;

        AgeAdapter(Context context, int layoutId, List<FaceAge> list) {
            super(context, layoutId, list);
        }

        @Override
        public void convert(final ViewHolder holder, final FaceAge data) {
            SettingItemLayout item = holder.getView(R.id.sil_item);
            item.setLeftText(data.getName());
            if (TextUtils.equals(ageRange, data.getName())) {
                selectedIndex = holder.getAdapterPosition();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = holder.getAdapterPosition();
                    ageRangeCode = data.getCode();
                    notifyDataSetChanged();
                }
            });
            selectedItem(selectedIndex, holder, item);
        }
    }

    private void selectedItem(int selectedIndex, ViewHolder holder, SettingItemLayout item) {
        if (selectedIndex == holder.getAdapterPosition()) {
            item.setRightImage(ContextCompat.getDrawable(context, com.sunmi.ipc.R.mipmap.ic_yes));
            item.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.common_orange));
        } else {
            item.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.text_main));
            item.setRightImage(null);
        }
    }


}