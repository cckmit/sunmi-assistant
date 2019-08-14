package com.sunmi.assistant.mine.platform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

import static com.sunmi.assistant.mine.shop.ShopListActivity.INTENT_EXTRA_SUCCESS;
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
    RecyclerView recyclerView;
    @ViewById(R.id.btn_next)
    Button btnNext;

    private String selectPlatform;
    private int selectSaasSource;

    @Extra
    boolean isCanBack;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (isCanBack) {
            titleBar.setLeftImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_back_dark));
        }
        initRecycler();
        CommonHelper.isCanClick(btnNext, false);
        getPlatformList();
    }

    private void getPlatformList() {
        showLoadingDialog();
        SunmiStoreApi.getPlatformList(new RetrofitCallback<PlatformInfo>() {
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
        if (isCanBack) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.txt_right:
                //没有对接saas数据Q
                SpUtils.setSaasExist(0);
                GotoActivityUtils.gotoMainActivity(this);
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
                .startForResult(REQUEST_CODE_SHOP);
    }

    @OnActivityResult(REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null
                && data.getBooleanExtra(INTENT_EXTRA_SUCCESS, false)) {
            Intent intent = getIntent();
            intent.putExtra(INTENT_EXTRA_SUCCESS, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        titleBar.getLeftImg().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
    }

    private void showViewList(List<PlatformInfo.SaasListBean> list) {
        recyclerView.setAdapter(new PlatformListAdapter(this, list));
    }

    private class PlatformListAdapter extends CommonListAdapter<PlatformInfo.SaasListBean> {

        int selectedIndex;

        private PlatformListAdapter(Context context, List<PlatformInfo.SaasListBean> list) {
            super(context, R.layout.item_merchant_platform, list);
            selectedIndex = -1;
        }

        @Override
        public void convert(ViewHolder holder, final PlatformInfo.SaasListBean bean) {
            TextView tvPlatform = holder.getView(R.id.tv_platform);
            tvPlatform.setText(bean.getSaas_name());
            ImageView ivSelect = holder.getView(R.id.iv_select);
            holder.itemView.setOnClickListener(v -> {
                selectedIndex = holder.getAdapterPosition();
                selectPlatform = bean.getSaas_name();
                selectSaasSource = bean.getSaas_source();
                notifyDataSetChanged();
                CommonHelper.isCanClick(btnNext, true);
            });
            if (selectedIndex == holder.getAdapterPosition()) {
                tvPlatform.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
                ivSelect.setVisibility(View.VISIBLE);
            } else {
                tvPlatform.setTextColor(ContextCompat.getColor(mContext, R.color.text_color));
                ivSelect.setVisibility(View.GONE);
            }
        }
    }
}
