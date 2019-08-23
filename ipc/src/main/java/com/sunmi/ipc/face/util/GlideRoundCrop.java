package com.sunmi.ipc.face.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Preconditions;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * @author yinhui
 * @date 2019-08-22
 */
public class GlideRoundCrop extends CenterCrop {

    private static final String ID = GlideRoundCrop.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);


    private final int roundingRadius;

    /**
     * @param roundingRadius the corner radius (in device-specific pixels).
     * @throws IllegalArgumentException if rounding radius is 0 or less.
     */
    public GlideRoundCrop(int roundingRadius) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");
        this.roundingRadius = roundingRadius;
    }

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap transform = super.transform(pool, toTransform, outWidth, outHeight);
        return TransformationUtils.roundedCorners(pool, transform, roundingRadius);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt(roundingRadius).array();
        messageDigest.update(radiusData);
    }
}
