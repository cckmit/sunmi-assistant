package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class CompanyIpcListResp {

    @SerializedName("device_list")
    private List<IpcDevice> list;

    public List<IpcDevice> getList() {
        return list;
    }

}
