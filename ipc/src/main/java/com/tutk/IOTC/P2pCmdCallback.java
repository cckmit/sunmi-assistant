package com.tutk.IOTC;

import com.sunmi.ipc.model.IotcCmdResp;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public interface P2pCmdCallback<T> {
    void onResponse(int cmd, IotcCmdResp<T> result);
}
