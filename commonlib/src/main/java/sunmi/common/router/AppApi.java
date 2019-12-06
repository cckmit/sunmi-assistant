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
    @FlagAnno({Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_NO_HISTORY})
    void goToLogin(Context context, @ParameterAnno("reason") String extra);

    @PathAnno(RouterConfig.App.MAIN)
    @FlagAnno({Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TASK})
    void goToMainClearTask(Context context);

    @PathAnno(RouterConfig.App.MAIN)
    void goToMain(Context context);

    @PathAnno(RouterConfig.App.MAIN)
    void goToMain(Context context, @AfterActionAnno Action action);

    @PathAnno(RouterConfig.App.SUNMILINK)
    void goToSunmiLink(Context context, @ParameterAnno("shopId") String shopId, @ParameterAnno("sn") String sn);

    @PathAnno(RouterConfig.App.MSGDETAIL)
    @FlagAnno({Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_NEW_TASK})
    void goToMsgDetail(Context context, @ParameterAnno("modelId") int modelId, @ParameterAnno("modelName") String modelName);

    @PathAnno(RouterConfig.App.MSGCENTER)
    @FlagAnno({Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_NEW_TASK})
    void goToMsgCenter(Context context);

    @PathAnno(RouterConfig.App.REGISTER)
    void goToRegister(Context context);

    @PathAnno(RouterConfig.App.IMPORT_ORDER_PREVIEW)
    void gotoImportOrderPreview(Context context);
}
