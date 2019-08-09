package com.sunmi.assistant.mine.shop;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.ShopInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ShopRegionResp;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
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
    private List<ShopRegionResp.Province> mList;

    private int mProvinceId;
    private int mProvinceIndex;
    private int mCityId;
    private int mAreaId;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getRightText().setOnClickListener(this::save);
        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(ShopRegionResp.Province.class, new ProvinceType());
        mAdapter.register(ShopRegionResp.City.class, new CityType());
        mAdapter.register(ShopRegionResp.Area.class, new AreaType());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mProvinceId = mInfo.getProvince();
        mCityId = mInfo.getCity();
        mAreaId = mInfo.getArea();

        btnArea1.setText(R.string.str_choose_please);
        btnArea1.setTextColor(ContextCompat.getColor(context, R.color.colorText));
        btnArea2.setText("");
        btnArea3.setText("");

        mPresenter = new ShopRegionPresenter(mInfo);
        mPresenter.attachView(this);
        mPresenter.getRegion();
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

    @Override
    public void showRegionList(List<ShopRegionResp.Province> list) {
        hideLoadingDialog();
        mList = list;
        mAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            ShopRegionResp.Province province = list.get(i);
            if (province.getProvince() <= 0) {
                LogCat.d(TAG, "----" + province.getName());
            }
            if (mProvinceId == province.getProvince()) {
                mProvinceIndex = i;
                recyclerView.scrollToPosition(i);
                btnArea1.setText(province.getName());
                btnArea1.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnArea2.setText(R.string.str_choose_please);
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.colorText));
            }
            for (ShopRegionResp.City city : province.getChildren()) {
                if (city.getCity() <= 0) {
                    LogCat.d(TAG, "----" + province.getName());
                }
                if (mCityId == city.getCity()) {
                    btnArea2.setText(city.getName());
                    btnArea2.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                    btnArea3.setText(R.string.str_choose_please);
                    btnArea3.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                }
                for (ShopRegionResp.Area area : city.getChildren()) {
                    if (area.getCounty() <= 0) {
                        LogCat.d(TAG, "----" + province.getName());
                    }
                    if (mAreaId == area.getCounty()) {
                        btnArea2.setText(area.getName());
                        btnArea2.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
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
            mPresenter.updateRegion(mProvinceId, mCityId, mAreaId);
        }
    }

    @Override
    public void getRegionFailed() {
        hideLoadingDialog();
    }

    @Override
    public void updateRegionFailed() {
        hideLoadingDialog();
    }

    private class ProvinceType extends ItemType<ShopRegionResp.Province, BaseViewHolder<ShopRegionResp.Province>> {

        private ProvinceType() {
            setOnItemClickListener((adapter, holder, model, position) -> {
                mProvinceId = model.getProvince();
                mCityId = -1;
                mAreaId = -1;
                btnArea1.setText(model.getName());
                btnArea1.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnArea2.setText(R.string.str_choose_please);
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                btnArea3.setText("");
                mAdapter.setData(model.getChildren());
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<ShopRegionResp.Province> holder, ShopRegionResp.Province model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mProvinceId == model.getProvince()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private class CityType extends ItemType<ShopRegionResp.City, BaseViewHolder<ShopRegionResp.City>> {

        private CityType() {
            setOnItemClickListener((adapter, holder, model, position) -> {
                mCityId = model.getCity();
                mAreaId = -1;
                btnArea2.setText(model.getName());
                btnArea2.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnArea3.setText(R.string.str_choose_please);
                btnArea3.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                mAdapter.setData(model.getChildren());
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<ShopRegionResp.City> holder, ShopRegionResp.City model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mCityId == model.getCity()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private class AreaType extends ItemType<ShopRegionResp.Area, BaseViewHolder<ShopRegionResp.Area>> {

        private AreaType() {
            setOnItemClickListener((adapter, holder, model, position) -> {
                mAreaId = model.getCounty();
                btnArea3.setText(model.getName());
                btnArea3.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                onBindViewHolder(holder, model, position);
            });
        }

        @Override
        public int getLayoutId(int type) {
            return R.layout.item_mine_area;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<ShopRegionResp.Area> holder, ShopRegionResp.Area model, int position) {
            TextView tvArea = holder.getView(R.id.tvArea);
            ImageView imageView = holder.getView(R.id.ivImg);
            final String name = model.getName();
            tvArea.setText(name);
            if (mAreaId == model.getCounty()) {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
