package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.AuthStoreCompleteContract;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;
import com.sunmi.assistant.ui.activity.presenter.AuthStoreCompletePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
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

    private List<AuthStoreInfo> list = new ArrayList<>();
    public Map<Integer, Boolean> checkedMap = new HashMap<>();
    private List<AuthStoreInfo> listChecked = null;//选中列表

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initRecycler();
        mPresenter = new AuthStoreCompletePresenter();
        mPresenter.attachView(this);
        mPresenter.getAuthStoreCompleteInfo();

        showViewList();
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Click({R.id.btnComplete})
    void btnComplete() {
        for (int i = 0; i < listChecked.size(); i++) {
            LogCat.e(TAG, "getShopId=" + listChecked.get(i).getShopId());
        }
    }

    // 保存选中的数据
    public void listCheckedNotifyDataSetChanged() {
        listChecked = new ArrayList<>();
        AuthStoreInfo bean;
        for (int i = 0; i < list.size(); i++) {
            Boolean isChecked = checkedMap.get(list.get(i).getShopId());
            bean = new AuthStoreInfo();
            if (isChecked != null && isChecked) {
                bean.setShopName(list.get(i).getShopName());
                bean.setPlatform(list.get(i).getPlatform());
                bean.setShopId(list.get(i).getShopId());
                listChecked.add(bean);
            }
        }
    }

    private void showViewList() {
        AuthStoreInfo bean;
        for (int i = 0; i < 1; i++) {
            bean = new AuthStoreInfo();
            bean.setShopName("安居客");
            bean.setPlatform("11111111");
            bean.setShopId(i);
            list.add(bean);
        }
        recyclerView.setAdapter(new CommonListAdapter<AuthStoreInfo>(this, R.layout.item_merchant_auth_store, list) {
            @Override
            public void convert(ViewHolder holder, final AuthStoreInfo bean) {
                holder.setText(R.id.tvName, bean.getShopName());
                holder.setText(R.id.tvPlatform, bean.getPlatform());
                CheckBox checkBox = holder.getView(R.id.CBox);
                if (list.size() == 1) {
                    checkBox.setChecked(true);
                    checkedMap.put(bean.getShopId(), true);
                    listCheckedNotifyDataSetChanged();
                }
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        checkedMap.put(bean.getShopId(), true);
                    } else {
                        checkedMap.remove(bean.getShopId());
                    }
                    listCheckedNotifyDataSetChanged();
                });
            }
        });
    }

    @Override
    public void getAuthStoreCompleteSuccess(String data) {
        LogCat.e(TAG, "bean=" + data);

    }

    @Override
    public void getAuthStoreCompleteFail(int code, String msg) {

    }
}
