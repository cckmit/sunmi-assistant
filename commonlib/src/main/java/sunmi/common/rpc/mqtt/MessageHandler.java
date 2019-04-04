package sunmi.common.rpc.mqtt;

import sunmi.common.rpc.sunmicall.ResponseBean;

public abstract class MessageHandler {

    protected static final String TAG = MessageHandler.class.getSimpleName();

    /**
     * 处理方法
     */
    protected abstract void handler(ResponseBean response);

}
