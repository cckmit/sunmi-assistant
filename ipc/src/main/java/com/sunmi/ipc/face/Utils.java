package com.sunmi.ipc.face;

import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.FaceGroup;

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
    private static SparseArray<Pair<String, String>> mGroupNameCache = new SparseArray<>(4);

    public static String getGroupName(Context context, FaceGroup faceGroup, boolean isRole) {
        Pair<String, String> model = mGroupNameCache.get(faceGroup.getType());
        if (model != null) {
            return isRole ? model.first : model.second;
        }
        String group = context.getString(R.string.ipc_face_group_suffix);
        if (faceGroup.isCustomType()) {
            return isRole ? faceGroup.getGroupName() : faceGroup.getGroupName() + group;
        }

        String role;
        switch (faceGroup.getType()) {
            case FACE_GROUP_TYPE_NEW:
                role = context.getString(R.string.ipc_face_group_new);
                model = new Pair<>(role, role + group);
                mGroupNameCache.put(FACE_GROUP_TYPE_NEW, model);
                return isRole ? model.first : model.second;
            case FACE_GROUP_TYPE_OLD:
                role = context.getString(R.string.ipc_face_group_old);
                model = new Pair<>(role, role + group);
                mGroupNameCache.put(FACE_GROUP_TYPE_OLD, model);
                return isRole ? model.first : model.second;
            case FACE_GROUP_TYPE_STAFF:
                role = context.getString(R.string.ipc_face_group_staff);
                model = new Pair<>(role, role + group);
                mGroupNameCache.put(FACE_GROUP_TYPE_STAFF, model);
                return isRole ? model.first : model.second;
            case FACE_GROUP_TYPE_BLACK:
                role = context.getString(R.string.ipc_face_group_black);
                model = new Pair<>(role, role + group);
                mGroupNameCache.put(FACE_GROUP_TYPE_BLACK, model);
                return isRole ? model.first : model.second;
            default:
                LogCat.e(TAG, "Face group type ERROR. " + faceGroup.getType());
        }
        return "";
    }
}
