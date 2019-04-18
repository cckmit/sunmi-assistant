package com.sunmi.ipc.view;

import android.support.v7.widget.RecyclerView;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * Description: WifiConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_config_completed")
public class WifiConfigCompletedActivity extends BaseActivity {

    @ViewById(resName = "rv_result")
    RecyclerView rvResult;

    @Extra
    String shopId;
    @Extra
    SunmiDevice sunmiDevice;

    private List<SunmiDevice> list = new ArrayList<>();

    @AfterViews
    void init() {
        initList();
    }

    @Click(resName = "btn_complete")
    void completeClick() {
        finish();
    }

    private void initList() {
        rvResult.setAdapter(new CommonListAdapter<SunmiDevice>(context,
                R.layout.item_device_config_complete, list) {
            @Override
            public void convert(ViewHolder holder, final SunmiDevice device) {
                holder.setText(R.id.tv_name, device.getName());
                if (device.getStatus() == 1) {
                    holder.setText(R.id.tv_status, "添加成功");
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_done);
//                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        recoverShowDialog(mContext, bean.getHostname(), bean.getMac());//取消优先
//                        return false;
//                    }
//                });
                } else {
                    holder.setText(R.id.tv_status, "添加失败，已绑定其他账号");
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_error);
                }
            }
        });
    }
}
