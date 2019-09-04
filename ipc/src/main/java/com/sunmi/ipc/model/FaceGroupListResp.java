package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.face.model.FaceGroup;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceGroupListResp {
    @SerializedName("group_list")
    private List<FaceGroup> groupList;

    public List<FaceGroup> getGroupList() {
        return groupList;
    }

}
