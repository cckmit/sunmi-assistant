package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopCreateContract;
import com.sunmi.assistant.mine.model.RegionProvince;
import com.sunmi.assistant.mine.presenter.ShopCreatePresenter;
import com.sunmi.assistant.utils.GetUserInfoUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Locale;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.NumberValueFilter;
import sunmi.common.utils.SoftKeyboardStateHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemEdittextLayout;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * @author yangShiJie
 * @date 2019/7/31
 */
@SuppressLint("Registered")
@EActivity(R.layout.company_activity_shop_create)
public class CreateShopActivity extends BaseMvpActivity<ShopCreatePresenter>
        implements ShopCreateContract.View, PoiSearch.OnPoiSearchListener {

    private static final int CREATE_SHOP_ALREADY_EXIST = 5035;
    private static final int SHOP_NAME_MAX_LENGTH = 40;
    private static final int POI_PAGE_SIZE = 20;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.sel_shop_name)
    SettingItemEdittextLayout etShopName;
    @ViewById(R.id.sel_shop_name_poi)
    SettingItemEdittextLayout selShopNamePoi;
    @ViewById(R.id.et_detail_address)
    ClearableEditText etDetailAddress;
    @ViewById(R.id.et_shop_square)
    ClearableEditText etShopSquare;
    @ViewById(R.id.sel_contact)
    SettingItemEdittextLayout etContact;
    @ViewById(R.id.sel_tel)
    SettingItemEdittextLayout etTel;
    @ViewById(R.id.tv_region_text)
    TextView tvRegionText;
    @ViewById(R.id.tv_category_text)
    TextView tvCategoryText;
    @ViewById(R.id.poi_recyclerView)
    RecyclerView recyclerViewPoi;
    @ViewById(R.id.tv_name_transparent)
    TextView tvNameTransparent;
    @ViewById(R.id.tv_address_transparent)
    TextView tvAddressTransparent;
    @ViewById(R.id.group)
    Group group;
    @ViewById(R.id.group_poi)
    Group groupPoi;

    @Extra
    int companyId;
    @Extra
    String companyName;
    @Extra
    int saasExist;
    @Extra
    boolean isLoginSuccessSwitchCompany;
    //区域
    private Button btnAreaPro, btnAreaCity, btnAreaRegion;
    private RecyclerView recyclerViewRegion;
    private BaseArrayAdapter<Object> mAdapter;
    private List<RegionProvince> mList;
    private int mProvinceId, mCityId, mAreaId, mProvinceIndex;
    //poi搜索
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private String poiKey = "";
    private String poiCityName = "";
    //品类
    private int mCategoryLeftCode, mCategoryRightCode;
    private String mCategoryLeftName, mCategoryRightName;
    private LeftCategoryAdapter mLeftAdapter;
    private RightCategoryAdapter mRightAdapter;

    private boolean isSetRecyclerViewPoiHeight;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ShopCreatePresenter();
        mPresenter.attachView(this);
        initSet();
        poiSearchShopName();
    }

    private void initSet() {
        recyclerViewPoi.setLayoutManager(new LinearLayoutManager(this));
        //门店面积
        etShopSquare.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etShopSquare.setFilters(new InputFilter[]{new NumberValueFilter()});
        //区域列表
        mList = new Gson().fromJson(FileUtils.getStringFromAssets(context, "region.json"),
                new TypeToken<List<RegionProvince>>() {
                }.getType());
    }

    /**
     * 区域
     */
    @Click(R.id.rl_region)
    void regionClick() {
        showRegionDialog(this);
    }

    /**
     * 区域是否已选
     */
    @Click({R.id.tv_name_transparent, R.id.tv_address_transparent})
    void nameClick() {
        if (TextUtils.isEmpty(tvRegionText.getText().toString())) {
            shortTip(getString(R.string.shop_input_region_tip));
        }
    }

    /**
     * 品类
     */
    @Click(R.id.rl_category)
    void categoryClick() {
        mPresenter.getCategory();
    }

    /**
     * 创建门店
     */
    @Click(R.id.btn_complete)
    void completeClick() {
        String shopRegion = tvRegionText.getText() == null ? null : tvRegionText.getText().toString().trim();
        String shopName = etShopName.getEditTextText().getText() == null ? null : etShopName.getEditTextText().getText().toString().trim();
        if (TextUtils.isEmpty(shopRegion)) {
            shortTip(getString(R.string.shop_input_region_tip));
            return;
        }
        if (TextUtils.isEmpty(shopName)) {
            shortTip(R.string.company_shop_create_hint);
            return;
        }
        String address = etDetailAddress.getText() == null ? "" : etDetailAddress.getText().toString().trim();
        String square = etShopSquare.getText() == null ? "" : etShopSquare.getText().toString().trim();
        String contact = etContact.getRightText().getText() == null ? "" : etContact.getRightText().getText().toString().trim();
        String tel = etTel.getRightText().getText() == null ? "" : etTel.getRightText().getText().toString().trim();
        //create
        mPresenter.createShop(shopName, mProvinceId, mCityId, mAreaId, address,
                mCategoryLeftCode, mCategoryRightCode,
                TextUtils.isEmpty(square) ? 0 : Float.parseFloat(square), contact, tel);
    }

    /**
     * 获取焦点
     *
     * @param v        EditText
     * @param hasFocus hasFocus
     */
    @FocusChange(R.id.et_content)
    void poiSearchFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            group.setVisibility(View.GONE);
            groupPoi.setVisibility(View.VISIBLE);
            selShopNamePoi.getEditTextText().requestFocus();
            InputMethodManager inputManager = (InputMethodManager) selShopNamePoi.getEditTextText()
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.showSoftInput(selShopNamePoi.getEditTextText(), 0);
        }
    }

    /**
     * 品类列表
     *
     * @param list list
     */
    @Override
    public void showCategoryList(List<ShopCategoryResp.ShopTypeListBean> list) {
        showCategoryDialog(this, list);
    }

    /**
     * 获取品类列表失败
     */
    @Override
    public void getCategoryFailed() {

    }

    /**
     * 门店创建成功
     *
     * @param resp result
     */
    @Override
    public void createShopSuccess(CreateShopInfo resp) {
        shortTip(getString(R.string.company_shop_create_success));
        if (SpUtils.isLoginSuccess()) {
            BaseNotification.newInstance().postNotificationName(CommonNotifications.shopCreate);
            if (isLoginSuccessSwitchCompany) {
                CommonHelper.saveCompanyShopInfo(companyId, companyName, saasExist, resp.getShop_id(), resp.getShop_name());
                GotoActivityUtils.gotoMainActivityClearTask(context);
            } else {
                setResult(RESULT_OK);
            }
            finish();
        } else {
            GetUserInfoUtils.userInfo(this, companyId, companyName, saasExist, resp.getShop_id(), resp.getShop_name());
        }
    }


    /**
     * 门店创建失败
     *
     * @param code code
     * @param msg  mag
     */
    @Override
    public void createShopFail(int code, String msg) {
        if (code == CREATE_SHOP_ALREADY_EXIST) {
            shortTip(R.string.str_create_store_alredy_exit);
        } else {
            shortTip(R.string.str_create_store_fail);
        }
    }

    /**
     * 门店名称 高德搜索poi搜索
     */
    private void poiSearchShopName() {
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.const_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                if (!isSetRecyclerViewPoiHeight && selShopNamePoi.getEditTextText().hasFocus()) {
                    isSetRecyclerViewPoiHeight = true;
                    recyclerViewPoi.getLayoutParams().height = CommonHelper.getScreenHeight(context) -
                            (int) context.getResources().getDimension(R.dimen.dp_48) - keyboardHeightInPx;
                }
            }

            @Override
            public void onSoftKeyboardClosed() {
                if (group.getVisibility() == View.VISIBLE) {
                    return;
                }
                group.setVisibility(View.VISIBLE);
                groupPoi.setVisibility(View.GONE);
                etShopName.setEditTextText(selShopNamePoi.getEditTextText().getText().toString());
            }
        });
        //字数统计
        selShopNamePoi.getEditTextText().addTextChangedListener(new TextLengthWatcher(selShopNamePoi.getEditTextText(), SHOP_NAME_MAX_LENGTH) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                selShopNamePoi.setRightText(String.format(Locale.getDefault(), "%d/40", s.length()));
                poiKey = s.toString();
                doSearchQuery(poiKey);
            }
        });
    }


    /**
     * 第一个参数表示搜索字符串
     * 第二个参数表示poi搜索类型
     * 第三个参数表示poi搜索区域（空字符串代表全国）
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
        recyclerViewPoi.setAdapter(new CommonListAdapter<PoiItem>(this, R.layout.item_area, poiItems) {
            int index = -1;

            @Override
            public void convert(ViewHolder holder, PoiItem poiItem) {
                TextView name = holder.getView(R.id.tv_item_name);
                TextView address = holder.getView(R.id.tv_item_address);
                name.setText(matcherSearchText(ContextCompat.getColor(context, R.color.common_orange), poiItem.getTitle(), poiKey));
                address.setText(matcherSearchText(ContextCompat.getColor(context, R.color.common_orange), poiItem.getSnippet(), poiKey));
                holder.itemView.setOnClickListener(v -> {
                    index = holder.getAdapterPosition();
                    selShopNamePoi.setEditTextText(poiItem.getTitle());
                    HelpUtils.setSelectionEnd(selShopNamePoi.getEditTextText());
                    etShopName.setEditTextText(poiItem.getTitle());
                    etDetailAddress.setText(poiItem.getSnippet());

                    notifyDataSetChanged();
                });
                if (index == holder.getAdapterPosition()) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black_15));
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
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
     * 经营品类
     */

    public void showCategoryDialog(final Context context, List<ShopCategoryResp.ShopTypeListBean> list) {
        Dialog dialog = new Dialog(context, R.style.BottomDialog);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_category, null);
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
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height - 300;
        dialogWindow.setAttributes(lp);

        TextView tvCancel = inflate.findViewById(R.id.tv_cancel);
        TextView tvConfirm = inflate.findViewById(R.id.tv_confirm);
        RecyclerView itemRecyclerViewLeft = inflate.findViewById(R.id.item_recycler_view_left);
        RecyclerView itemRecyclerViewRight = inflate.findViewById(R.id.item_recycler_view_right);
        setRecyclerViewData(itemRecyclerViewLeft, itemRecyclerViewRight, list);
        View.OnClickListener clickListener = v -> {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    dialog.dismiss();
                    break;
                case R.id.tv_confirm:
                    if (TextUtils.isEmpty(mCategoryLeftName) || TextUtils.isEmpty(mCategoryRightName)) {
                        shortTip(getString(R.string.company_shop_select_category));
                        return;
                    }
                    tvCategoryText.setText(String.format("%s %s", mCategoryLeftName, mCategoryRightName));
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        };
        tvCancel.setOnClickListener(clickListener);
        tvConfirm.setOnClickListener(clickListener);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void setRecyclerViewData(RecyclerView itemRecyclerViewLeft, RecyclerView itemRecyclerViewRight,
                                     List<ShopCategoryResp.ShopTypeListBean> list) {
        mLeftAdapter = new LeftCategoryAdapter(this);
        itemRecyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerViewLeft.setAdapter(mLeftAdapter);
        mRightAdapter = new RightCategoryAdapter(this);
        itemRecyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerViewRight.setAdapter(mRightAdapter);

        mLeftAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            ShopCategoryResp.ShopTypeListBean type1 = list.get(i);
            if (mCategoryLeftCode == type1.getId()) {
                itemRecyclerViewLeft.scrollToPosition(i);
                List<ShopCategoryResp.ShopTypeListBean.ChildBean> subList = type1.getChild();
                for (int j = 0, size2 = subList.size(); j < size2; j++) {
                    ShopCategoryResp.ShopTypeListBean.ChildBean type2 = subList.get(j);
                    if (mCategoryRightCode == type2.getId()) {
                        mRightAdapter.setData(subList);
                        itemRecyclerViewRight.scrollToPosition(j);
                    }
                }
            }
        }
    }

    /**
     * 地区选择
     */
    private void showRegionDialog(final Context context) {
        Dialog dialog = new Dialog(context, R.style.BottomDialog);
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
        recyclerViewRegion = inflate.findViewById(R.id.item_recycler_view);
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
                    break;
                case R.id.tv_confirm:
                    dialog.dismiss();
                    tvRegionText.setText(String.format("%s %s %s", btnAreaPro.getText().toString(),
                            btnAreaCity.getText().toString(), btnAreaRegion.getText().toString()));
                    tvNameTransparent.setVisibility(View.GONE);
                    tvAddressTransparent.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        };
        btnAreaPro.setOnClickListener(clickListener);
        btnAreaCity.setOnClickListener(clickListener);
        tvCancel.setOnClickListener(clickListener);
        tvConfirm.setOnClickListener(clickListener);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void initAddress() {
        mAdapter = new BaseArrayAdapter<>();
        mAdapter.register(RegionProvince.class, new ProvinceType());
        mAdapter.register(RegionProvince.City.class, new CityType());
        mAdapter.register(RegionProvince.Area.class, new AreaType());
        recyclerViewRegion.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRegion.setAdapter(mAdapter);

        btnAreaPro.setText(R.string.str_choose_please);
        btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.colorText));
        btnAreaCity.setText("");
        btnAreaRegion.setText("");

        showRegionList(mList);
    }

    public void showRegionList(List<RegionProvince> list) {
        mAdapter.setData(list);
        for (int i = 0, size1 = list.size(); i < size1; i++) {
            RegionProvince province = list.get(i);
            if (mProvinceId == province.getProvince()) {
                mProvinceIndex = i;
                recyclerViewRegion.scrollToPosition(i);
                btnAreaPro.setText(province.getName());
                btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnAreaCity.setText(R.string.str_choose_please);
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.colorText));
            }
            for (RegionProvince.City city : province.getChildren()) {
                if (mCityId == city.getCity()) {
                    btnAreaCity.setText(city.getName());
                    btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                    btnAreaRegion.setText(R.string.str_choose_please);
                    btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                }
                for (RegionProvince.Area area : city.getChildren()) {
                    if (mAreaId == area.getCounty()) {
                        btnAreaRegion.setText(area.getName());
                        btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                    }
                }
            }
        }
    }

    private class LeftCategoryAdapter extends CommonListAdapter<ShopCategoryResp.ShopTypeListBean> {

        /**
         * @param context 上下文
         */
        LeftCategoryAdapter(Context context) {
            super(context, R.layout.item_mine_category_left, null);
        }

        @Override
        public void convert(final ViewHolder holder, final ShopCategoryResp.ShopTypeListBean model) {
            TextView textView = holder.getView(R.id.tvName);
            RelativeLayout rlLeft = holder.getView(R.id.rlLeft);
            View vLine = holder.getView(R.id.v_line);
            vLine.setVisibility(View.GONE);
            final String name = model.getName();
            textView.setText(name);
            holder.itemView.setOnClickListener(v -> {
                mCategoryLeftCode = model.getId();
                mCategoryLeftName = name;
                mCategoryRightName = "";
                mCategoryRightCode = -1;
                notifyDataSetChanged();
                mRightAdapter.setData(model.getChild());
            });
            if (mCategoryLeftCode == model.getId()) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.color_FF6000));
                rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_FAFAFA));
            } else {
                rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.c_white));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
            }
        }
    }

    private class RightCategoryAdapter extends CommonListAdapter<ShopCategoryResp.ShopTypeListBean.ChildBean> {

        /**
         * @param context 上下文
         */
        RightCategoryAdapter(Context context) {
            super(context, R.layout.item_mine_category_left, null);
        }

        @Override
        public void convert(ViewHolder holder, ShopCategoryResp.ShopTypeListBean.ChildBean model) {
            TextView textView = holder.getView(R.id.tvName);
            RelativeLayout rlLeft = holder.getView(R.id.rlLeft);
            View vLine = holder.getView(R.id.v_line);
            vLine.setVisibility(View.GONE);
            rlLeft.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_FAFAFA));
            final String nameRight = model.getName();
            textView.setText(nameRight);
            holder.itemView.setOnClickListener(v -> {
                mCategoryRightCode = model.getId();
                mCategoryRightName = nameRight;
                notifyDataSetChanged();
            });
            if (mCategoryRightCode == model.getId()) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.color_FF6000));
            } else {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
            }
        }
    }

    private class ProvinceType extends ItemType<RegionProvince, BaseViewHolder<RegionProvince>> {

        private ProvinceType() {
            setOnItemClickListener((holder, model, position) -> {
                mProvinceId = model.getProvince();
                mCityId = -1;
                mAreaId = -1;
                btnAreaPro.setText(model.getName());
                btnAreaPro.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnAreaCity.setText(R.string.str_choose_please);
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.colorText));
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
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
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
                btnAreaCity.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                btnAreaRegion.setText(R.string.str_choose_please);
                btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.colorText));
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
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private class AreaType extends ItemType<RegionProvince.Area, BaseViewHolder<RegionProvince.Area>> {

        private AreaType() {
            setOnItemClickListener((holder, model, position) -> {
                mAreaId = model.getCounty();
                btnAreaRegion.setText(model.getName());
                btnAreaRegion.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
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
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.color_FF6000));
                imageView.setVisibility(View.VISIBLE);
            } else {
                tvArea.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                imageView.setVisibility(View.GONE);
            }
        }
    }

}
