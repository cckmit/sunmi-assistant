package com.sunmi.ipc.router;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

import sunmi.common.constant.RouterConfig;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-29.
 */
@RouterApiAnno()
@HostAnno(RouterConfig.SunmiService.NAME)
public interface SunmiServiceApi {

    @PathAnno(RouterConfig.SunmiService.WEB_VIEW_CLOUD)
    void goToWebViewCloud(@ParameterAnno("mUrl") String url, @ParameterAnno("deviceSn") String sn);

    @PathAnno(RouterConfig.SunmiService.SERVICE_DETAIL)
    void goToServiceDetail(@ParameterAnno("mSn") String sn, @ParameterAnno("isBind") boolean isBind, @ParameterAnno("deviceName") String deviceName);
}
