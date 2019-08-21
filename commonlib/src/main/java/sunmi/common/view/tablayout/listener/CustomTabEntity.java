package sunmi.common.view.tablayout.listener;

import android.support.annotation.DrawableRes;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-15.
 */
public interface CustomTabEntity {
    String getTabTitle();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();
}
