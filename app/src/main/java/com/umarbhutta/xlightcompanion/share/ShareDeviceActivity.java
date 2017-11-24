package com.umarbhutta.xlightcompanion.share;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.bindDevice.BindDeviceSearchActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by 75932 on 2017/11/1.
 */

public class ShareDeviceActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_add_share);

        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.share_title);
    }

    @Override
    public void onClick(View v) {

    }

    public boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    ProgressDialog progressDialog;

    public void shareClick(View v) {
        progressDialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.loading));
        EditText et_user_accountStr = (EditText) findViewById(R.id.etUsername);
        // 验证
        if (TextUtils.isEmpty(et_user_accountStr.getText().toString())) {
            ToastUtil.showToast(this, getString(R.string.account_is_null));
            return;
        }

        if (!StringUtil.isEmail(et_user_accountStr.getText().toString())) {
            ToastUtil.showToast(this, getString(R.string.email_error));
            return;
        }
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }
        // 后台分享接口
        if (!progressDialog.isShowing())
            progressDialog.show();
        JSONObject jb = new JSONObject();
        try {
            jb.put("toUser", et_user_accountStr.getText().toString());
            jb.put("userId", UserUtils.getUserInfo(this).id);
            jb.put("deviceId", getIntent().getIntExtra("deviceId", 0));
            jb.put("type", 3);
            jb.put("content", "share device");
            jb.put("lan", isZh() ? "cn" : "en");
        } catch (Exception e) {
            Log.e("XLight", e.getMessage(), e);
        }
        HttpUtils.getInstance().postRequestInfo(String.format(NetConfig.URL_ADD_SHARE, UserUtils.getAccessToken(this)), jb.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        // 分享成功
                        ToastUtil.showToast(getApplication(), R.string.share_success);
                        finish();
                    }
                });
            }

            @Override
            public void onHttpRequestFail(final int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (code == 40202) {
                            ToastUtil.showToast(getApplicationContext(), R.string.share_notfound);
                        } else if (code == 41400) {
                            ToastUtil.showToast(getApplicationContext(), R.string.share_already);
                        } else if (code == 41401) {
                            ToastUtil.showToast(getApplicationContext(), R.string.share_yourself);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), errMsg);
                        }
                    }
                });
            }
        });
    }
}
