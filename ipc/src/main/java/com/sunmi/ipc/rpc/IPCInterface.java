package com.sunmi.ipc.rpc;

/**
 * Description: IPCInterface
 * Created by Bruce on 2019/3/31.
 */
class IPCInterface {
    private final static String IPC_API = "http://47.96.240.44:35150/";

    //绑定
    final static String BIND_IPC = IPC_API + "api/device/bind";

    public final static String CREATE_EMQ_TOKEN = IPC_API + "api/emq/token/create";

}
