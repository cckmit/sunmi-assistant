package com.sunmi.assistant.mine;

import java.io.File;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/1/24.
 */
public interface UserInfoContract {

    interface View extends BaseView {
        /**
         * UI更新头像
         *
         * @param url 头像URL
         */
        void updateAvatarView(String url);
    }

    interface Presenter {
        /**
         * 上传头像
         *
         * @param file 头像图片文件
         */
        void updateAvatar(File file);
    }

}
