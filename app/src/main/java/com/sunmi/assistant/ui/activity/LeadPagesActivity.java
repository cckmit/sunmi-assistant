package com.sunmi.assistant.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.login.LoginActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.activity.ProtocolActivity_;
import sunmi.common.view.dialog.CommonDialog;

import static sunmi.common.view.activity.ProtocolActivity.USER_PRIVATE;
import static sunmi.common.view.activity.ProtocolActivity.USER_PROTOCOL;

@EActivity(R.layout.viewpager_view)
public class LeadPagesActivity extends BaseActivity {

    @ViewById(R.id.viewpager_view_pager)
    ViewPager mPager;
    @ViewById(R.id.point1)
    ImageView point1;
    @ViewById(R.id.point2)
    ImageView point2;
    @ViewById(R.id.point3)
    ImageView point3;
    @ViewById(R.id.point4)
    ImageView point4;
    @ViewById(R.id.btnSkip)
    Button btnSkip;

    private SparseArray<View> mPageCache = new SparseArray<>();
    private final int mCount = 4;//page数量

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);
        getProtocolDialog().showWithOutTouchable(false);
        point1.setBackgroundResource(R.drawable.oval_gold);
        point2.setBackgroundResource(R.drawable.oval_black_light);
        point3.setBackgroundResource(R.drawable.oval_black_light);
        point4.setBackgroundResource(R.drawable.oval_black_light);

        mPager.setAdapter(new ViewPagerAdapter(this));
        mPager.addOnPageChangeListener(new OnViewPageChangeListener());
    }

    @Click(R.id.btnSkip)
    void onSkipClick(View v) {
        switch (v.getId()) {
            case R.id.btnSkip:
                gotoLoginActivity();
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        private ViewPagerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = mPageCache.get(position);
            if (page == null) {
                page = mInflater.inflate(R.layout.viewpager_view_item, container, false);
                ImageView imgContent = page.findViewById(R.id.viewpager_view_page_content);
                TextView tvTitle = page.findViewById(R.id.tvTitle);
                TextView tvContent = page.findViewById(R.id.tvContent);
                Button btnText = page.findViewById(R.id.btnText);
                switch (position) {
                    case 0:
                        imgContent.setImageResource(R.mipmap.ic_lead_a);
                        tvTitle.setText(R.string.str_text_lead_remote);
                        tvContent.setText(R.string.str_text_lead_query_status);
                        btnText.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        if (CommonHelper.isGooglePlay()) {
                            imgContent.setImageResource(R.mipmap.ic_lead_b_foreign);
                        } else {
                            imgContent.setImageResource(R.mipmap.ic_lead_b);
                        }
                        tvTitle.setText(R.string.str_text_lead_tem);
                        tvContent.setText(R.string.str_text_lead_set_perm);
                        btnText.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        if (CommonHelper.isGooglePlay()) {
                            imgContent.setImageResource(R.mipmap.ic_lead_c_foreign);
                        } else {
                            imgContent.setImageResource(R.mipmap.ic_lead_c);
                        }
                        tvTitle.setText(R.string.str_text_lead_allo);
                        tvContent.setText(R.string.str_text_lead_service);
                        btnText.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        btnText.setVisibility(View.VISIBLE);
                        imgContent.setImageResource(R.mipmap.ic_lead_d);
                        tvTitle.setText(R.string.str_text_lead_search_dev);
                        tvContent.setText(R.string.str_text_lead_set_connected);
                        btnText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoLoginActivity();
                            }
                        });
                        break;
                    default:
                        break;
                }
                mPageCache.append(position, page);
            }
            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void gotoLoginActivity() {   //login
        if (isFastClick(1000)) return;
        LoginActivity_.intent(context).start();
        finish();
    }

    private CommonDialog getProtocolDialog() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        CommonDialog.Builder dialogBuilder = new CommonDialog.Builder(context)
                .setTitle(R.string.dialog_protocol_title);
        int len1, len2;
        builder.append(getText(R.string.dialog_protocol_msg_1));
        String protocol = getString(R.string.sunmi_user_protocol);
        String privacy = getString(R.string.str_privacy);
        len1 = builder.length();
        builder.append(protocol);
        ClickableSpan csProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ProtocolActivity_.intent(context)
                        .protocolType(USER_PROTOCOL).start();
                overridePendingTransition(R.anim.activity_open_down_up, 0);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(context,R.color.common_orange));
            }
        };
        builder.setSpan(csProtocol, len1, len1 + protocol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(getText(R.string.str_and));
        len2 = builder.length();
        builder.append(privacy);
        ClickableSpan csPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ProtocolActivity_.intent(context)
                        .protocolType(USER_PRIVATE).start();
                overridePendingTransition(R.anim.activity_open_down_up, 0);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(context,R.color.common_orange));
            }
        };
        builder.setSpan(csPrivacy, len2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(getText(R.string.dialog_protocol_msg_2));
        dialogBuilder.setMessage(builder);
        dialogBuilder.setMessageMovementMethod(LinkMovementMethod.getInstance());
        dialogBuilder.setCancelButton(R.string.dialog_protocol_cancel, (dialog, which) -> {
            MyApplication.getInstance().finishActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        });
        dialogBuilder.setConfirmButton(R.string.dialog_protocol_confirm, (dialog, which) -> {
            SpUtils.saveLead();//保存引导页值
        });
        CommonDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    private class OnViewPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        private void isVisiblePoint() {
            point1.setVisibility(View.VISIBLE);
            point2.setVisibility(View.VISIBLE);
            point3.setVisibility(View.VISIBLE);
            point4.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.VISIBLE);
        }

        private void isGonePoint() {
            point1.setVisibility(View.GONE);
            point2.setVisibility(View.GONE);
            point3.setVisibility(View.GONE);
            point4.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
        }

        @Override
        public void onPageSelected(int position) {
            LogCat.e("TAG", "position: " + position);
            switch (position) {
                case 0:
                    isVisiblePoint();
                    point1.setBackgroundResource(R.drawable.oval_gold);
                    point2.setBackgroundResource(R.drawable.oval_black_light);
                    point3.setBackgroundResource(R.drawable.oval_black_light);
                    point4.setBackgroundResource(R.drawable.oval_black_light);
                    break;
                case 1:
                    isVisiblePoint();
                    point1.setBackgroundResource(R.drawable.oval_black_light);
                    point2.setBackgroundResource(R.drawable.oval_gold);
                    point3.setBackgroundResource(R.drawable.oval_black_light);
                    point4.setBackgroundResource(R.drawable.oval_black_light);
                    break;
                case 2:
                    isVisiblePoint();
                    point1.setBackgroundResource(R.drawable.oval_black_light);
                    point2.setBackgroundResource(R.drawable.oval_black_light);
                    point3.setBackgroundResource(R.drawable.oval_gold);
                    point4.setBackgroundResource(R.drawable.oval_black_light);
                    break;
                case 3:
                    isGonePoint();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE://空闲状态
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING://滑动状态
                    break;
                case ViewPager.SCROLL_STATE_SETTLING://滑动后自然沉降的状态
                    break;
            }
        }
    }

}