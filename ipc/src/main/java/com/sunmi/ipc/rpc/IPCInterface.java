package com.sunmi.ipc.rpc;

import com.sunmi.ipc.config.IpcConfig;

/**
 * Description: IPCInterface
 * Created by Bruce on 2019/3/31.
 */
class IPCInterface {
    //绑定
    final static String BIND_IPC = IpcConfig.IPC_CLOUD_URL + "api/device/bind";
    //解绑
    final static String UNBIND_IPC = IpcConfig.IPC_CLOUD_URL + "api/device/unbind";
    //获取用户指定店铺下的摄像头列表
    final static String GET_IPC_LIST = IpcConfig.IPC_CLOUD_URL + "api/device/ipc/getList";

    public final static String CREATE_EMQ_TOKEN = IpcConfig.IPC_CLOUD_URL + "api/emq/token/create";

}
