package com.sunmi.ipc.rpc;

/**
 * Description: IPCInterface
 * Created by Bruce on 2019/3/31.
 */
class IPCInterface {
    //    private final static String IPC_API = "http://47.96.240.44:35150/";
    private final static String IPC_API = "http://121.196.219.63:3000/";
    //绑定
    final static String BIND_IPC = IPC_API + "api/device/bind";
    //解绑
    final static String UNBIND_IPC = IPC_API + "api/device/unbind";
    //获取用户指定店铺下的摄像头列表
    final static String GET_IPC_LIST = IPC_API + "api/device/ipc/getList";

    public final static String CREATE_EMQ_TOKEN = IPC_API + "api/emq/token/create";

}
