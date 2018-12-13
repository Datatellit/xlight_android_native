package com.umarbhutta.xlightcompanion.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.help.DeviceInfo;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginParam;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;

public class WelcomActivity extends BaseActivity implements HttpUtils.OnHttpRequestCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_welcom);
        ImmersionBar.with(this).statusBarDarkFont(true).init();
//        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));

        if (UserUtils.isLogin(this)) {
            if (UserUtils.isExpires(this, SharedPreferencesUtils.KEY__USERINFO)) {
                // 即将过期，再次获取用户数据
                LoginResult user = UserUtils.getUserInfo(this);
                LoginParam param = new LoginParam(user.username, user.password, DeviceInfo.getSign(this));
                Gson gson = new Gson();
                String paramStr = gson.toJson(param);
                HttpUtils.getInstance().postRequestInfo(NetConfig.URL_LOGIN, paramStr, LoginResult.class, this);
            } else {
                handler.sendEmptyMessageDelayed(100, 100);
            }
        } else {
            if (!UserUtils.isExpires(this, SharedPreferencesUtils.KEY__ANONYMOUSINFO)) {
                handler.sendEmptyMessageDelayed(100, 100);
            } else {
                anonymousLogin();
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            boolean isFirst = (boolean) SharedPreferencesUtils.getObject(WelcomActivity.this, SharedPreferencesUtils.KEY_IS_FIRST_LUNCH, true);
            SharedPreferencesUtils.putObject(WelcomActivity.this, SharedPreferencesUtils.KEY_IS_FIRST_LUNCH, false);
            if (!isFirst) {
                startActivity(new Intent(WelcomActivity.this, SlidingMenuMainActivity.class));
            } else {
                Intent intent = new Intent(WelcomActivity.this, SplashActivity.class);
                startActivity(intent);
            }
            WelcomActivity.this.finish();
        }
    };

    public void anonymousLogin() {
        //开始匿名登录
        AnonymousParams anonymousParams = UserUtils.getAnonymous(this);
        Gson gson = new Gson();
        String paramStr = gson.toJson(anonymousParams);
        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ANONYMOUS_LOGIN, paramStr, AnonymousResult.class, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                //登录成功，设置到本次的UserUtils对象中
                AnonymousResult ar = (AnonymousResult) result;
                UserUtils.saveAnonymousInfo(getApplicationContext(), ar);
                handler.sendEmptyMessageDelayed(100, 100);
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getBaseContext(), R.string.net_error);
                        handler.sendEmptyMessageDelayed(100, 1000);
                    }
                });
            }
        });
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        // 自动登录失败
        LoginResult info = (LoginResult) result;
        if (info.code == 1) {   //登录成功
            info.data.get(0).setImage(NetConfig.SERVER_ADDRESS_DOMAIN + info.data.get(0).getImage());
            // 保存明码
            LoginResult user = UserUtils.getUserInfo(this);
            info.data.get(0).password = user.password;
            UserUtils.saveUserInfo(this, info.data.get(0));
            handler.sendEmptyMessageDelayed(100, 100);
        } else if (info.code == 40200) {
            // 登录失败，跳转到登录页
            ToastUtil.showToast(this, R.string.session_timeout);
            UserUtils.saveUserInfo(this, null);
            anonymousLogin();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        // 登录失败，跳转到登录页
        ToastUtil.showToast(this, R.string.net_error);
//        UserUtils.saveUserInfo(this, null);
        handler.sendEmptyMessageDelayed(100, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
