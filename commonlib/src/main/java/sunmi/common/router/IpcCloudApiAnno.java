package sunmi.common.router;

import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-31.
 */
public interface IpcCloudApiAnno {

    void getDetailList(int companyId, int shopId, RetrofitCallback<IpcListResp> callback);
}
