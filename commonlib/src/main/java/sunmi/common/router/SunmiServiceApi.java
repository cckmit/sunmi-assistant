package sunmi.common.router;

import android.content.Context;
import android.content.Intent;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.AfterActionAnno;
import com.xiaojinzi.component.anno.router.FlagAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;
import com.xiaojinzi.component.support.Action;

import java.util.ArrayList;

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
    void goToWebViewCloud(Context context, @ParameterAnno("mUrl") String url, @ParameterAnno("snList") ArrayList<String> sn);

    @PathAnno(RouterConfig.SunmiService.WEB_VIEW_CLOUD)
    void goToWebViewCloud(Context context, @ParameterAnno("mUrl") String url, @ParameterAnno("snList") ArrayList<String> sn, @AfterActionAnno Action action);

    @PathAnno(RouterConfig.SunmiService.WEB_VIEW_CLOUD)
    @FlagAnno({Intent.FLAG_ACTIVITY_SINGLE_TOP, Intent.FLAG_ACTIVITY_CLEAR_TOP})
    void goToWebViewCloudSingle(Context context, @ParameterAnno("mUrl") String url, @ParameterAnno("snList") ArrayList<String> sn);

    @PathAnno(RouterConfig.SunmiService.SERVICE_DETAIL)
    void goToServiceDetail(Context context, @ParameterAnno("mSn") String sn, @ParameterAnno("isBind") boolean isBind, @ParameterAnno("deviceName") String deviceName);

    @PathAnno(RouterConfig.SunmiService.SERVICE_MALL)
    void goToServiceMail(Context context, @ParameterAnno("mUrl") String mUrl);
}
