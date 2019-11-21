package sunmi.common.router;

import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.router.HostAnno;
import com.xiaojinzi.component.anno.router.PathAnno;
import com.xiaojinzi.component.anno.router.RouterApiAnno;

import sunmi.common.constant.RouterConfig;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-20.
 */
@RouterApiAnno()
@HostAnno(RouterConfig.Ipc.NAME)
public interface IpcApi {

    @PathAnno(RouterConfig.Ipc.IPC_START_CONFIG)
    void goToIpcStartConfig(@ParameterAnno("ipcType")int type);
}
