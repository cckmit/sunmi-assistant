package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class CompanyIpcListResp {

    @SerializedName("shop_list")
    private List<CompanyIpcList> shopList;

    public List<CompanyIpcList> getShopList() {
        return shopList;
    }

}
