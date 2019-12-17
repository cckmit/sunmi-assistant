package com.sunmi.assistant.importorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.xiaojinzi.component.impl.Router;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.AppApi;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
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
    SmRecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;
    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;
    @Extra
    int importOrderType;
    private AuthStoreInfo.SaasUserInfoListBean selectBean;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(R.string.str_select_store);
        tvTip.setText(R.string.import_order_selecte_shop);
        btnComplete.setText(R.string.import_order_access_data);
        recyclerView.init(R.drawable.shap_line_divider);
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
                        if (importOrderType == CommonConstants.IMPORT_ORDER_FROM_COMMON) {
                            Router.withApi(AppApi.class).goToMain(context);
                        } else {
                            Router.withApi(SunmiServiceApi.class)
                                    .goToWebViewCloudSingle(context, CommonConfig.SERVICE_H5_URL + CommonConstants.H5_CASH_VIDEO, null);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Authorize shop Failed. " + msg);
                        hideLoadingDialog();
                        if (code == 3) {
                            shortTip(R.string.import_order_access_already_auth);
                        } else if (code == 2) {
                            shortTip(R.string.import_order_access_no_auth_more);
                        } else {
                            shortTip(R.string.import_order_access_fail);
                        }
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
            super(context, R.layout.item_common_checked, list);
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
            SettingItemLayout item = holder.getView(R.id.sil_item);
            item.setTitle(info.getShop_name());
            item.setChecked(list.size() == 1 || selectedIndex == holder.getAdapterPosition());

            holder.itemView.setOnClickListener(v -> {
                selectedIndex = holder.getAdapterPosition();
                selectBean = info;
                enableCompleteBtn(true);
                notifyDataSetChanged();
            });
        }
    }
}
