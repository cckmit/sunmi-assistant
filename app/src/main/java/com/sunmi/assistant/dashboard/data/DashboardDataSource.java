package com.sunmi.assistant.dashboard.data;

import java.util.List;

import sunmi.common.model.CompanyIpcListResp;
import sunmi.common.model.SaasStatus;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.model.ShopInfo;
import sunmi.common.router.model.IpcListResp;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardDataSource {

    List<ShopInfo> getShopList(int companyId);

    List<CompanyIpcListResp.ShopIpc> getShopIpcList(int companyId);

    List<SaasStatus> getSaasStatus(int companyId);

    List<SaasStatus> getSaasStatus(int companyId, int shopId);

    List<IpcListResp.SsListBean> getFsDevices(int companyId, int shopId);

    int getCustomer(int companyId, int shopId);

    ShopBundledCloudInfo getBundledList(int companyId, int shopId);
}
