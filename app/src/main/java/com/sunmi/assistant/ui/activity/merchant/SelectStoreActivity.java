package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.MainActivity_;
import com.sunmi.assistant.ui.activity.contract.AuthStoreCompleteContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;
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
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * 选择门店
 * Created by YangShiJie on 2019/6/26.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_store)
public class SelectStoreActivity extends BaseMvpActivity<AuthStoreCompletePresenter>
        implements AuthStoreCompleteContract.View {
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    @Extra
    ArrayList<AuthStoreInfo.SaasUserInfoListBean> list;

    public Map<String, Boolean> checkedMap = new HashMap<>();
    private List<AuthStoreInfo.SaasUserInfoListBean> listChecked = null;//选中列表
    private String shopNo, saasName;
    private int saasSource;
    private int flagAuth;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initRecycler();
        mPresenter = new AuthStoreCompletePresenter();
        mPresenter.attachView(this);
        showViewList();
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    //先创建门店再授权
    @Click({R.id.btnComplete})
    void btnComplete() {
        for (int i = 0; i < listChecked.size(); i++) {
            //创建门店
            saasSource = listChecked.get(i).getSaas_source();
            saasName = listChecked.get(i).getSaas_name();
            shopNo = listChecked.get(i).getShop_no();
            mPresenter.createStore(listChecked.get(i).getShop_name());
        }
    }

    //创建门店
    @Override
    public void createStoreSuccess(CreateStoreInfo data) {
        //成功后授权
        mPresenter.authStoreCompleteInfo(data.getShop_id() + "", saasSource + "", shopNo, saasName);
    }

    @Override
    public void createStoreFail(int code, String msg) {
        if (code == 5034) {
            shortTip(getString(R.string.str_create_store_fail));
        } else {
            shortTip(getString(R.string.str_create_store_alredy_exit));
        }
    }

    //授权
    @Override
    public void authStoreCompleteSuccess(String data) {
        LogCat.e(TAG, "bean=" + data);
        flagAuth++;
        if (listChecked.size() == flagAuth) {
            gotoMainActivity(); //跳转到首页
        }
    }

    @Override
    public void authStoreCompleteFail(int code, String msg) {
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
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
        recyclerView.setAdapter(new CommonListAdapter<AuthStoreInfo.SaasUserInfoListBean>(this, R.layout.item_merchant_auth_store, list) {
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
