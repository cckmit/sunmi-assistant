package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public class MotionVideoListResp {

    /**
     * total_count : 51
     * motion_list : [{"id":40,"device_name":"绑定测试1","sn":"xxxxxxxxxxxx","source":1,"detect_time":1554201061,"cdn_address":"http://test.cdn.sunmi.com/VIDEO/IPC/d42eeebbc9a6c7ddce5db13a07943bb7cfcee7552045c1af4ff5aef65d681dd1","snapshot_address":"http://test.cdn.sunmi.com/VIDEO/IPC/d42eeebbc9a6c7ddce5db13a07943bb7cfcee7552045c1af4ff5aef65d681dd1?x-oss-process=video/snapshot,t_2300,f_jpg,m_fast"},{"id":49,"device_name":"绑定测试1","sn":"yyyyyyyyyyyyy","source":1,"detect_time":1554202517,"cdn_address":"http://test.cdn.sunmi.com/VIDEO/IPC/41dd3f089ee0746efa4f9c0500719e71d1cf474b111f2a72e832906e619bc6f8","snapshot_address":"http://test.cdn.sunmi.com/VIDEO/IPC/41dd3f089ee0746efa4f9c0500719e71d1cf474b111f2a72e832906e619bc6f8?x-oss-process=video/snapshot,t_2300,f_jpg,m_fast"}]
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("motion_list")
    private List<MotionVideo> motionList;

    public int getTotalCount() {
        return totalCount;
    }

    public List<MotionVideo> getMotionList() {
        return motionList;
    }

}
