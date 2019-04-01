package com.sunmi.ipc;

/**
 * Description: IPCInterface
 * Created by Bruce on 2019/3/31.
 */
class IPCInterface {
    private final static String WEB_URL = "https://uat.webapi.sunmi.com/webapi/wap";

    private final static String IPC_API = WEB_URL + "/api/sso/app/sso/1.0/?service="; //ipc api todo

    //绑定
    final static String BIND_IPC = IPC_API + "/sendcode";//TODO

}
