package com.sunmi.assistant.ui.activity.merchant;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

    private List<PlatformInfo> list = new ArrayList<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initRecycler();
        mPresenter = new PlatformPresenter();
        mPresenter.attachView(this);
        mPresenter.getPlatformInfo();

        showViewList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_right:
                shortTip("skip");
                break;
        }
    }

    @Click({R.id.btn_next})
    void btnNext() {
        CheckPlatformMobileActivity_.intent(this).start();
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        titleBar.getLeftImg().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
    }

    private void showViewList() {
        PlatformInfo bean;
        for (int i = 0; i < 5; i++) {
            bean = new PlatformInfo();
            bean.setMsg("大商新玛特");
            list.add(bean);
        }
        recyclerView.setAdapter(new CommonListAdapter<PlatformInfo>(this, R.layout.item_merchant_platform, list) {
            int selectedIndex = -1;

            @Override
            public void convert(ViewHolder holder, final PlatformInfo bean) {
                TextView tvPlatform = holder.getView(R.id.tv_platform);
                tvPlatform.setText(bean.getMsg());
                ImageView ivSelect = holder.getView(R.id.iv_select);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedIndex = holder.getAdapterPosition();
                        notifyDataSetChanged();//刷新
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

    @Override
    public void getPlatformInfoSuccess(String data) {
        LogCat.e(TAG, "bean=" + data);

    }

    @Override
    public void getPlatformInfoFail(int code, String msg) {

    }

}
