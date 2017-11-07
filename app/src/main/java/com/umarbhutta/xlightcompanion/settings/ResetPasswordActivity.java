package com.umarbhutta.xlightcompanion.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.CommentResult;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/5.
 * 重置密码
 */

public class ResetPasswordActivity extends BaseActivity implements HttpUtils.OnHttpRequestCallBack {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private EditText et_old_passwordTv;
    private EditText et_new_passwordTv;
    private EditText et_new_password_againTv;
    private ProgressDialog dialog;
    private String email;
    private ImageButton ib_clear1,ib_clear2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        email = getIntent().getStringExtra("email");
        initViews();
    }

    private void initViews() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tvEditSure).setVisibility(View.GONE);
        btnSure = (TextView) findViewById(R.id.btn_login);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.reset_pwd);


        et_old_passwordTv = (EditText) findViewById(R.id.et_old_password);
        et_new_passwordTv = (EditText) findViewById(R.id.et_new_password);
        et_new_password_againTv = (EditText) findViewById(R.id.et_new_password_again);

        ib_clear1 = (ImageButton) findViewById(R.id.ib_clear1);
        ib_clear2 = (ImageButton) findViewById(R.id.ib_clear2);
        ib_clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_old_passwordTv.setText("");
            }
        });
        ib_clear1.setVisibility(View.INVISIBLE);

        ib_clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_new_passwordTv.setText("");
            }
        });
        ib_clear2.setVisibility(View.INVISIBLE);

        et_old_passwordTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    ib_clear1.setVisibility(View.INVISIBLE);
                } else {
                    ib_clear1.setVisibility(View.VISIBLE);
                }
            }
        });
        et_new_passwordTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    ib_clear2.setVisibility(View.INVISIBLE);
                } else {
                    ib_clear2.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.first_layout).setVisibility(View.GONE);

    }

    private void commit() {
        String et_new_passwordTvStr = et_new_passwordTv.getText().toString();
        String et_new_password_againTvStr = et_new_password_againTv.getText().toString();

        if (TextUtils.isEmpty(et_new_passwordTvStr)) {
            ToastUtil.showToast(this, R.string.input_new_pwd);
            return;
        }

        if (TextUtils.isEmpty(et_new_password_againTvStr)) {
            ToastUtil.showToast(this, getString(R.string.please_input_verifycode));
            return;
        }


        dialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.commit_img));

        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("password", et_new_passwordTvStr);
            object.put("verificationcode", et_new_password_againTvStr);
            String paramStr = object.toString();
            HttpUtils.getInstance().putRequestInfo(NetConfig.URL_RESET_PWD, paramStr, CommentResult.class, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onHttpRequestSuccess(final Object result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                ToastUtil.showToast(ResetPasswordActivity.this, getString(R.string.pwd_reset_success));
                ResetPasswordActivity.this.finish();
            }
        });

    }

    @Override
    public void onHttpRequestFail(int code, final String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                ToastUtil.showToast(ResetPasswordActivity.this, "" + errMsg);
            }
        });

    }
}
