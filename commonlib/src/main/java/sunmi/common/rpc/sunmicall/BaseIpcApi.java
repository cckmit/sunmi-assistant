package sunmi.common.rpc.sunmicall;

import android.content.Context;

public abstract class BaseIpcApi {

    public abstract void post(Context context, String sn, String msgId, int opCode, String json);

    public abstract void post(Context context, String sn, String msgId, int opCode, String model, String json);

}
