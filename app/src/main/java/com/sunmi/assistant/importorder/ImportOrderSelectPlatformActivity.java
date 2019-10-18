package com.sunmi.assistant.importorder;

import android.annotation.SuppressLint;
import android.content.Context;
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
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.PlatformInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * 选择平台
 *
 * @author YangShiJie
 * @date 2019/10/14
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_store)
public class ImportOrderSelectPlatformActivity extends BaseActivity {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tv_tip)
    TextView tvTip;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    private PlatformInfo.SaasListBean selectPlatformBean;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(R.string.import_order_select_cash_register_software);
        tvTip.setText(R.string.import_order_select_shop_cash_register_software);
        btnComplete.setText(R.string.str_next);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        CommonHelper.isCanClick(btnComplete, false);
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
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
                hideLoadingDialog();
            }
        });
    }

    @Click({R.id.btnComplete})
    void btnNext() {
        if (selectPlatformBean == null) {
            return;
        }
        ImportOrderPlatformMobileActivity_.intent(this)
                .selectPlatformBean(selectPlatformBean)
                .start();
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
                selectPlatformBean = bean;
                notifyDataSetChanged();
                CommonHelper.isCanClick(btnComplete, true);
            });
            if (selectedIndex == holder.getAdapterPosition()) {
                tvPlatform.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
                ivSelect.setVisibility(View.VISIBLE);
            } else {
                tvPlatform.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
                ivSelect.setVisibility(View.GONE);
            }
        }
    }
}
