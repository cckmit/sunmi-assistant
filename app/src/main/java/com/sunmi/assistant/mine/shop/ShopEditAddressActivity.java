package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019-09-04
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_store_address)
public class ShopEditAddressActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    @AfterViews
    void init() {
        doSearchQuery("餐馆");
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String key) {
        int currentPage = 0;
        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(key, "", "浦东新区");
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查询页码
        query.setPageNum(currentPage);

        //构造 PoiSearch 对象，并设置监听
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (1000 == AMapException.CODE_AMAP_SUCCESS) {
            List<PoiItem> poiItems = poiResult.getPois();
            for (int j = 0; j < poiItems.size(); j++) {
                LogCat.e(TAG, "kk =" + poiItems.get(j).getSnippet() + " , " + poiItems.get(j).getTitle());

            }
        }


    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
