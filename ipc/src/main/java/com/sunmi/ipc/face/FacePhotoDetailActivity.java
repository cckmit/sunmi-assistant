package com.sunmi.ipc.face;


import android.app.Dialog;
import android.content.Context;
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
import com.sunmi.ipc.face.contract.FacePhotoContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FacePhotoPresenter;
import com.sunmi.ipc.face.util.Utils;
import com.sunmi.ipc.model.FaceAgeRangeResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.InputDialog;

import static sunmi.common.utils.CommonHelper.long2Time;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
@EActivity(resName = "face_activity_photo_detail")
public class FacePhotoDetailActivity extends BaseMvpActivity<FacePhotoPresenter>
        implements FacePhotoContract.View {
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

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new FacePhotoPresenter(mShopId);
        mPresenter.attachView(this);
        mPresenter.faceAgeRange();
        mPresenter.loadGroup();
        initFaceInfo();
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
                silFaceRegisterTime.setRightText(long2Time(mFace.getCreateTime() * 1000, DATE_FORMAT_REGISTER));
            }
            if (mFace.getLastArrivalTime() != 0) {
                silFaceNewEnterShopTime.setRightText(long2Time(mFace.getLastArrivalTime() * 1000, DATE_FORMAT_ENTER_SHOP));
            }
        }
    }

    @Click(resName = "iv_face_image")
    void imageClick() {

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
                        mPresenter.updateName(mFace.getFaceId(), mFace.getGroupId(), input);
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
        FacePhotoDetailTimeRecordActivity_.intent(context)
                .mShopId(mShopId)
                .mFace(mFace)
                .start();
    }

    @Override
    public void updateNameSuccessView(String name) {
        silFaceName.setRightText(name);
    }

    @Override
    public void updateIdentitySuccessView() {
        silFaceId.setRightText(groupName);
    }

    @Override
    public void updateGenderSuccessView(int gender) {
        silFaceSex.setRightText(gender == 1 ? context.getString(R.string.ipc_face_gender_male) :
                context.getString(R.string.ipc_face_gender_female));
    }

    @Override
    public void updateAgeSuccessView(int ageRangeCode) {
        for (FaceAge age : faceAgesList) {
            if (ageRangeCode == age.getCode()) {
                silFaceAge.setRightText(age.getName());
            }
        }
    }

    @Override
    public void faceAgeRangeSuccessView(FaceAgeRangeResp data) {
        faceAgesList = data.getAgeRangeList();
        for (FaceAge age : faceAgesList) {
            if (mFace.getAgeRangeCode() == age.getCode()) {
                silFaceAge.setRightText(age.getName());
            }
        }
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
                mPresenter.updateIdentity(mFace.getFaceId(), mFace.getGroupId(), targetGroupId);
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
                mPresenter.updateAge(mFace.getFaceId(), mFace.getGroupId(), ageRangeCode);
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
                mPresenter.updateGender(mFace.getFaceId(), mFace.getGroupId(), gender);
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}