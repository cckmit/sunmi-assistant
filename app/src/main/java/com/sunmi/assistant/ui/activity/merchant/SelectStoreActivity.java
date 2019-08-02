package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.AuthStoreCompleteContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import sunmi.common.model.CreateStoreInfo;
import com.sunmi.assistant.ui.activity.presenter.AuthStoreCompletePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * 选择门店
 * Created by YangShiJie on 2019/6/26.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_store)
public class SelectStoreActivity extends BaseMvpActivity<AuthStoreCompletePresenter>
        implements AuthStoreCompleteContract.View {
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    @Extra
    boolean isBack;
    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;

    public Map<String, Boolean> checkedMap = new HashMap<>();
    private List<AuthStoreInfo.SaasUserInfoListBean> listChecked = null;//选中列表
    private int createFlag, authFlag;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        if (!isBack) titleBar.getLeftLayout().setVisibility(View.GONE);
        initRecycler();
        mPresenter = new AuthStoreCompletePresenter();
        mPresenter.attachView(this);
        showViewList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isBack && keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    //先创建门店再授权
    @Click({R.id.btnComplete})
    void btnComplete() {
        showLoadingDialog();
        for (int i = 0; i < listChecked.size(); i++) {
            String shopName = listChecked.get(i).getShop_name();
            if (i == 0) {
                SpUtils.setShopName(shopName(shopName, i));//默认门店名称
                mPresenter.editStore(shopName(shopName, i));//第一个编辑门店
            } else {
                mPresenter.createStore(shopName(shopName, i));//创建门店
            }
        }
    }

    //门店名称
    private String shopName(String shopName, int i) {
        if (i < 10) {
            return shopName + "_0" + (i + 1);
        }
        return shopName + "_" + (i + 1);
    }

    /**
     * 编辑店铺-选择一个或多个店铺第一个店铺为初始化注册默认创建的
     *
     * @param data
     */
    @Override
    public void editStoreSuccess(Object data) {
        LogCat.e(TAG, "111 data editStoreSuccess shopNo=");
        createFlag++;
        mPresenter.authStoreCompleteInfo(SpUtils.getShopId(),
                listChecked.get(0).getSaas_source(),
                listChecked.get(0).getShop_no(),
                listChecked.get(0).getSaas_name());
    }

    @Override
    public void editStoreFail(int code, String msg) {
        LogCat.e(TAG, "111 data editStoreFail code=" + code + "," + msg);
        createFlag++;
        isGotoMainActivity();
    }

    /**
     * 创建门店
     *
     * @param data
     */
    @Override
    public void createStoreSuccess(CreateStoreInfo data) {
        //成功后授权
        createFlag++;
        LogCat.e(TAG, "111 data createStoreSuccess createFlag=" + (createFlag - 1));
        mPresenter.authStoreCompleteInfo(data.getShop_id(),
                listChecked.get(createFlag - 1).getSaas_source(),
                listChecked.get(createFlag - 1).getShop_no(),
                listChecked.get(createFlag - 1).getSaas_name());
    }

    @Override
    public void createStoreFail(int code, String msg) {
        LogCat.e(TAG, "111 data createStoreFail msg=" + msg);
        createFlag++;
        if (code == 5034) {
            shortTip(getString(R.string.str_create_store_alredy_exit));
        } else {
            shortTip(getString(R.string.str_create_store_fail));
        }
        isGotoMainActivity();
    }

    /**
     * 授权
     *
     * @param data
     */
    @Override
    public void authStoreCompleteSuccess(Object data) {
        LogCat.e(TAG, "111 data authStoreCompleteSuccess");
        authFlag++;
        SpUtils.setSaasExist(1);//对接saas数据
        isGotoMainActivity();
    }

    @Override
    public void authStoreCompleteFail(int code, String msg) {
        LogCat.e(TAG, "111 data authStoreCompleteFail code=" + code + "," + msg);
        authFlag++;
        isGotoMainActivity();
    }

    private void isGotoMainActivity() {
        int checkedNum = listChecked.size();
        if (checkedNum == createFlag || checkedNum == authFlag) {
            createFlag = 0;
            authFlag = 0;
            hideLoadingDialog();
            GotoActivityUtils.gotoMainActivity(this);
        }
    }

    // 保存选中的数据
    public void listCheckedNotifyDataSetChanged() {
        listChecked = new ArrayList<>();
        AuthStoreInfo.SaasUserInfoListBean bean;
        for (int i = 0; i < list.size(); i++) {
            Boolean isChecked = checkedMap.get(list.get(i).getShop_no());
            bean = new AuthStoreInfo.SaasUserInfoListBean();
            if (isChecked != null && isChecked) {
                bean.setShop_name(list.get(i).getShop_name());
                bean.setSaas_name(list.get(i).getSaas_name());
                bean.setShop_no(list.get(i).getShop_no());
                bean.setSaas_source(list.get(i).getSaas_source());
                listChecked.add(bean);
            }
        }
        isCanClick(listChecked);
    }

    private void showViewList() {
        isCanClick(listChecked);
        recyclerView.setAdapter(new CommonListAdapter<AuthStoreInfo.SaasUserInfoListBean>(context,
                R.layout.item_merchant_auth_store, list) {
            @Override
            public void convert(ViewHolder holder, final AuthStoreInfo.SaasUserInfoListBean bean) {
                holder.setText(R.id.tvName, bean.getShop_name());
                holder.setText(R.id.tvPlatform, bean.getSaas_name());
                CheckBox checkBox = holder.getView(R.id.CBox);
                if (list.size() == 1) {
                    checkBox.setChecked(true);
                    checkedMap.put(bean.getShop_no(), true);
                    listCheckedNotifyDataSetChanged();
                }
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        checkedMap.put(bean.getShop_no(), true);
                    } else {
                        checkedMap.remove(bean.getShop_no());
                    }
                    listCheckedNotifyDataSetChanged();
                });
            }
        });
    }

    private void isCanClick(List<AuthStoreInfo.SaasUserInfoListBean> listChecked) {
        if (listChecked != null && listChecked.size() > 0) {
            btnComplete.setAlpha(1f);
            btnComplete.setEnabled(true);
        } else {
            btnComplete.setAlpha(0.5f);
            btnComplete.setEnabled(false);
        }
    }

}
