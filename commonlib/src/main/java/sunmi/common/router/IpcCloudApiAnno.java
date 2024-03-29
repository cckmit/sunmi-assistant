package sunmi.common.router;

import java.util.List;

import sunmi.common.model.ServiceResp;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-31.
 */
public interface IpcCloudApiAnno {

    void getDetailList(int companyId, int shopId, RetrofitCallback<IpcListResp> callback);

    void getAuditVideoServiceList(int companyId, int shopId, List<String> snList, RetrofitCallback<ServiceResp> callback);

    void getStorageList(int companyId, int shopId, List<String> snList, RetrofitCallback<ServiceResp> callback);

    void getAuditSecurityPolicyList(int companyId, int shopId, List<String> snList, RetrofitCallback<ServiceResp> callback);
}
