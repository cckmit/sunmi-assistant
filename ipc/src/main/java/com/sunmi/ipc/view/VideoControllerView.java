package com.sunmi.ipc.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Description:
 * Created by bruce on 2019/4/19.
 */
public class VideoControllerView  extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvPercent;
    private ProgressBar proPercent;

    public VideoControllerView(Context context) {
        super(context);

        init();
    }

    private void init() {
//        setGravity(Gravity.CENTER);
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_center_window, this);
//        ivIcon = view.findViewById(R.id.iv_icon);
//        tvPercent = view.findViewById(R.id.tv_percent);
//        proPercent = view.findViewById(R.id.pro_percent);
    }

    public void setIcon(int icon) {
        if (ivIcon != null) ivIcon.setImageResource(icon);
    }

    public void setTextView(String text) {
        if (tvPercent != null) tvPercent.setText(text);
    }

    public void setProPercent(int percent) {
        if (proPercent != null) proPercent.setProgress(percent);
    }

    public void setProVisibility(int visibility) {
        if (proPercent != null) proPercent.setVisibility(visibility);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
//        if (visibility != VISIBLE) {
//            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dkplayer_anim_center_view);
//            this.startAnimation(animation);
//        }
    }
}