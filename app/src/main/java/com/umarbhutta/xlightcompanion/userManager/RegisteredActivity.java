package com.umarbhutta.xlightcompanion.userManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginParam;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RegisteResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RigsteParam;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/4.
 */

public class RegisteredActivity extends BaseActivity implements View.OnClickListener, HttpUtils.OnHttpRequestCallBack {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private EditText et_user_account;
    private EditText et_user_password;
    private EditText et_verification_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registerd);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        initViews();
        getHelpUrl();
    }

    private void initViews() {
        et_user_account = (EditText) findViewById(R.id.et_user_account);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        et_user_password.setHint(R.string.input_pwd);
        et_verification_code = (EditText) findViewById(R.id.et_verification_code);
        et_verification_code.setVisibility(View.GONE);
        findViewById(R.id.btn_finash_registered).setOnClickListener(this);
        findViewById(R.id.tv_protocol).setOnClickListener(this);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisteredActivity.this, SlidingMenuMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setText(R.string.close);
        btnSure.setVisibility(View.INVISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.regist);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((App) getApplicationContext()).finishActivity();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_finash_registered:
                //
                registe();
                break;
            case R.id.tv_protocol:
                //
                onFabPressed();
                break;
        }
    }

    private void onFabPressed() {
        Intent intent = new Intent(RegisteredActivity.this, UserResProtocalActivity.class);
        intent.putExtra("url", url);
        startActivityForResult(intent, 1);
    }

    private void registe() {
        String et_user_accountStr = et_user_account.getText().toString();
        String et_user_passwordStr = et_user_password.getText().toString();

        if (TextUtils.isEmpty(et_user_accountStr)) {
            ToastUtil.showToast(this, getString(R.string.account_is_null));
            return;
        }

        if (TextUtils.isEmpty(et_user_passwordStr)) {
            ToastUtil.showToast(this, getString(R.string.password_is_null));
            return;
        }

        if (!StringUtil.isEmail(et_user_accountStr)) {
            ToastUtil.showToast(this, R.string.email_error);
            return;
        }

        showProgressDialog(getString(R.string.regist));

        RigsteParam param = new RigsteParam(et_user_accountStr, et_user_passwordStr);

        Gson gson = new Gson();
        String paramStr = gson.toJson(param);

        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_REGISTER, paramStr, RegisteResult.class, this);
    }

    @Override
    public void onHttpRequestSuccess(final Object result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RegisteResult info = (RegisteResult) result;
                if (info.code == 0) {
                    RegisteredActivity.this.cancelProgressDialog();
                    ToastUtil.showToast(RegisteredActivity.this, info.msg);
                } else if (1 == info.code) {
//                    ToastUtil.showToast(RegisteredActivity.this, getString(R.string.registe_success));
                    setResult(10086);
                    login();
                } else {
                    RegisteredActivity.this.cancelProgressDialog();
                    ToastUtil.showToast(RegisteredActivity.this, getString(R.string.net_error));
                }
            }
        });
    }

    /**
     * 注册完成之后调用登录接口
     */
    private void login() {
        String et_user_accountStr = et_user_account.getText().toString();
        String et_user_passwordStr = et_user_password.getText().toString();

        LoginParam param = new LoginParam(et_user_accountStr, et_user_passwordStr);

        Gson gson = new Gson();
        String paramStr = gson.toJson(param);

        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_LOGIN, paramStr, LoginResult.class, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(final Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RegisteredActivity.this.cancelProgressDialog();
                        LoginResult info = (LoginResult) result;
                        if (info.code == 1) {   //登录成功
                            info.data.get(0).setImage(NetConfig.SERVER_ADDRESS_DOMAIN + info.data.get(0).getImage());
                            UserUtils.saveUserInfo(RegisteredActivity.this, info.data.get(0));
                            ToastUtil.showToast(RegisteredActivity.this, getString(R.string.registe_success));
                            finish();
                            startActivity(new Intent(RegisteredActivity.this, SlidingMenuMainActivity.class));
                        } else if (info.code == 0) {  //登录失败，提示服务端返回的信息
                            ToastUtil.showToast(RegisteredActivity.this, info.msg);
                        } else {
                            ToastUtil.showToast(RegisteredActivity.this, getString(R.string.net_error));
                        }
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RegisteredActivity.this.cancelProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public void onHttpRequestFail(int code, final String errMsg) {
        Logger.i("login fail = ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RegisteredActivity.this.cancelProgressDialog();
                ToastUtil.showToast(RegisteredActivity.this, "" + errMsg);
            }
        });
    }

    public String url;

    /**
     * 获取注册协议的url
     */
    public void getHelpUrl() {
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_REGISTER_URL, null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(final Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject((String) result);
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            url = dataObj.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, final String errMsg) {

            }
        });
    }


}
