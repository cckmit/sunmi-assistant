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

import static sunmi.common.view.activity.ProtocolActivity.USER_IPC_PROTOCOL;
import static sunmi.common.view.activity.ProtocolActivity.USER_PRIVATE;
import static sunmi.common.view.activity.ProtocolActivity.USER_PROTOCOL;

/**
 * Description:
 * Created by bruce on 2019/3/28.
 */
public class ViewUtils {
    public static void setPrivacy(final Activity context, final CheckedTextView tv,
                                  int colorRes, int protocolType, int privacyType) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int len1, len2;
        String protocol = "";
        String privacy = "";
        switch (protocolType) {
            case USER_PROTOCOL:
                protocol = context.getString(R.string.sunmi_user_protocol);
                break;
            case USER_IPC_PROTOCOL:
                protocol = context.getString(R.string.str_ipc_protocol);
                break;
            default:
                protocol = context.getString(R.string.sunmi_user_protocol);
                break;
        }
        builder.append(tv.getText());
        len1 = builder.length();
        builder.append(protocol);
        TextPaint tp = new TextPaint();
        tp.linkColor = colorRes;
        //设置部分文字点击事件
        ClickableSpan csProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                CommonUtils.trackCommonEvent(context, "setupUserAgreement",
// "用户协议", Constants.EVENT_SETUP_ROUTER);//TODO
                ProtocolActivity_.intent(context)
                        .protocolType(protocolType).start();
                context.overridePendingTransition(R.anim.activity_open_down_up, 0);
            }
        };
        csProtocol.updateDrawState(tp);
        builder.setSpan(csProtocol, len1, len1 + protocol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), len1, len1 + protocol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
        tv.setText(builder);
        if (privacyType != -1) {
            builder.append(context.getString(R.string.str_and));
            len2 = builder.length();
            switch (privacyType) {
                case USER_PRIVATE:
                    privacy = context.getString(R.string.str_privacy);
                    break;
                default:
                    break;
            }
            builder.append(privacy);
            //设置部分文字点击事件
            ClickableSpan csPrivacy = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
//                CommonUtils.trackCommonEvent(context, "setupPrivacyAgreement",
// "隐私政策", Constants.EVENT_SETUP_ROUTER);//TODO
                    ProtocolActivity_.intent(context)
                            .protocolType(privacyType).start();
                    context.overridePendingTransition(R.anim.activity_open_down_up, 0);
                }
            };
            csPrivacy.updateDrawState(tp);
            builder.setSpan(csPrivacy, len2, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), len2, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
            tv.setText(builder);
        }
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
