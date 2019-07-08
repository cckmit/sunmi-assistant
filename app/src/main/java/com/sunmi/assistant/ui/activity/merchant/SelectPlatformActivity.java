package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.contract.SelectPlatformContract;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;
import com.sunmi.assistant.ui.activity.presenter.PlatformPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * 选择平台
 * Created by YangShiJie on 2019/6/26.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_merchant_select_platform)
public class SelectPlatformActivity extends BaseMvpActivity<PlatformPresenter>
        implements SelectPlatformContract.View, View.OnClickListener {
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btn_next)
    Button btnNext;
    private String selectPlatform;
    private int selectSaasSource;


    private List<PlatformInfo.SaasListBean> list = new ArrayList<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        titleBar.getLeftLayout().setVisibility(View.GONE);
        initRecycler();
        isCanClick(false);
        mPresenter = new PlatformPresenter();
        mPresenter.attachView(this);
        showLoadingDialog();
        mPresenter.getPlatformInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isCanClick(boolean isClick) {
        if (isClick) {
            btnNext.setAlpha(1f);
            btnNext.setEnabled(true);
        } else {
            btnNext.setAlpha(0.5f);
            btnNext.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_right:
                GotoActivityUtils.gotoMainActivity(this);
                break;
        }
    }

    @Click({R.id.btn_next})
    void btnNext() {
        CheckPlatformMobileActivity_.intent(this)
                .extra("platform", selectPlatform)
                .extra("saasSource", selectSaasSource)
                .start();
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        titleBar.getLeftImg().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
    }


    @Override
    public void getPlatformInfoSuccess(PlatformInfo data) {
        hideLoadingDialog();
        showViewList(data);
    }

    @Override
    public void getPlatformInfoFail(int code, String msg) {
        LogCat.e(TAG, "data onFail code=" + code + "," + msg);
        hideLoadingDialog();
    }

    private void showViewList(PlatformInfo data) {
        list = data.getSaasList();
        recyclerView.setAdapter(new CommonListAdapter<PlatformInfo.SaasListBean>(context,
                R.layout.item_merchant_platform, list) {
            int selectedIndex = -1;

            @Override
            public void convert(ViewHolder holder, final PlatformInfo.SaasListBean bean) {
                TextView tvPlatform = holder.getView(R.id.tv_platform);
                tvPlatform.setText(bean.getSaas_name());
                ImageView ivSelect = holder.getView(R.id.iv_select);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        selectPlatform = bean.getSaas_name();
                        selectSaasSource = bean.getSaas_source();
                        notifyDataSetChanged();//刷新
                        isCanClick(true);
                    }
                });
                if (selectedIndex == holder.getAdapterPosition()) {
                    tvPlatform.setTextColor(getResources().getColor(R.color.common_orange));
                    ivSelect.setVisibility(View.VISIBLE);
                } else {
                    tvPlatform.setTextColor(getResources().getColor(R.color.text_color));
                    ivSelect.setVisibility(View.GONE);
                }
            }
        });
    }

}
