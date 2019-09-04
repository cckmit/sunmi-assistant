package com.sunmi.assistant.mine.contract;

import sunmi.common.base.BaseView;

/**
 * @author bruce
 * @date 2019/1/29
 */
public interface ChangeUsernameContract {

    interface View extends BaseView {
        /**
         * UI更新昵称
         *
         * @param name 昵称
         */
        void updateUsernameView(String name);

        /**
         * 获取昵称失败
         */
        void getNameFailed();

        /**
         * 更新昵称成功
         */
        void updateSuccess();
    }

    interface Presenter {

        /**
         * 获取最新昵称
         */
        void getUsername();

        /**
         * 请求更新用户名称
         *
         * @param name 用户名称
         */
        void updateUsername(String name);

    }

}
