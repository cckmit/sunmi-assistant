package com.sunmi.assistant.mine.contract;

import sunmi.common.base.BaseView;

/**
 * @author bruce
 * @date 2019/6/6
 */
public interface ChangeCompanyNameContract {

    interface View extends BaseView {
        /**
         * UI更新商户名称
         *
         * @param name 商户名称
         */
        void updateNameView(String name);

        /**
         * 获取商户名称失败
         */
        void getNameFailed();

        /**
         * 更新名称成功
         */
        void updateSuccess();

    }

    interface Presenter {
        /**
         * 获取商户名称
         */
        void getCompanyInfo();

        /**
         * 更新商户名称
         *
         * @param name 商户名称
         */
        void updateCompanyName(String name);

    }
}
