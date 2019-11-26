package sunmi.common.router;

import android.content.Context;

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
@HostAnno(RouterConfig.ApManager.NAME)
public interface ApManagerApi {

    @PathAnno(RouterConfig.ApManager.PRIMARY_ROUTE_SEARCH)
    void goToPrimaryRouteSearch(Context context);
}
