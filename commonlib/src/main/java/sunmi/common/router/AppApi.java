package sunmi.common.router;

import android.content.Intent;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.FlagAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

import sunmi.common.constant.RouterConfig;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-25.
 */
@RouterApiAnno()
@HostAnno(RouterConfig.App.NAME)
public interface AppApi {

    @PathAnno(RouterConfig.App.LOGIN)
    @FlagAnno({Intent.FLAG_ACTIVITY_NEW_TASK,Intent.FLAG_ACTIVITY_CLEAR_TASK,Intent.FLAG_ACTIVITY_CLEAR_TOP,Intent.FLAG_ACTIVITY_NO_HISTORY})
    void goToLogin(@ParameterAnno("reason")String extra);

    @PathAnno(RouterConfig.App.LOGIN)
    @FlagAnno({Intent.FLAG_ACTIVITY_NEW_TASK,Intent.FLAG_ACTIVITY_CLEAR_TASK,Intent.FLAG_ACTIVITY_CLEAR_TOP,Intent.FLAG_ACTIVITY_NO_HISTORY})
    void goToLogin();
}
