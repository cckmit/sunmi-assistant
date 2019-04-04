package sunmi.common.rpc.mqtt;

import sunmi.common.rpc.sunmicall.ResponseBean;

public class MessageManager {

    public static final int REQUEST_CHANNEL = 0x00001;
    public static final int BYPASS_CHANNEL = 0x00002;
    private static volatile MessageManager instance;

    public static MessageManager newInstance() {
        if (instance == null) {
            synchronized (MessageManager.class) {
                if (instance == null)
                    instance = new MessageManager();
            }
        }
        return instance;
    }

    public void notice(ResponseBean response, int channelType) {
        if (channelType == REQUEST_CHANNEL) {
            requestMessage(response);
        } else if (channelType == BYPASS_CHANNEL) {
            requestMessage(response);
        }
    }

    /**
     * Request消息分发
     */
    private void requestMessage(ResponseBean response) {
        MessageHandler noticeHandler = HandlerFactory.getInstance()
                .createNoticeHandler(response);
        if (noticeHandler != null) noticeHandler.handler(response);
    }

}
