package com.sunmi.assistant.mine.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.PlatformInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

import static com.sunmi.assistant.mine.shop.ShopListActivity.REQUEST_CODE_SHOP;

/**
 * 选择平台
 *
 * @author YangShiJie
 * @date 2019/6/26
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_platform)
public class SelectPlatformActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerView)
    SmRecyclerView recyclerView;
    @ViewById(R.id.btn_next)
    Button btnNext;

    private String selectPlatform;
    private int selectSaasSource;

    @Extra
    boolean isCanBack;
    @Extra
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;
    @Extra
    boolean isLoginSuccessSwitchCompany;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        initRecycler();
        btnNext.setEnabled(false);
        getPlatformList();
    }

    private void getPlatformList() {
        showLoadingDialog();
        SunmiStoreApi.getInstance().getPlatformList(new RetrofitCallback<PlatformInfo>() {
            @Override
            public void onSuccess(int code, String msg, PlatformInfo data) {
                hideLoadingDialog();
                showViewList(data.getSaasList());
            }

            @Override
            public void onFail(int code, String msg, PlatformInfo data) {
                hideLoadingDialog();
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Click({R.id.btn_next})
    void btnNext() {
        PlatformMobileActivity_.intent(this)
                .platform(selectPlatform)
                .saasSource(selectSaasSource)
                .companyId(companyId)
                .companyName(companyName)
                .saasExist(saasExist)
                .isLoginSuccessSwitchCompany(isLoginSuccessSwitchCompany)
                .startForResult(REQUEST_CODE_SHOP);
    }

    @OnActivityResult(REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.init(R.drawable.shap_line_divider);
        titleBar.getLeftImg().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
    }

    private void showViewList(List<PlatformInfo.SaasListBean> list) {
        recyclerView.setAdapter(new PlatformListAdapter(this, list));
    }

    private class PlatformListAdapter extends CommonListAdapter<PlatformInfo.SaasListBean> {

        int selectedIndex;

        private PlatformListAdapter(Context context, List<PlatformInfo.SaasListBean> list) {
            super(context, R.layout.item_common_checked, list);
            selectedIndex = -1;
        }

        @Override
        public void convert(ViewHolder holder, final PlatformInfo.SaasListBean bean) {
            SettingItemLayout item = holder.getView(R.id.sil_item);
            item.setTitle(bean.getSaas_name());
            item.setChecked(selectedIndex == holder.getAdapterPosition());

            holder.itemView.setOnClickListener(v -> {
                selectedIndex = holder.getAdapterPosition();
                selectPlatform = bean.getSaas_name();
                selectSaasSource = bean.getSaas_source();
                notifyDataSetChanged();
                btnNext.setEnabled(true);
            });
        }
    }
}
