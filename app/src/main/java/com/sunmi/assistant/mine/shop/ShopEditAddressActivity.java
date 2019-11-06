package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopRegionContract;
import com.sunmi.assistant.mine.model.RegionProvince;
import com.sunmi.assistant.mine.presenter.ShopRegionPresenter;

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
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.SoftKeyboardStateHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * @author yangShiJie
 * @date 2019-09-04
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_store_address)
public class ShopEditAddressActivity extends BaseMvpActivity<ShopRegionPresenter>
        implements ShopRegionContract.View, PoiSearch.OnPoiSearchListener {
    private static final int IPC_MARK_MAX_LENGTH = 100;
    private static final int POI_PAGE_SIZE = 20;
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.sil_address)
    SettingItemLayout silAddress;
    @ViewById(R.id.cet_details_address)
    ClearableEditText cetDetailsAddress;
    @ViewById(R.id.poi_recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.const_layout_poi)
    View constLayoutPoi;
    @ViewById(R.id.v_space)
    View vSpace;
    @ViewById(R.id.tv_transparent)
    TextView tvTransparent;

    @Extra
    ShopInfo mInfo;

    Button btnAreaPro;
    Button btnAreaCity;
    Button btnAreaRegion;
    RecyclerView itemRecyclerView;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private String poiKey = "";
    private String poiCityName = "";

    private Dialog dialog = null;
    private BaseArrayAdapter<Object> mAdapter;
    private List<RegionProvince> mList;
    private int mProvinceId;
    private int mProvinceIndex;
    private int mCityId;
    private int mAreaId;
    private String lat = "", lng = "";

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ShopRegionPresenter(mInfo);
        mPresenter.attachView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        titleBar.getRightText().setOnClickListener(this::save);
        mList = new Gson().fromJson(FileUtils.getStringFromAssets(context, "region.json"),
                new TypeToken<List<RegionProvince>>() {
                }.getType());
        mProvinceId = mInfo.getProvince();
        mCityId = mInfo.getCity();
        mAreaId = mInfo.getArea();
        lat = mInfo.getLat();
        lng = mInfo.getLng();
        poiCityName = cityName(mList);
        if (!TextUtils.isEmpty(mInfo.getRegionName())) {
            silAddress.setRightText(mInfo.getRegionName());
            tvTransparent.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mInfo.getAddress())) {
            cetDetailsAddress.setText(mInfo.getAddress());
        }
        editTextChangedListener();
    }

    @Click(R.id.sil_address)
    void addressClick() {
        showDialog(this);
    }

    private void isShowPoi(boolean hasShow) {
        if (hasShow) {
            cetDetailsAddress.requestFocus();
            silAddress.setVisibility(View.GONE);
            vSpace.setVisibility(View.GONE);
            cetDetailsAddress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            cetDetailsAddress.clearFocus();
            silAddress.setVisibility(View.VISIBLE);
            vSpace.setVisibility(View.VISIBLE);
            cetDetailsAddress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        if (silAddress.getRightText().getText() == null ||
                TextUtils.isEmpty(silAddress.getRightText().getText().toString())) {
            tvTransparent.setVisibility(View.VISIBLE);
        } else {
            tvTransparent.setVisibility(View.GONE);
        }
    }

    public String cityName(List<RegionProvince> list) {
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            RegionProvince province = list.get(i);
            for (RegionProvince.City city : province.getChildren()) {
                if (mInfo.getCity() == city.getCity()) {
                    return city.getName();
                }
            }
        }
        return "";
    }

    @Click(R.id.tv_transparent)
    void clickAddress() {
        if (silAddress.getRightText().getText() == null ||
                TextUtils.isEmpty(silAddress.getRightText().getText().toString())) {
            shortTip(getString(R.string.shop_input_region_tip));
            tvTransparent.setVisibility(View.VISIBLE);
        } else {
            tvTransparent.setVisibility(View.GONE);
            cetDetailsAddress.requestFocus();
        }
    }

    /**
     * 判断软键盘是否隐藏
     */
    private void editTextChangedListener() {
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.const_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
            }

            @Override
            public void onSoftKeyboardClosed() {
                cetDetailsAddress.clearFocus();
                isShowPoi(false);
            }
        });
        cetDetailsAddress.addTextChangedListener(new TextLengthWatcher(cetDetailsAddress, IPC_MARK_MAX_LENGTH) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                poiKey = s.toString();
                if (TextUtils.isEmpty(poiKey)) {
                    lat = "";
                    lng = "";
                }
                doSearchQuery(poiKey);
            }
        });
        //监听键盘按钮事件
        cetDetailsAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                String address = cetDetailsAddress.getText() == null ||
                        TextUtils.isEmpty(cetDetailsAddress.getText().toString())
                        ? "" : cetDetailsAddress.getText().toString();
                if (!TextUtils.equals(address, mInfo.getAddress())) {
                    lat = "";
                    lng = "";
                }
                hideSoftInputWindow();
                return true;
            }
            return false;
        });
        cetDetailsAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSoftInputWindow();
            } else {
                isShowPoi(false);
            }
        });
    }

    private void showSoftInputWindow() {
        if (silAddress.getRightText().getText() == null ||
                TextUtils.isEmpty(silAddress.getRightText().getText().toString())) {
            shortTip(getString(R.string.shop_input_region_tip));
            cetDetailsAddress.clearFocus();
            return;
        }
        isShowPoi(true);
        InputMethodManager inputManager = (InputMethodManager) cetDetailsAddress
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.showSoftInput(cetDetailsAddress, 0);
    }

    private void hideSoftInputWindow() {
        cetDetailsAddress.clearFocus();
        isShowPoi(false);
        InputMethodManager inputManager = (InputMethodManager) cetDetailsAddress
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(cetDetailsAddress.getWindowToken(), 0);
    }

    private void save(View v) {
        String address = cetDetailsAddress.getText() == null || TextUtils.isEmpty(cetDetailsAddress.getText().toString())
                ? "" : cetDetailsAddress.getText().toString();
        if (mProvinceId <= 0 || mCityId <= 0 || mAreaId <= 0) {
            shortTip(getString(R.string.shop_input_region_tip));
        } else if (mInfo.getProvince() == mProvinceId && mInfo.getCity() == mCityId &&
                mInfo.getArea() == mAreaId && TextUtils.equals(mInfo.getAddress(), address)) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (cetDetailsAddress.getText() == null || TextUtils.isEmpty(cetDetailsAddress.getText().toString())) {
            shortTip(R.string.tip_addr_empty);
        } else {
            mInfo.setLat(lat);
            mInfo.setLng(lng);
            mPresenter.updateRegion(mProvinceId, mCityId, mAreaId, address);
        }
    }

    /**
     * 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
     */
    protected void doSearchQuery(String key) {
        if (TextUtils.isEmpty(poiCityName)) {
            return;
        }
        int currentPage = 0;
        query = new PoiSearch.Query(key, "", poiCityName);
        query.setPageSize(POI_PAGE_SIZE);
        query.setPageNum(currentPage);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        List<PoiItem> poiItems = poiResult.getPois();
        showPoiData(poiItems);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void showPoiData(List<PoiItem> poiItems) {
        recyclerView.setAdapter(new CommonListAdapter<PoiItem>(this, R.layout.item_area, poiItems) {
            int index = -1;

            @Override
            public void convert(ViewHolder holder, PoiItem poiItem) {
                TextView name = holder.getView(R.id.tv_item_name);
                TextView address = holder.getView(R.id.tv_item_address);
                name.setText(matcherSearchText(ContextCompat.getColor(context, R.color.common_orange), poiItem.getTitle(), poiKey));
                address.setText(matcherSearchText(ContextCompat.getColor(context, R.color.common_orange), poiItem.getSnippet(), poiKey));
                holder.itemView.setOnClickListener(v -> {
                    index = holder.getAdapterPosition();
                    cetDetailsAddress.setText(poiItem.getSnippet());
                    lat = String.valueOf(poiItem.getLatLonPoint().getLatitude());
                    lng = String.valueOf(poiItem.getLatLonPoint().getLongitude());
                    hideSoftInputWindow();
                    notifyDataSetChanged();
                });
                if (index == holder.getAdapterPosition()) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black_15));
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.c_white));
                }
            }
        });
    }


    public SpannableStringBuilder matcherSearchText(int color, String string, String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return SpannableStringBuilder.valueOf(string);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        int indexOf = string.indexOf(keyWord);
        if (indexOf != -1) {
            builder.setSpan(new ForegroundColorSpan(color), indexOf, indexOf + keyWord.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 初始化
     */
    private void initAddress() {
        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(RegionProvince.class, new ProvinceType());
        mAdapter.register(RegionProvince.City.class, new CityType());
        mAdapter.register(RegionProvince.Area.class, new AreaType());
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerView.setAdapter(mAdapter);

        btnAreaPro.setText(R.string.str_choose_please);
        btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.text_main));
        btnAreaCity.setText("");
        btnAreaRegion.setText("");
        showRegionList(mList);
    }

    public void showDialog(final Context context) {
        dialog = new Dialog(context, R.style.BottomDialog);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_area, null);
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow == null) {
            return;
        }
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height - 300;
        dialogWindow.setAttributes(lp);

        TextView tvCancel = inflate.findViewById(R.id.tv_cancel);
        TextView tvConfirm = inflate.findViewById(R.id.tv_confirm);
        itemRecyclerView = inflate.findViewById(R.id.item_recycler_view);
        btnAreaPro = inflate.findViewById(R.id.btn_area_pro);
        btnAreaCity = inflate.findViewById(R.id.btn_area_city);
        btnAreaRegion = inflate.findViewById(R.id.btn_area_region);
        initAddress();
        View.OnClickListener clickListener = v -> {
            switch (v.getId()) {
                case R.id.btn_area_pro:
                    btnAreaCity.setText("");
                    btnAreaRegion.setText("");
                    mCityId = -1;
                    mAreaId = -1;
                    mAdapter.setData(mList);
                    break;
                case R.id.btn_area_city:
                    btnAreaRegion.setText("");
                    mAreaId = -1;
                    mAdapter.setData(mList.get(mProvinceIndex).getChildren());
                    break;
                case R.id.tv_cancel:
                    dialog.dismiss();
                    mProvinceId = mInfo.getProvince();
                    mCityId = mInfo.getCity();
                    mAreaId = mInfo.getArea();
                    break;
                case R.id.tv_confirm:
                    if (mProvinceId <= 0 || mCityId <= 0 || mAreaId <= 0) {
                        shortTip(getString(R.string.shop_input_region_tip));
                        return;
                    }
                    if (mProvinceId != mInfo.getProvince() || mCityId != mInfo.getCity() || mAreaId != mInfo.getArea()) {
                        cetDetailsAddress.setText("");
                    }
                    dialog.dismiss();
                    tvTransparent.setVisibility(View.GONE);
                    silAddress.setRightText(btnAreaPro.getText().toString() + "," +
                            btnAreaCity.getText().toString() + "," +
                            btnAreaRegion.getText().toString());
                    break;
                default:
                    break;
            }
        };
        btnAreaPro.setOnClickListener(clickListener);
        btnAreaCity.setOnClickListener(clickListener);
        tvCancel.setOnClickListener(clickListener);
        tvConfirm.setOnClickListener(clickListener);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void showRegionList(List<RegionProvince> list) {
        mAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            RegionProvince province = list.get(i);
            if (mProvinceId == province.getProvince()) {
                mProvinceIndex = i;
                itemRecyclerView.scrollToPosition(i);
                btnAreaPro.setText(province.getName());
                btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnAreaCity.setText(R.string.str_choose_please);
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.text_main));
            }
            for (RegionProvince.City city : province.getChildren()) {
                if (mCityId == city.getCity()) {
                    btnAreaCity.setText(city.getName());
                    btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                    btnAreaRegion.setText(R.string.str_choose_please);
                    btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                }
                for (RegionProvince.Area area : city.getChildren()) {
                    if (mAreaId == area.getCounty()) {
                        btnAreaRegion.setText(area.getName());
                        btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
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

    @Override
    public void getRegionFailed() {

    }

    @Override
    public void updateRegionFailed() {

    }

    private class ProvinceType extends ItemType<RegionProvince, BaseViewHolder<RegionProvince>> {

        private ProvinceType() {
            setOnItemClickListener((holder, model, position) -> {
                mProvinceId = model.getProvince();
                mCityId = -1;
                mAreaId = -1;
                btnAreaPro.setText(model.getName());
                btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnAreaCity.setText(R.string.str_choose_please);
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                btnAreaRegion.setText("");
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
                poiCityName = model.getName();
                btnAreaCity.setText(model.getName());
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                btnAreaRegion.setText(R.string.str_choose_please);
                btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
                btnAreaRegion.setText(model.getName());
                btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
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
