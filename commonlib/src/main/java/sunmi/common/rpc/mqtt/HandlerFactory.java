package sunmi.common.rpc.mqtt;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sunmi.common.rpc.sunmicall.ResponseBean;

public class HandlerFactory {

    private static volatile HandlerFactory instance;

    private static Map<String, MessageHandler> handlers = new ConcurrentHashMap<>(23);

    private HandlerFactory() {
        init();
    }

    public static HandlerFactory getInstance() {
        if (instance == null) {
            synchronized (HandlerFactory.class) {
                if (instance == null)
                    instance = new HandlerFactory();
            }
        }
        return instance;
    }

    private void init() {

    }

    /**
     * @param response 消息
     * @return 对应的handler
     */
    MessageHandler createNoticeHandler(ResponseBean response) {
        for (Map.Entry<String, MessageHandler> map : handlers.entrySet()) {
            String noticeType = map.getKey();
            if (TextUtils.equals(response.getOpcode(), noticeType)) {
                return map.getValue();
            }
        }
        return null;
    }

}
