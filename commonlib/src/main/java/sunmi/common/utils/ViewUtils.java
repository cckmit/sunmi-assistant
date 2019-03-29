package sunmi.common.utils;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckedTextView;

import com.commonlibrary.R;

import sunmi.common.view.activity.ProtocolActivity_;

import static sunmi.common.view.activity.ProtocolActivity.USER_AP_PRIVATE;
import static sunmi.common.view.activity.ProtocolActivity.USER_AP_PROTOCOL;
import static sunmi.common.view.activity.ProtocolActivity.USER_PRIVATE;
import static sunmi.common.view.activity.ProtocolActivity.USER_PROTOCOL;

/**
 * Description:
 * Created by bruce on 2019/3/28.
 */
public class ViewUtils {
    public static void setPrivacy(final Activity context, final CheckedTextView tv,
                                  int colorRes, final boolean isLocalPage) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int len1, len2;
        String protocol = context.getString(R.string.sunmi_user_protocol);
        String privacy = context.getString(R.string.str_privacy);
        builder.append(tv.getText());
        len1 = builder.length();
        builder.append(protocol);
        builder.append(context.getString(R.string.str_and));
        len2 = builder.length();
        builder.append(privacy);
        TextPaint tp = new TextPaint();
        tp.linkColor = colorRes;
        //设置部分文字点击事件
        ClickableSpan csProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                CommonUtils.trackCommonEvent(context, "setupUserAgreement",
// "用户协议", Constants.EVENT_SETUP_ROUTER);//TODO
                ProtocolActivity_.intent(context)
                        .protocolType(isLocalPage ? USER_AP_PROTOCOL : USER_PROTOCOL).start();
                context.overridePendingTransition(R.anim.activity_open_down_up, 0);
            }
        };
        csProtocol.updateDrawState(tp);
        builder.setSpan(csProtocol, len1, len1 + protocol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);

        //设置部分文字点击事件
        ClickableSpan csPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                CommonUtils.trackCommonEvent(context, "setupPrivacyAgreement",
// "隐私政策", Constants.EVENT_SETUP_ROUTER);//TODO
                ProtocolActivity_.intent(context)
                        .protocolType(isLocalPage ? USER_AP_PRIVATE : USER_PRIVATE).start();
                context.overridePendingTransition(R.anim.activity_open_down_up, 0);
            }
        };
        csPrivacy.updateDrawState(tp);
        builder.setSpan(csPrivacy, len2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);

        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(builder);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setChecked(!tv.isChecked());
            }
        });
    }

}
