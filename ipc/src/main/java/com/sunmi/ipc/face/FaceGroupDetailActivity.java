package com.sunmi.ipc.face;

import com.sunmi.ipc.face.contract.FaceGroupDetailContract;
import com.sunmi.ipc.face.presenter.FaceGroupDetailPresenter;

import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseMvpActivity;

/**
 * @author yinhui
 * @date 2019-08-20
 */
@EActivity(resName = "face_activity_group_detail")
public class FaceGroupDetailActivity extends BaseMvpActivity<FaceGroupDetailPresenter>
        implements FaceGroupDetailContract.View {


    @Override
    public void updateNameView(String name) {

    }

    @Override
    public void updateCapacityView(int capacity) {

    }

    @Override
    public void updateRegularView(int times, int days) {

    }

    @Override
    public void updateMarkView(String mark) {

    }
}
