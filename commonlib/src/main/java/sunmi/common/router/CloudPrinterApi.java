package sunmi.common.router;

import android.content.Context;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

import sunmi.common.constant.RouterConfig;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-26.
 */
@RouterApiAnno
@HostAnno(RouterConfig.CloudPrinter.NAME)
public interface CloudPrinterApi {

    @PathAnno(RouterConfig.CloudPrinter.START_CONFIG_PRINTER)
    void goToSartConfigPrinter(Context context, @ParameterAnno("shopId") int shopId);

}
