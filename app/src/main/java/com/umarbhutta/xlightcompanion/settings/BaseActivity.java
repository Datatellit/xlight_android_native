package com.umarbhutta.xlightcompanion.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * create by：guangbinw on 16/3/30 17:01
 * email：guangbingwang@126.com
 */
public class BaseActivity extends FragmentActivity {
    protected ProgressDialog mProgressDialog;
    private LayoutInflater mInflater;
    private View netErrorView;
    private String pageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mInflater = getLayoutInflater();

        PushAgent.getInstance(this).onAppStart();

    }

    @Override
    protected void onResume() {
        if (TextUtils.isEmpty(pageName)) {
            pageName = getLocalClassName();
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 获得rootview
     *
     * @param context
     * @return
     */
    public View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content))
                .getChildAt(0);
    }

    /**
     * 替换fragment
     *
     * @param resid
     * @param frame
     */
    public void replace(int resid, Fragment frame) {
        if (findViewById(resid) != null) {
            FragmentTransaction t = this.getSupportFragmentManager()
                    .beginTransaction();
            t.replace(resid, frame);
            t.commitAllowingStateLoss();
        }
    }

    /**
     * 菊花消失
     */
    public void cancelProgressDialog() {
        if (mProgressDialog == null) {
            return;
        } else {
            mProgressDialog.cancel();
        }
    }

    /**
     * 菊花旋转
     */
    public void showProgressDialog(String msg) {

        if (null == mProgressDialog) {
            mProgressDialog = ProgressDialogUtils.showProgressDialog(this, msg);
        } else {
            mProgressDialog.setMessage(msg);
        }

        if (null != mProgressDialog) {
            mProgressDialog.show();
        }
    }
}
