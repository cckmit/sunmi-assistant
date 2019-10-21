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
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * @author yangShiJie
 * @date 2019-10-14
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_store)
public class ImportOrderSelectShopActivity extends BaseActivity {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tv_tip)
    TextView tvTip;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;
    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;
    private AuthStoreInfo.SaasUserInfoListBean selectBean;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(R.string.str_select_store);
        tvTip.setText(R.string.import_order_selecte_shop);
        btnComplete.setText(R.string.import_order_access_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ShopListAdapter(this, list));
    }

    @Click(R.id.btnComplete)
    void completeClick() {
        authorize();
    }

    private void authorize() {
        if (selectBean == null) {
            return;
        }
        showLoadingDialog();
        SunmiStoreApi.getInstance().authorizeSaas(SpUtils.getCompanyId(), SpUtils.getShopId(),
                selectBean.getSaas_source(), selectBean.getShop_no(), selectBean.getSaas_name(), 2,
                new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        hideLoadingDialog();
                        shortTip(R.string.import_order_access_data_success);
                        BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSaasDock);
                        GotoActivityUtils.gotoMainActivity(context);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Authorize shop Failed. " + msg);
                        hideLoadingDialog();
                        shortTip(R.string.import_order_access_fail);
                    }
                });
    }


    private void enableCompleteBtn(boolean enable) {
        btnComplete.setEnabled(enable);
        if (enable) {
            btnComplete.setAlpha(1f);
        } else {
            btnComplete.setAlpha(0.5f);
        }
    }

    private class ShopListAdapter extends CommonListAdapter<AuthStoreInfo.SaasUserInfoListBean> {
        int selectedIndex = -1;

        private ShopListAdapter(Context context, List<AuthStoreInfo.SaasUserInfoListBean> list) {
            super(context, R.layout.item_import_order_select_shop, list);
            if (list.size() > 1) {
                enableCompleteBtn(false);
            }
            if (list.size() == 1) {
                enableCompleteBtn(true);
                selectBean = list.get(0);
            }
        }

        @Override
        public void convert(ViewHolder holder, AuthStoreInfo.SaasUserInfoListBean info) {
            TextView tvName = holder.getView(R.id.tv_shop_name);
            ImageView ivSelect = holder.getView(R.id.iv_select);
            tvName.setText(info.getShop_name());
            holder.itemView.setOnClickListener(v -> {
                selectedIndex = holder.getAdapterPosition();
                selectBean = info;
                enableCompleteBtn(true);
                notifyDataSetChanged();
            });
            if (list.size() == 1 || selectedIndex == holder.getAdapterPosition()) {
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.common_orange));
                ivSelect.setVisibility(View.VISIBLE);
            } else {
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.text_main));
                ivSelect.setVisibility(View.GONE);
            }
        }
    }
}
