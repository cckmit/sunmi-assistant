package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-10-17
 */
public class AuthorizeInfoResp {

    @SerializedName("authorized_list")
    private List<SaasStatus> list;

    public List<SaasStatus> getList() {
        return list;
    }

}
