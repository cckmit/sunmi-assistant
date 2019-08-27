package sunmi.common.mediapicker.util;

import java.util.List;

import sunmi.common.mediapicker.data.model.Image;

public interface OnCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(List<Image> images);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(Throwable e);
}
