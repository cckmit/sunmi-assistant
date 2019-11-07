package com.sunmi.assistant.mine.shop;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopRegionContract;
import com.sunmi.assistant.mine.model.RegionProvince;
import com.sunmi.assistant.mine.presenter.ShopRegionPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-08
 */
@EActivity(R.layout.activity_mine_area)
public class ShopRegionActivity extends BaseMvpActivity<ShopRegionPresenter>
        implements ShopRegionContract.View {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.btnArea1)
    Button btnArea1;
    @ViewById(R.id.btnArea2)
    Button btnArea2;
    @ViewById(R.id.btnArea3)
    Button btnArea3;

    private BaseArrayAdapter<Object> mAdapter;

    @Extra
    ShopInfo mInfo;
    private List<RegionProvince> mList;

    private int mProvinceId;
    private int mProvinceIndex;
    private int mCityId;
    private int mAreaId;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getRightText().setOnClickListener(this::save);
        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(RegionProvince.class, new ProvinceType());
        mAdapter.register(RegionProvince.City.class, new CityType());
        mAdapter.register(RegionProvince.Area.class, new AreaType());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mProvinceId = mInfo.getProvince();
        mCityId = mInfo.getCity();
        mAreaId = mInfo.getArea();

        btnArea1.setText(R.string.str_choose_please);
        btnArea1.setTextColor(ContextCompat.getColor(context, R.color.text_main));
        btnArea2.setText("");
        btnArea3.setText("");

        mPresenter = new ShopRegionPresenter(mInfo);
        mPresenter.attachView(this);
        getRegion();
        showLoadingDialog();
    }

    @Click(R.id.btnArea1)
    public void clickProvince() {
        btnArea2.setText("");
        btnArea3.setText("");
        mCityId = -1;
        mAreaId = -1;
        mAdapter.setData(mList);
    }

    @Click(R.id.btnArea2)
    public void clickCity() {
        btnArea3.setText("");
        mAreaId = -1;
        mAdapter.setData(mList.get(mProvinceIndex).getChildren());
    }

    @Background
    void getRegion() {
        mList = new Gson().fromJson(FileUtils.getStringFromAssets(context, "region.json"),
                new TypeToken<List<RegionProvince>>() {
                }.getType());
        showRegionList(mList);
    }

    @Override
    @UiThread
    public void showRegionList(List<RegionProvince> list) {
        hideLoadingDialog();
        mAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            RegionProvince province = list.get(i);
            if (mProvinceId == province.getProvince()) {
                mProvinceIndex = i;
                recyclerView.scrollToPosition(i);
                btnArea1.setText(province.getName());
                btnArea1.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnArea2.setText(R.string.str_choose_please);
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.text_main));
            }
            for (RegionProvince.City city : province.getChildren()) {
                if (mCityId == city.getCity()) {
                    btnArea2.setText(city.getName());
                    btnArea2.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                    btnArea3.setText(R.string.str_choose_please);
                    btnArea3.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                }
                for (RegionProvince.Area area : city.getChildren()) {
                    if (mAreaId == area.getCounty()) {
                        btnArea3.setText(area.getName());
                        btnArea3.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                    }
                }
            }
        }
    }

    @Override
    public void complete() {
        hideLoadingDialog();
        setResult(RESULT_OK);
        finish();
    }

    private void save(View v) {
        if (mProvinceId <= 0 || mCityId <= 0 || mAreaId <= 0) {
            shortTip(R.string.str_selected);
        } else if (mInfo.getProvince() == mProvinceId && mInfo.getCity() == mCityId && mInfo.getArea() == mAreaId) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            mPresenter.updateRegion(mProvinceId, mCityId, mAreaId, "");
        }
    }

    @Override
    public void getRegionFailed() {
        hideLoadingDialog();
    }

    @Override
    public void updateRegionFailed() {
        hideLoadingDialog();
        shortTip(R.string.tip_save_fail);
    }

    private class ProvinceType extends ItemType<RegionProvince, BaseViewHolder<RegionProvince>> {

        private ProvinceType() {
            setOnItemClickListener((holder, model, position) -> {
                mProvinceId = model.getProvince();
                mCityId = -1;
                mAreaId = -1;
                btnArea1.setText(model.getName());
                btnArea1.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnArea2.setText(R.string.str_choose_please);
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                btnArea3.setText("");
                mAdapter.setData(model.getChildren());
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<RegionProvince> holder, RegionProvince model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mProvinceId == model.getProvince()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private class CityType extends ItemType<RegionProvince.City, BaseViewHolder<RegionProvince.City>> {

        private CityType() {
            setOnItemClickListener((holder, model, position) -> {
                mCityId = model.getCity();
                mAreaId = -1;
                btnArea2.setText(model.getName());
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnArea3.setText(R.string.str_choose_please);
                btnArea3.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                mAdapter.setData(model.getChildren());
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<RegionProvince.City> holder, RegionProvince.City model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mCityId == model.getCity()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private class AreaType extends ItemType<RegionProvince.Area, BaseViewHolder<RegionProvince.Area>> {

        private AreaType() {
            setOnItemClickListener((holder, model, position) -> {
                mAreaId = model.getCounty();
                btnArea3.setText(model.getName());
                btnArea3.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                getAdapter().notifyDataSetChanged();
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<RegionProvince.Area> holder, RegionProvince.Area model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mAreaId == model.getCounty()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
