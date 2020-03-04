package com.sunmi.assistant.dashboard.subpage;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.SmRecyclerView;

@EActivity(R.layout.activity_customer_distribution)
public class CustomerDistributionActivity extends BaseMvpActivity {

    @ViewById(R.id.dm_motion_customer)
    DropdownMenuNew dmMotionSale;
    @ViewById(R.id.dropdown_title)
    TextView dropdownTitle;
    @ViewById(R.id.dropdown_img)
    ImageView dropdownImg;
    @ViewById(R.id.rv_rank)
    SmRecyclerView rvRank;
    @ViewById(R.id.layout_refresh)
    BGARefreshLayout refreshView;
    @ViewById(R.id.layout_error)
    View layoutError;
    @ViewById(R.id.rb_new_old)
    RadioButton rbNewOld;
    @ViewById(R.id.rb_gender)
    RadioButton rbGender;
    @ViewById(R.id.rb_age)
    RadioButton rbAge;

    @AfterViews
    void init(){

    }
}
