package com.sunmi.ipc.face;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceDetailContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceDetailPresenter;
import com.sunmi.ipc.face.util.Utils;
import com.sunmi.ipc.model.FaceAgeRangeResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.luban.Luban;
import sunmi.common.mediapicker.TakePhoto;
import sunmi.common.mediapicker.TakePhotoAgent;
import sunmi.common.mediapicker.data.model.Result;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

import static com.sunmi.ipc.face.contract.FaceUploadContract.FILE_SIZE_1M;
import static sunmi.common.utils.DateTimeUtils.secondToDate;


/**
 * @author yangShiJie
 * @date 2019/8/27
 */
@EActivity(resName = "face_activity_photo_detail")
public class FaceDetailActivity extends BaseMvpActivity<FaceDetailPresenter>
        implements FaceDetailContract.View {
    private static final String DATE_FORMAT_REGISTER = "yyyy/MM/dd";
    public static final String DATE_FORMAT_ENTER_SHOP = "yyyy/MM/dd  hh:mm";
    private static final int IPC_NAME_MAX_LENGTH = 36;
    private static final int UPDATE_INDEX_ID = 0;
    private static final int UPDATE_INDEX_GENDER = 1;
    private static final int UPDATE_INDEX_ADE = 2;

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
    private int targetGroupId, gender, ageRangeCode;
    private String groupName;
    private List<FaceAge> faceAgesList;
    private List<FaceGroup> groupList;
    private int updateIndex = -1;

    private BottomPopMenu mPickerDialog;
    private CommonDialog mUploadDialog;
    private TakePhotoAgent mPickerAgent;

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
    }

    private void initFaceInfo() {
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(this).load(mFace.getImgUrl()).apply(requestOptions).into(ivFaceImage);
        if (mFaceGroup != null) {
            silFaceId.setRightText(Utils.getGroupName(this, mFaceGroup, false));
        }
        if (mFace != null) {
            silFaceName.setRightText(mFace.getName());
            silFaceSex.setRightText(mFace.getGender() == 1 ? context.getString(R.string.ipc_face_gender_male) :
                    context.getString(R.string.ipc_face_gender_female));
            silFaceEnterShopNum.setRightText(mFace.getArrivalCount() + "");
            if (mFace.getCreateTime() != 0) {
                silFaceRegisterTime.setRightText(secondToDate(mFace.getCreateTime(), DATE_FORMAT_REGISTER));
            }
            if (mFace.getLastArrivalTime() != 0) {
                silFaceNewEnterShopTime.setRightText(secondToDate(mFace.getLastArrivalTime(), DATE_FORMAT_ENTER_SHOP));
            }
        }
    }

    private void takePhoto() {
        new CommonDialog.Builder(this)
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
        new CommonDialog.Builder(this)
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
        try {
            int count = 0;
            while (file.length() > FILE_SIZE_1M && count < 3) {
                List<File> files = Luban.with(this)
                        .setTargetDir(FileHelper.SDCARD_CACHE_IMAGE_PATH)
                        .load(file)
                        .get();
                file = files.get(0);
                count++;
            }
            mPresenter.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
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
                .setInitInputContent(mFace.getName())
                .setInputWatcher(new InputDialog.TextChangeListener() {
                    @Override
                    public void onTextChange(EditText view, Editable s) {
                        if (TextUtils.isEmpty(s.toString())) {
                            return;
                        }
                        String name = s.toString().trim();
                        if (name.length() > IPC_NAME_MAX_LENGTH) {
                            shortTip(R.string.ipc_setting_tip_name_length);
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
        updateIndex = UPDATE_INDEX_ID;
        identityDialog(getString(R.string.ipc_face_selected_group));
    }

    @Click(resName = "sil_face_sex")
    void sexClick() {
        updateIndex = UPDATE_INDEX_GENDER;
        genderDialog(getString(R.string.ipc_face_selected_gender));
    }

    @Click(resName = "sil_face_age")
    void ageClick() {
        updateIndex = UPDATE_INDEX_ADE;
        ageDialog(getString(R.string.ipc_face_selected_age_range));
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
        Glide.with(this).load(url).apply(requestOptions).into(ivFaceImage);
        setResult(RESULT_OK);
    }

    @Override
    public void updateImageFailed() {
        mUploadDialog.dismiss();
        new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_face_error_upload)
                .setMessage(R.string.ipc_face_error_photo)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_face_tack_photo_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickerAgent.takePhoto();
                    }
                })
                .create().show();
    }

    @Override
    public void updateNameSuccessView(String name) {
        silFaceName.setRightText(name);
        setResult(RESULT_OK);
    }

    @Override
    public void updateIdentitySuccessView() {
        silFaceId.setRightText(groupName);
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
            }
        }
        setResult(RESULT_OK);
    }

    @Override
    public void loadGroupSuccessView(List<FaceGroup> list) {
        groupList = list;
    }

    private void identityDialog(String title) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context, com.commonlibrary.R.style.Son_dialog);
        assert inflater != null;
        View layout = inflater.inflate(com.commonlibrary.R.layout.dialog_common_list, null);
        dialog.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tvTitle = layout.findViewById(com.commonlibrary.R.id.tv_title);
        RecyclerView rvContent = layout.findViewById(com.commonlibrary.R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        tvTitle.setText(title);
        rvContent.setAdapter(new CommonListAdapter<FaceGroup>(this,
                R.layout.item_common, groupList) {
            int selectedIndex = -1;

            @Override
            public void convert(final ViewHolder holder, final FaceGroup data) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setLeftText(Utils.getGroupName(context, data, false));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        targetGroupId = data.getTargetGroupId();
                        groupName = Utils.getGroupName(context, data, false);
                        notifyDataSetChanged();
                    }
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    shopItem.setRightImage(ContextCompat.getDrawable(context, com.sunmi.ipc.R.mipmap.ic_yes));
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.common_orange));
                } else {
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.colorText));
                    shopItem.setRightImage(null);
                }
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateIdentity(targetGroupId);
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void ageDialog(String title) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context, com.commonlibrary.R.style.Son_dialog);
        assert inflater != null;
        View layout = inflater.inflate(com.commonlibrary.R.layout.dialog_common_list, null);
        dialog.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tvTitle = layout.findViewById(com.commonlibrary.R.id.tv_title);
        RecyclerView rvContent = layout.findViewById(com.commonlibrary.R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        tvTitle.setText(title);

        rvContent.setAdapter(new CommonListAdapter<FaceAge>(this,
                R.layout.item_common, faceAgesList) {
            int selectedIndex = -1;

            @Override
            public void convert(final ViewHolder holder, final FaceAge data) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setLeftText(data.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        ageRangeCode = data.getCode();
                        notifyDataSetChanged();
                    }
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    shopItem.setRightImage(ContextCompat.getDrawable(context, com.sunmi.ipc.R.mipmap.ic_yes));
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.common_orange));
                } else {
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.colorText));
                    shopItem.setRightImage(null);
                }
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateAge(ageRangeCode);
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void genderDialog(String title) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context, com.commonlibrary.R.style.Son_dialog);
        assert inflater != null;
        View layout = inflater.inflate(com.commonlibrary.R.layout.dialog_common_list, null);
        dialog.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tvTitle = layout.findViewById(com.commonlibrary.R.id.tv_title);
        RecyclerView rvContent = layout.findViewById(com.commonlibrary.R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        tvTitle.setText(title);
        final List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.ipc_face_gender_male));
        list.add(context.getString(R.string.ipc_face_gender_female));
        rvContent.setAdapter(new CommonListAdapter<String>(this,
                R.layout.item_common, list) {
            int selectedIndex = -1;

            @Override
            public void convert(final ViewHolder holder, final String data) {
                SettingItemLayout shopItem = holder.getView(R.id.sil_item);
                shopItem.setLeftText(data);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        gender = TextUtils.equals(context.getString(R.string.ipc_face_gender_male), data) ? 1 : 2;
                        notifyDataSetChanged();
                    }
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    shopItem.setRightImage(ContextCompat.getDrawable(context, com.sunmi.ipc.R.mipmap.ic_yes));
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.common_orange));
                } else {
                    shopItem.setLeftTextColor(ContextCompat.getColor(context, com.sunmi.ipc.R.color.colorText));
                    shopItem.setRightImage(null);
                }
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout.findViewById(com.commonlibrary.R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateGender(gender);
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
            if (mUploadDialog == null) {
                mUploadDialog = new CommonDialog.Builder(FaceDetailActivity.this)
                        .setTitle(R.string.ipc_face_tip_photo_uploading_title)
                        .setMessage(R.string.ipc_face_tip_photo_uploading_content)
                        .create();
            }
            mUploadDialog.show();
            compress(new File(result.getImage().getPath()));
        }

        @Override
        public void onError(int errorCode, int from, String msg) {
        }

        @Override
        public void onCancel(int from) {
        }
    }

}