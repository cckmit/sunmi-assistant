package sunmi.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commonlibrary.R;


public class WinsafeSpinner<T> extends LinearLayout {
	/**
	 * 样式枚举
	 */
	public enum StyleEnum {
		NONE, BLUE, RED, Green
	}

	private TextView tvTitle;
	private AutoCompleteTextView actvText;
	private ImageButton ibtnDropdown;

	public WinsafeSpinner(Context context) {
		super(context);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 */
	public WinsafeSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutInflater.inflate(R.layout.winsafe_spinner, this);

//		if (!isInEditMode()) {
			tvTitle = (TextView) findViewById(R.id.tvTitle);
			actvText = (AutoCompleteTextView) findViewById(R.id.actvText);
			actvText.setThreshold(1);
			ibtnDropdown = (ImageButton) findViewById(R.id.ibtnDropdown);
	
			actvText.setOnClickListener(new ViewClickListener());
			ibtnDropdown.setOnClickListener(new ViewClickListener());
//		}
	}

	@SuppressLint("NewApi")
	public void setStyle(StyleEnum styleEnum) {
		switch (styleEnum) {
//		case RED:
//			tvTitle.setBackgroundResource(R.drawable.wsspinner_textview_red);
//			actvText.setBackgroundResource(R.drawable.wsspinner_edittext_red);
//			ibtnDropdown.setBackgroundResource(R.drawable.wsspinner_dropdown_red);
//			break;
//		case Green:
//			tvTitle.setBackgroundResource(R.drawable.wsspinner_textview_green);
//			actvText.setBackgroundResource(R.drawable.wsspinner_edittext_green);
//			ibtnDropdown.setBackgroundResource(R.drawable.wsspinner_dropdown_green);
//			break;
		case NONE:
			tvTitle.setBackgroundResource(R.color.transparent);
			actvText.setBackgroundResource(R.color.transparent);
			ibtnDropdown.setVisibility(View.GONE);
			break;
		default:
			tvTitle.setBackgroundResource(R.drawable.wsspinner_textview_blue);
			actvText.setBackgroundResource(R.drawable.wsspinner_edittext_blue);
			ibtnDropdown.setBackgroundResource(R.drawable.wsspinner_dropdown_blue);
			break;
		}
	}

	/**
	 * 设置下拉框的标题
	 * 
	 * @param text
	 *            标题文本
	 */
	public void setTitle(String text) {
		tvTitle.setText(text);
	}

	/**
	 * 设置下拉框的内容文本
	 * 
	 * @param prompt
	 *            内容文本
	 */
	@SuppressLint("NewApi")
	public void setPrompt(String prompt) {
		actvText.setText(prompt, false);
	}

	/**
	 * 获取下拉框的内容文本
	 */
	public String getPrompt() {
		return actvText.getText().toString();
	}

	public void setPopupEnabled(boolean enabled) {
		if (enabled) {
			actvText.setThreshold(1);
		} else {
			actvText.setThreshold(Integer.MAX_VALUE);
		}
	}
	
	/**
	 * 给下拉文本框设置数据源
	 * 
	 * @param adapter
	 *            数据源
	 */
	public void setAdapter(ArrayAdapter<T> adapter) {
		actvText.setAdapter(adapter);
	}

	/**
	 * 返回下拉文本框数据源
	 * 
	 * @param
	 *
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public ArrayAdapter<T> getAdapter() {
		return (ArrayAdapter<T>) actvText.getAdapter();
	}

	/**
	 * 给下拉框设置ItemClick事件
	 * 
	 * @param l
	 *            事件
	 */
	public void setOnItemClickListener(OnItemClickListener l) {
		actvText.setOnItemClickListener(l);
	}

	/**
	 * 设置标题、内容和下拉按钮所占比重
	 * 
	 * @param titleWeight
	 *            标题比重值
	 * @param contentWeight
	 *            内容比重值
	 * @param dropdownWeight
	 *            下拉按钮比重值
	 */
	public void setWeight(float titleWeight, float contentWeight, float dropdownWeight) {
		LayoutParams titleLP = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		titleLP.weight = titleWeight;
		tvTitle.setLayoutParams(titleLP);

		LayoutParams contentLP = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		contentLP.weight = contentWeight;
		actvText.setLayoutParams(contentLP);

		LayoutParams dropdownLP = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		dropdownLP.weight = dropdownWeight;
		ibtnDropdown.setLayoutParams(dropdownLP);
	}
	
	public AutoCompleteTextView getActvText() {
		return actvText;
	}

	private class ViewClickListener implements OnClickListener {
		public void onClick(View v) {
			actvText.showDropDown();
		}
	}
}
