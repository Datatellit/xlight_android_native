package com.umarbhutta.xlightcompanion.Tools;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.umarbhutta.xlightcompanion.R;

public class ToastUtil {
    private static IToast mToast = null;
    private static KProgressHUD kProgressHUD;

    public static void showToast(Context context, int Stringid) {
        showToast(context, context.getString(Stringid));
    }

    public static void showToast(Context context, String string) {
        if (TextUtils.isEmpty(string))
            return;
        try {
//            if (null == mToast) {
//                mToast = ToastCompat.makeText(context, string, Toast.LENGTH_SHORT);
//            } else {
//                mToast.setText(string);
//            }
            ToastCompat.makeText(context, string, Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, 0).show();
//            mToast.setGravity(Gravity.CENTER, 0, 0);
//            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLoading(Context context) {
        showLoading(context, null, null);
    }

    public static void showLoading(Context context, String title) {
        showLoading(context, title, null);
    }

    public static void showLoading(Context context, String title, String detail) {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            try {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing())
                        kProgressHUD.dismiss();
                }
                kProgressHUD = null;
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                kProgressHUD = null;
            }
        }
        kProgressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        if (title == null)
            kProgressHUD.setLabel(context.getResources().getString(R.string.add_device_wifi_wait));
        else
            kProgressHUD.setLabel(title);

        if (detail != null)
            kProgressHUD.setDetailsLabel(detail);
        kProgressHUD.show();
    }

    public static void dismissLoading() {
        if (kProgressHUD != null && kProgressHUD.isShowing())
            kProgressHUD.dismiss();
    }

}