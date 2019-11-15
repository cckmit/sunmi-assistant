package sunmi.common.model;

import org.litepal.crud.DataSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-12.
 */
public class ShopBundledCloudInfo extends DataSupport {
    private int shopId;
    private Set<String> snSet;
    private boolean floatingShow;

    public ShopBundledCloudInfo(int shopId) {
        this.shopId = shopId;
        this.floatingShow = false;
        this.snSet = new HashSet<>();
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public Set<String> getSnSet() {
        return snSet;
    }

    public void setSnSet(Set<String> snSet) {
        this.snSet = snSet;
    }

    public boolean isFloatingShow() {
        return floatingShow;
    }

    public void setFloatingShow(boolean floatingShow) {
        this.floatingShow = floatingShow;
    }
}
