package com.sunmi.assistant.ui.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.presenter.ChooseShopPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.ViewHolder;

/**
 * Description: ChooseShopActivity
 * Created by Bruce on 2019/7/1.
 */
@EActivity(R.layout.activity_choose_shop)
public class ChooseShopActivity extends BaseMvpActivity<ChooseShopPresenter>
        implements ChooseShopContract.View {

    @ViewById(R.id.rl_root)
    RelativeLayout rlRoot;
    @ViewById(R.id.tv_current_shop)
    TextView tvCurrShop;
    @ViewById(R.id.rv_choose)
    SmRecyclerView rvChoose;
    @ViewById(R.id.tv_tip_no_data)
    TextView tvNoData;

    @Extra
    int action;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ChooseShopPresenter();
        mPresenter.attachView(this);
        tvCurrShop.setText(SpUtils.getShopName());
        rvChoose.init(R.drawable.shap_line_divider);
        mPresenter.getShopList(SpUtils.getCompanyId());
    }

    @Override
    public void getShopListSuccess(List<ShopInfo> shopList) {
        List<ShopInfo> list = new ArrayList<>();
        for (ShopInfo shopInfo : shopList) {
            if (shopInfo.getShopId() != SpUtils.getShopId()) {
                list.add(shopInfo);
            }
        }
        initShopList(list);
    }

    @Override
    public void getShopListFail(int code, String msg, ShopListResp data) {

    }

    @Override
    public void getCompanyListSuccess(List<CompanyInfoResp> companyList) {

    }

    @Override
    public void getCompanyListFail(int code, String msg, CompanyListResp data) {

    }

    @UiThread
    void initShopList(final List<ShopInfo> data) {
        if (data.size() <= 0) {
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        tvNoData.setVisibility(View.GONE);
        rvChoose.setAdapter(new CommonListAdapter<ShopInfo>(context,
                R.layout.item_shop_list, data) {
            @Override
            public void convert(ViewHolder holder, final ShopInfo shopInfo) {
                holder.setText(R.id.tv_shop, shopInfo.getShopName());
                holder.itemView.setOnClickListener(v -> {
                    SpUtils.setShopId(shopInfo.getShopId());
                    SpUtils.setShopName(shopInfo.getShopName());
                    BaseNotification.newInstance().postNotificationName(
                            CommonNotifications.shopSwitched);
                    finish();
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, com.sunmi.apmanager.R.anim.activity_close_up_down);
    }
}
