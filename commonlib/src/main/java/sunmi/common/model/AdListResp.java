package sunmi.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public class AdListResp implements Serializable {
    private List<AdListBean> ad_list;

    public List<AdListBean> getAd_list() {
        return ad_list;
    }

    public void setAd_list(List<AdListBean> ad_list) {
        this.ad_list = ad_list;
    }

}
