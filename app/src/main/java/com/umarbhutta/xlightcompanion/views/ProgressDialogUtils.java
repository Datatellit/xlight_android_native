package com.umarbhutta.xlightcompanion.views;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 加载进度条
 * Created by guangbinw on 2017/3/13.
 */
public class ProgressDialogUtils {

    /**
     * 显示加载的dialog
     *
     * @param context
     */
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        return dialog;
    }

}
