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

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.CommentResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ModifyPwdParam;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

/**
 * Created by Administrator on 2017/3/5.
 */

public class ModifyPasswordActivity extends BaseActivity implements HttpUtils.OnHttpRequestCallBack {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private EditText et_old_passwordTv;
    private EditText et_new_passwordTv;
    private EditText et_new_password_againTv;
    private ProgressDialog dialog;
    private ImageButton ib_clear1,ib_clear2,ib_clear3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
//        getSupportActionBar().hide();
        initViews();
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
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
        tvTitle.setText(R.string.modify_pwd);


        et_old_passwordTv = (EditText) findViewById(R.id.et_old_password);
        et_new_passwordTv = (EditText) findViewById(R.id.et_new_password);
        et_new_password_againTv = (EditText) findViewById(R.id.et_new_password_again);

        ib_clear1 = (ImageButton) findViewById(R.id.ib_clear1);
        ib_clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_old_passwordTv.setText("");
            }
        });
        ib_clear1.setVisibility(View.INVISIBLE);
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

        ib_clear2 = (ImageButton) findViewById(R.id.ib_clear2);
        ib_clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_new_passwordTv.setText("");
            }
        });
        ib_clear2.setVisibility(View.INVISIBLE);
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

        ib_clear3 = (ImageButton) findViewById(R.id.ib_clear3);
        ib_clear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_new_password_againTv.setText("");
            }
        });
        ib_clear3.setVisibility(View.INVISIBLE);
        et_new_password_againTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    ib_clear3.setVisibility(View.INVISIBLE);
                } else {
                    ib_clear3.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void commit() {
        String et_old_passwordTvStr = et_old_passwordTv.getText().toString();
        String et_new_passwordTvStr = et_new_passwordTv.getText().toString();
        String et_new_password_againTvStr = et_new_password_againTv.getText().toString();

        if (TextUtils.isEmpty(et_old_passwordTvStr)) {
            ToastUtil.showToast(this, R.string.input_old_pwd);
            return;
        }

        if (TextUtils.isEmpty(et_new_passwordTvStr)) {
            ToastUtil.showToast(this, R.string.input_new_pwd);
            return;
        }


        if (TextUtils.isEmpty(et_new_password_againTvStr)) {
            ToastUtil.showToast(this, getString(R.string.input_new_pwd_again));
            return;
        }


        if (!et_new_passwordTvStr.equals(et_new_password_againTvStr)) {
            ToastUtil.showToast(this, getString(R.string.two_pwd_not_same));
            return;
        }


        dialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.commit_img));

        ModifyPwdParam param = new ModifyPwdParam(et_old_passwordTvStr, et_new_passwordTvStr);

        Gson gson = new Gson();
        String paramStr = gson.toJson(param);

        HttpUtils.getInstance().putRequestInfo(NetConfig.URL_MODIFY_PWD + UserUtils.getUserInfo(this).getId() + "/resetpassword?access_token=" + UserUtils.getUserInfo(this).getAccess_token(), paramStr, CommentResult.class, this);

    }

    @Override
    public void onHttpRequestSuccess(final Object result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                CommentResult info = (CommentResult) result;
                if (info.code == 1) {
                    ToastUtil.showToast(ModifyPasswordActivity.this, getString(R.string.modify_pwd_success));
                    finish();
                } else {
                    ToastUtil.showToast(ModifyPasswordActivity.this, info.msg);
                }
            }
        });

    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                ToastUtil.showToast(ModifyPasswordActivity.this, R.string.net_error);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
