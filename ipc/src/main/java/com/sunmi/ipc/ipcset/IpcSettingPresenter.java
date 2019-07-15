package com.sunmi.ipc.ipcset;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public class IpcSettingPresenter extends BasePresenter<IpcSettingContract.View>
        implements IpcSettingContract.Presenter {

    @Override
    public void loadConfig() {
        // TODO: api，拉取摄像头信息
    }

    @Override
    public void updateName(String name) {
        // TODO: api，更改摄像头名称
    }

}
