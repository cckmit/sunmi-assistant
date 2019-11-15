package com.sunmi.cloudprinter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;

import sunmi.common.utils.ByteUtils;
import sunmi.common.utils.PhotoUtils;

/**
 * Description:
 * Created by bruce on 2019/11/12.
 */
public class BitmapUtils {

    public static byte[] getCompressedImage(Context context, Uri imageUri) {
        Bitmap bitmap = PhotoUtils.getBitmapFromUri(imageUri, context);
        Bitmap output = BitmapUtils.zoomBitmap(bitmap, 384);
        byte[] gray = BitmapUtils.convertGreyImg(output);
        int h = output.getHeight(), w = output.getWidth();
        return ByteUtils.byteMergerAll(
                new byte[]{0x1D, 0x28, 0x45, 0x06, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x06},
                intToByte2L(w), intToByte2L(h),
                gray, new byte[]{0x0a, 0x0a, 0x0a});
    }

    /**
     * 整数转换为2字节的byte数组，低位在前
     */
    public static byte[] intToByte2L(int i) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (i & 0xFF);
        targets[1] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    /**
     * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片
     *
     * @param bitmap 源bitmap
     * @param w      缩放后指定的宽度
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= 384 || height <= 384) {
            return bitmap;
        }
        int h = (int) ((float) height / width * w);
        float scaleWidth, scaleHeight, x, y;
        Bitmap newbmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidth = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            // createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，
            // y+height要小于或等于bitmap.getHeight()
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x),
                    (int) (height - y), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public static byte[] convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高
        boolean needRotation = height < width;
        byte[] pixelsByte = new byte[width * height];//实际的byte灰度矩阵
        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                pixelsByte[width * i + j] = (byte) grey;
            }
        }
        return pixelsByte;
    }

//    public static byte[] convertGreyImg(Bitmap img) {
//        int width = img.getWidth();         //获取位图的宽
//        int height = img.getHeight();       //获取位图的高
//        boolean needRotation = height < width;
//        byte[] pixelsByte = new byte[width * height];//实际的byte灰度矩阵
//        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
//        img.getPixels(pixels, 0, width, 0, 0, width, height);
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                int grey = pixels[width * i + j];
//                int red = ((grey & 0x00FF0000) >> 16);
//                int green = ((grey & 0x0000FF00) >> 8);
//                int blue = (grey & 0x000000FF);
//                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
//                if (needRotation) {
//                    pixelsByte[height * i + j] = (byte) grey;
//                } else {
//                    pixelsByte[width * i + j] = (byte) grey;
//                }
//            }
//        }
//        return pixelsByte;
//    }

}
