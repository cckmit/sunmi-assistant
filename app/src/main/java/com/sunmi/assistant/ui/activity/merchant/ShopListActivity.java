package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunmi.apmanager.R;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.MineStoreBean;
import com.sunmi.apmanager.rpc.merchant.MerchantApi;
import com.sunmi.apmanager.ui.activity.store.MyStoreDetailsActivity;
import com.sunmi.apmanager.utils.CommonUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * 我的店铺
 */
@SuppressLint("Registered")
@EActivity(resName = "activity_mine_my_store")
public class ShopListActivity extends BaseActivity {

    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;

    private List<MineStoreBean> list = new ArrayList<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShopList();
    }

    @Click(resName = "btnAdd")
    void onAddClick(View v) {//添加店铺
        CommonUtils.trackCommonEvent(context, "addStore",
                "主页_我的_我的店铺_添加店铺", Constants.EVENT_MY_INFO);
        //openActivity(this, AddStorePoiActivity.class, false);
        BottomPopMenu choosePhotoMenu = new BottomPopMenu.Builder(this)
                .setTitle(R.string.company_shop_new_create_or_import)
                .setIsShowCircleBackground(true)
                .addItemAction(new PopItemAction(R.string.company_shop_new_create,
                        PopItemAction.PopItemStyle.Normal, this::createShop))
                .addItemAction(new PopItemAction(R.string.company_shop_import,
                        PopItemAction.PopItemStyle.Normal, this::importShop))
                .addItemAction(new PopItemAction(R.string.sm_cancel,
                        PopItemAction.PopItemStyle.Cancel))
                .create();
        choosePhotoMenu.show();

    }

    //新建门店
    private void createShop() {
        CreateShopActivity_.intent(context)
                .companyId(SpUtils.getCompanyId())
                .start();
    }

    //导入门店
    private void importShop() {
        SelectPlatformActivity_.intent(context)
                .isCanBack(true)
                .start();
    }

    private void getShopList() {
        MerchantApi.getShopList(new RpcCallback(context) {//请求店铺列表
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    initList(data);
                    showList();
                } else {
                    shortTip(getString(R.string.str_store_load_error));
                }
            }
        });
    }

    private void initList(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            MineStoreBean bean;
            list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                bean = new MineStoreBean();
                JSONObject object = (JSONObject) jsonArray.opt(i);
                bean.setShop_id(object.getString("shop_id"));
                bean.setName(object.getString("name"));
                bean.setRole(object.getString("role"));
                bean.setMachineCount(getString(R.string.str_zero_dev)); //object.getString("machineCount")
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示列表数据
    @UiThread
    void showList() {
        final CommonListAdapter adapter = new CommonListAdapter<MineStoreBean>(context,
                R.layout.item_mine_store, list) {
            @Override
            public void convert(ViewHolder holder, final MineStoreBean bean) {
                holder.setText(R.id.tvName, bean.getName());
                holder.setText(R.id.tvNum, bean.getMachineCount());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.trackCommonEvent(context, "defaultStore",
                                "主页_我的_我的店铺_默认店铺", Constants.EVENT_MY_INFO);
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_id", bean.getShop_id());
                        openActivity(ShopListActivity.this,
                                MyStoreDetailsActivity.class, bundle, false);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
