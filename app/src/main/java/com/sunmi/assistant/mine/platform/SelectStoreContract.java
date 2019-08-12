package com.sunmi.assistant.mine.platform;

import com.sunmi.assistant.mine.model.SelectShopModel;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface SelectStoreContract {

    interface View extends BaseView {

        void complete();

        void onFailedShopCreate(int code);

    }

    interface Presenter {

        List<SelectShopModel> getList();

        void createShops(List<SelectShopModel> list);

    }

}
