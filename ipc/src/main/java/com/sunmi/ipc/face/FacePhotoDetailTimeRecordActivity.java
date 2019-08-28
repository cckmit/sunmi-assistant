package com.sunmi.ipc.face;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.model.FaceEntryHistoryResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

import static com.sunmi.ipc.face.FacePhotoDetailActivity.DATE_FORMAT_ENTER_SHOP;
import static sunmi.common.utils.CommonHelper.long2Time;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
@EActivity(resName = "face_activity_photo_detail_time_record")
public class FacePhotoDetailTimeRecordActivity extends BaseActivity {

    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;
    @ViewById(resName = "tv_empty")
    TextView tvEmpty;
    @ViewById(resName = "include_network_error")
    View includeNetworkError;
    @Extra
    int mShopId;
    @Extra
    Face mFace;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        arrivalList();
    }

    private void arrivalList() {
        showLoadingDialog();
        IpcCloudApi.arrivalListFaceGroup(SpUtils.getCompanyId(), mShopId, mFace.getFaceId(), new RetrofitCallback<FaceEntryHistoryResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceEntryHistoryResp data) {
                hideLoadingDialog();
                List<FaceEntryHistoryResp.EntryHistory> list = data.getHistoryList();
                includeNetworkError.setVisibility(View.GONE);
                if (list.size() == 0) {
                    tvEmpty.setText(R.string.ipc_face_group_no_enter_shop_time);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    showDataView(list);
                }
            }

            @Override
            public void onFail(int code, String msg, FaceEntryHistoryResp data) {
                hideLoadingDialog();
                includeNetworkError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showDataView(List<FaceEntryHistoryResp.EntryHistory> list) {
        recyclerView.setAdapter(new CommonListAdapter<FaceEntryHistoryResp.EntryHistory>(this,
                R.layout.item_face_photo_time_record, list) {
            @Override
            public void convert(ViewHolder holder, FaceEntryHistoryResp.EntryHistory entryHistory) {
                holder.setText(R.id.tv_time, long2Time(entryHistory.getArrivalTime() * 1000, DATE_FORMAT_ENTER_SHOP));
            }
        });
    }
}