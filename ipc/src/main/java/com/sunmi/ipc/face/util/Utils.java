package com.sunmi.ipc.face.util;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.FaceGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sunmi.common.luban.Luban;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.log.LogCat;

import static com.sunmi.ipc.face.model.FaceGroup.FACE_GROUP_TYPE_BLACK;
import static com.sunmi.ipc.face.model.FaceGroup.FACE_GROUP_TYPE_NEW;
import static com.sunmi.ipc.face.model.FaceGroup.FACE_GROUP_TYPE_OLD;
import static com.sunmi.ipc.face.model.FaceGroup.FACE_GROUP_TYPE_STAFF;

/**
 * @author yinhui
 * @date 2019-08-19
 */
public class Utils {

    private static final String TAG = "Face.Utils";

    /**
     * Pair.first：角色名，例如：熟客，会员
     * Pair.second：分组名，例如：熟客分组，会员分组
     */
    private static SparseArray<String> mGroupNameCache = new SparseArray<>(4);

    public static String getGroupName(Context context, FaceGroup group) {
        if (group.isCustomType()) {
            return group.getGroupName();
        }
        String name = mGroupNameCache.get(group.getType());
        if (name != null) {
            return name;
        }

        String role;
        switch (group.getType()) {
            case FACE_GROUP_TYPE_NEW:
                name = context.getString(R.string.ipc_face_group_new);
                mGroupNameCache.put(FACE_GROUP_TYPE_NEW, name);
                return name;
            case FACE_GROUP_TYPE_OLD:
                name = context.getString(R.string.ipc_face_group_old);
                mGroupNameCache.put(FACE_GROUP_TYPE_OLD, name);
                return name;
            case FACE_GROUP_TYPE_STAFF:
                name = context.getString(R.string.ipc_face_group_staff);
                mGroupNameCache.put(FACE_GROUP_TYPE_STAFF, name);
                return name;
            case FACE_GROUP_TYPE_BLACK:
                name = context.getString(R.string.ipc_face_group_black);
                mGroupNameCache.put(FACE_GROUP_TYPE_BLACK, name);
                return name;
            default:
                LogCat.e(TAG, "Face group type ERROR. " + group.getType());
        }
        return "";
    }

    @WorkerThread
    public static File imageCompress(Context context, File file) {
        try {
            int count = 0;
            while (file.length() > Constants.FILE_SIZE_1M) {
                List<File> files = Luban.with(context)
                        .setTargetDir(FileHelper.SDCARD_CACHE_IMAGE_PATH)
                        .load(file)
                        .get();
                file = files.get(0);
                count++;
            }
            return file.length() > Constants.FILE_SIZE_1M ? null : file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
