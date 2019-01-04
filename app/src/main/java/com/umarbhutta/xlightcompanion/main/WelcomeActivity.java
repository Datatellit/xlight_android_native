package com.umarbhutta.xlightcompanion.main;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
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

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 11) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
//        }
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_welcom);
        ImmersionBar.with(this).statusBarDarkFont(true).init();
//        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        this.selfPermissionGranted(this, new PermissionCallback() {
            @Override
            public void hasPermission() {
                if (UserUtils.isLogin(getBaseContext())) {
                    if (UserUtils.isExpires(getBaseContext(), SharedPreferencesUtils.KEY__USERINFO)) {
                        // 即将过期，再次获取用户数据
                        LoginResult user = UserUtils.getUserInfo(getBaseContext());
                        LoginParam param = new LoginParam(user.username, user.password, DeviceInfo.getSign(getBaseContext()));
                        Gson gson = new Gson();
                        String paramStr = gson.toJson(param);
                        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_LOGIN, paramStr, LoginResult.class, new HttpUtils.OnHttpRequestCallBack() {
                            @Override
                            public void onHttpRequestSuccess(Object result) {
                                // 自动登录失败
                                LoginResult info = (LoginResult) result;
                                if (info.code == 1) {   //登录成功
                                    info.data.get(0).setImage(NetConfig.SERVER_ADDRESS_DOMAIN + info.data.get(0).getImage());
                                    // 保存明码
                                    LoginResult user = UserUtils.getUserInfo(getBaseContext());
                                    info.data.get(0).password = user.password;
                                    UserUtils.saveUserInfo(getBaseContext(), info.data.get(0));
                                    handler.sendEmptyMessageDelayed(100, 100);
                                } else if (info.code == 40200) {
                                    // 登录失败，跳转到登录页
                                    ToastUtil.showToast(getBaseContext(), R.string.session_timeout);
                                    UserUtils.saveUserInfo(getBaseContext(), null);
                                    anonymousLogin();
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onHttpRequestFail(int code, String errMsg) {
                                // 登录失败，跳转到登录页
                                ToastUtil.showToast(getBaseContext(), R.string.code_login_fail);
                                UserUtils.saveUserInfo(getBaseContext(), null);
                                handler.sendEmptyMessageDelayed(100, 1000);
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        handler.sendEmptyMessageDelayed(100, 100);
                    }
                } else {
                    if (!UserUtils.isExpires(getBaseContext(), SharedPreferencesUtils.KEY__ANONYMOUSINFO)) {
                        handler.sendEmptyMessageDelayed(100, 100);
                    } else {
                        anonymousLogin();
                    }
                }
            }

            @Override
            public void noPermission() {
                ToastUtil.showToast(getBaseContext(), R.string.defaule_first_message);
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            boolean isFirst = (boolean) SharedPreferencesUtils.getObject(WelcomeActivity.this, SharedPreferencesUtils.KEY_IS_FIRST_LUNCH, true);
            SharedPreferencesUtils.putObject(WelcomeActivity.this, SharedPreferencesUtils.KEY_IS_FIRST_LUNCH, false);
            if (!isFirst) {
                startActivity(new Intent(WelcomeActivity.this, SlidingMenuMainActivity.class));
            } else {
                Intent intent = new Intent(WelcomeActivity.this, SplashActivity.class);
                startActivity(intent);
            }
            WelcomeActivity.this.finish();
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
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
