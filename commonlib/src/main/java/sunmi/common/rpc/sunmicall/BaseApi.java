package sunmi.common.rpc.sunmicall;

import android.content.Context;

public abstract class BaseApi {

    public abstract void post(Context context, String sn, String msgId, int opCode, String json);

}
