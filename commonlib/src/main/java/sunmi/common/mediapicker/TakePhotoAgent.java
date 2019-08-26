package sunmi.common.mediapicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sunmi.common.mediapicker.data.model.Result;

/**
 * Created by Jacob on 17-11-27.
 */

public interface TakePhotoAgent {

    int FROM_TAKE_PHOTO = 0;
    int FROM_SINGLE_PICK = 1;
    int FROM_MULTI_PICK = 2;

    void pickSinglePhoto();

    void pickMultiPhotos(Result haveSelected);

    void takePhoto();

    void setPickLimit(int limit);

    void onCreate(@Nullable Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults);

}
