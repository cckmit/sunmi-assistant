package com.sunmi.assistant.importorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
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
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
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
    SmRecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    private PlatformInfo.SaasListBean selectPlatformBean;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(R.string.import_order_select_cash_register_software);
        tvTip.setText(R.string.import_order_select_shop_cash_register_software);
        btnComplete.setText(R.string.str_next);
        recyclerView.init(R.drawable.shap_line_divider);
        btnComplete.setEnabled(false);
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
                selectPlatformBean = bean;
                notifyDataSetChanged();
                btnComplete.setEnabled(true);
            });
        }
    }
}
