package sunmi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.commonlibrary.R;

/**
 * 圆形头像
 * 重写CircleImage，继承ImageView
 */
public class CircleImage extends AppCompatImageView {

    private Context mContext;
    private float radius;


    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap b = bd.getBitmap();
        if (b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            Bitmap roundBitmap = getCroppedBitmap(bitmap, getWidth() < getHeight() ? getWidth() : getHeight(),
                    (int) radius);
            canvas.drawBitmap(roundBitmap, 0, 0, null);
        }
    }

    /*
     * 对Bitmap裁剪，使其变成圆形，这步最关键
     */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int minSide, int radius) {
        Bitmap squareBmp;
        //保证为正方形
        if (bmp.getWidth() != minSide || bmp.getHeight() != minSide)
            squareBmp = Bitmap.createScaledBitmap(bmp, minSide, minSide, false);
        else
            squareBmp = bmp;

        Bitmap output = Bitmap.createBitmap(squareBmp.getWidth(), squareBmp.getHeight(), Bitmap.Config.ARGB_8888);
        final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
        //设置画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));

        Canvas c = new Canvas(output);
        //填充透明
        c.drawARGB(0, 0, 0, 0);
        //画圆角矩形
        if (radius == -1) {
            c.drawRoundRect(new RectF(0, 0, squareBmp.getWidth(), squareBmp.getHeight()),
                    squareBmp.getWidth() * 1.0F / 6, squareBmp.getWidth() * 1.0F / 6, paint);
        } else {
            c.drawRoundRect(new RectF(0, 0, squareBmp.getWidth(), squareBmp.getHeight()),
                    radius, radius, paint);
        }
//            c.drawCircle(squareBmp.getWidth() / 2 + 0.7f, squareBmp.getHeight() / 2 + 0.7f,
//                    squareBmp.getWidth() / 2 + 0.1f, paint);                   //圆形
        //设置重叠时绘制图像规则
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //填充图片
        c.drawBitmap(squareBmp, rect, rect, paint);
        return output;
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CircleImage);
        radius = a.getDimension(R.styleable.CircleImage_radius, -1);
        a.recycle();
    }
}  
