package com.sunmi.assistant.mine;

import sunmi.common.base.BaseView;

/**
 * @author bruce
 * @date 2019/1/29
 */
public interface MineContract {

    interface View extends BaseView {
        /**
         * 更新界面用户信息
         */
        void updateUserInfo();
    }

    interface Presenter {
        /**
         * 获取用户信息
         */
        void getUserInfo();
    }
}
