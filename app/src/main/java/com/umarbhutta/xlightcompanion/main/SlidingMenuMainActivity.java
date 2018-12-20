package com.umarbhutta.xlightcompanion.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.seismic.ShakeDetector;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.BLE.BLEPairedDeviceList;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.ControlRuleFragment;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.help.HelpFragment;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderOptions;
import com.umarbhutta.xlightcompanion.news.NewsMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ShakeInfo;
import com.umarbhutta.xlightcompanion.room.RoomMainFragment;
import com.umarbhutta.xlightcompanion.rule.RuleMainFragment;
import com.umarbhutta.xlightcompanion.scenario.ScenarioMainFragment;
import com.umarbhutta.xlightcompanion.settings.BaseFragmentActivity;
import com.umarbhutta.xlightcompanion.settings.SettingFragment;
import com.umarbhutta.xlightcompanion.settings.UserMsgModifyActivity;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;
import com.umarbhutta.xlightcompanion.views.CircleImageView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator
 */

public class SlidingMenuMainActivity extends BaseFragmentActivity implements View.OnClickListener {
    private Fragment mContent;

    private SensorManager sensorManager;
    private ShakeDetector sd;
    private Vibrator vibrator;
    public static Map<String, xltDevice> xltDeviceMaps;
    public static xltDevice m_mainDevice;
    public static ShakeInfo mShakeInfo;
    public static DrawerLayout drawerLayout;
    public static RelativeLayout relativeLayoutMenu;
    private CircleImageView userIcon;
    private TextView tv_userName, textView, nav_exit;
    private Button btnLogin;
    private LinearLayout llLogin;
    private long startTime = 0;
    private long lastTime = 0;
    private LinearLayout llPerName;
    private ArrayList<TextView> lstTv;
    private TextView itemGlance, itemControl, itemScenario, itemLogout, itemSettings, itemHelp, itemNews, itemRoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        // 设置主视图界面
        setContentView(R.layout.responsive_content_frame);
//        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= 20) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
//        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        relativeLayoutMenu = (RelativeLayout) findViewById(R.id.ll_menu);
        initSlidingMenu(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        ImmersionBar.with(this).statusBarDarkFont(true).init();
        // Check Bluetooth
        BLEPairedDeviceList.init(this);
        if (!App.isRequestBlue && BLEPairedDeviceList.IsSupported() && !BLEPairedDeviceList.IsEnabled()) {
            App.isRequestBlue = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLEPairedDeviceList.REQUEST_ENABLE_BT);
        }
        xltDeviceMaps = new HashMap<String, xltDevice>();
        if (null == sensorManager) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            sd = new ShakeDetector(new ShakeDetector.Listener() {
                @Override
                public void hearShake() {
                    Log.e("XLight", "Don't shake me, bro!");
                    shakeAction();
                }
            });
            sd.start(sensorManager);
        }
    }

    public void initSlidingMenu(Bundle savedInstanceState) {
        // check if the content frame contains the menu frame
//        if (findViewById(R.id.menu_frame) == null) {
//            setBehindContentView(R.layout.menu_frame);
//            getSlidingMenu().setSlidingEnabled(true);
//            getSlidingMenu()
//                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        } else {
//            // add a dummy view
//            View v = new View(this);
//            setBehindContentView(v);
//            getSlidingMenu().setSlidingEnabled(false);
//            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//        }
        lstTv = new ArrayList<>();
        llPerName = (LinearLayout) findViewById(R.id.llPerName);
        tv_userName = (TextView) findViewById(R.id.tv_userName);
        textView = (TextView) findViewById(R.id.textView);
        btnLogin = (Button) findViewById(R.id.btn_login);
        nav_exit = (TextView) findViewById(R.id.nav_exit);
        userIcon = (CircleImageView) findViewById(R.id.userIcon);

        itemGlance = (TextView) findViewById(R.id.nav_glance);
        itemRoom = (TextView) findViewById(R.id.nav_room);
        itemScenario = (TextView) findViewById(R.id.nav_scenario);
        itemControl = (TextView) findViewById(R.id.nav_control);
        itemSettings = (TextView) findViewById(R.id.nav_settings);
        itemNews = (TextView) findViewById(R.id.nav_news);
        itemHelp = (TextView) findViewById(R.id.nav_help);
        llLogin = (LinearLayout) findViewById(R.id.ll_login);
        itemGlance.setOnClickListener(this);
        itemControl.setOnClickListener(this);
        itemScenario.setOnClickListener(this);
        itemNews.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemRoom.setOnClickListener(this);
        itemHelp.setOnClickListener(this);
        nav_exit.setOnClickListener(this);
        userIcon.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        lstTv.add(itemGlance);
        lstTv.add(itemControl);
        lstTv.add(itemScenario);
        lstTv.add(itemNews);
        lstTv.add(itemSettings);
        lstTv.add(itemRoom);
        lstTv.add(itemHelp);


        // 设置主界面Fragment视图内容
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new GlanceMainFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = drawerLayout.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;//1~0
                content.setTranslationX(menu.getMeasuredWidth() * (1 - scale));//0~width
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        // 默认选中
        noSelected(itemGlance);
        // set the Behind View Fragment

//        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.scene_name);
//
////create the drawer and remember the `Drawer` result object
//        Drawer result = new DrawerBuilder()
//                .withActivity(this)
//                .addDrawerItems(
//                        item1,
//                        new DividerDrawerItem()
//                )
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        // do something with the clicked item :D
//                        return true;
//                    }
//                }).withFullscreen(true)
//                .buildView();

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.menu_frame, new MainMenuFragment()).commit();
//
//        SlidingMenu sm = getSlidingMenu();
//        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        sm.setShadowWidthRes(R.dimen.shadow_width);
//        sm.setShadowDrawable(R.drawable.shadow);
//        sm.setBehindScrollScale(0.25f);
//        sm.setFadeDegree(0.25f);
//        ImmersionBar.with(this).init();
    }

    public void switchContent(final Fragment fragment) {
        Log.e("XLight", "switch content");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mContent.getClass() == fragment.getClass()) {
            return;
        }
//        if (!fragment.isAdded()) {
//            fragmentTransaction.add(R.id.content_frame, fragment);
//        } else {
        fragmentTransaction.replace(R.id.content_frame, fragment);
//        }
        mContent = fragment;
        fragmentTransaction.commit();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                // getSlidingMenu().showContent();
            }
        }, 50);
    }

    public void switchMessage() {
        if (!UserUtils.isLogin(this)) {
//                    showContentView();
            onActivityPressed(LoginActivity.class);
            return;
        }
        noSelected(itemNews);
        Fragment fragment = new NewsMainFragment();//设置
        Log.e("XLight", "click news");//消息
        switchContent(fragment);
    }

    public void onActivityPressed(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLEPairedDeviceList.REQUEST_ENABLE_BT) {
            BLEPairedDeviceList.init(this);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            this.toggle(false);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            ToastUtil.showToast(this, R.string.again_exit);
            startTime = currentTime;
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.btn_login:
//                showContentView();
                onActivityPressed(LoginActivity.class);
                break;
            case R.id.userIcon:
//                showContentView();
                if (!UserUtils.isLogin(this)) {
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                onActivityPressed(UserMsgModifyActivity.class);
                break;
            case R.id.nav_exit:
                showLogoutDialog();
                break;
            case R.id.nav_glance:
                noSelected(itemGlance);
                fragment = new GlanceMainFragment();//首页
                break;
            case R.id.nav_control:
                if (!UserUtils.isLogin(this)) {
//                    showContentView();
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                noSelected(itemControl);
                fragment = new RuleMainFragment();//规则
                break;
            case R.id.nav_scenario:
                if (!UserUtils.isLogin(this)) {
//                    showContentView();
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                noSelected(itemScenario);
                fragment = new ScenarioMainFragment();//场景
                break;
            case R.id.nav_news:
                if (!UserUtils.isLogin(this)) {
//                    showContentView();
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                noSelected(itemNews);
                fragment = new NewsMainFragment();//设置
                Log.e("XLight", "click news");//消息
                break;
            case R.id.nav_settings:
                if (!UserUtils.isLogin(this)) {
//                    showContentView();
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                noSelected(itemSettings);
                fragment = new SettingFragment();//设置
                break;
            case R.id.nav_help:
                noSelected(itemHelp);
                fragment = new HelpFragment();//帮助
                break;
            case R.id.nav_room:
                if (!UserUtils.isLogin(this)) {
//                    showContentView();
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                noSelected(itemRoom);
                fragment = new RoomMainFragment();//房間
                Log.e("XLight", "click room");
                break;
        }
        if (fragment != null) {
            switchContent(fragment);
        }
    }

    public void noSelected(TextView selected) {
        for (TextView tv : lstTv) {
            tv.setBackgroundColor(getResources().getColor(R.color.white));
        }
        selected.setBackgroundColor(getResources().getColor(R.color.menu_selected));
        toggle(false);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getShakeInfo();
        if (UserUtils.isLogin(this)) {
            LoginResult userInfo = UserUtils.getUserInfo(this);
            llLogin.setVisibility(View.GONE);
            llPerName.setVisibility(View.VISIBLE);
            String nickName = UserUtils.getUserInfo(this).getNickname();
            if (null == nickName) {
                nickName = "";
            }
            tv_userName.setText("Welcome, " + nickName);
            textView.setText(UserUtils.getUserInfo(this).getEmail());
            ImageLoader.getInstance().displayImage(userInfo.getImage(), userIcon, ImageLoaderOptions.getImageLoaderOptions());
        } else {
            // ImageLoader.getInstance().displayImage("", userIcon, ImageLoaderOptions.getImageLoaderOptions());
            llLogin.setVisibility(View.VISIBLE);
            llPerName.setVisibility(View.GONE);
        }
    }

    public static void toggle() {
        if (!SlidingMenuMainActivity.drawerLayout.isDrawerOpen(SlidingMenuMainActivity.relativeLayoutMenu)) {
            SlidingMenuMainActivity.drawerLayout.openDrawer(SlidingMenuMainActivity.relativeLayoutMenu);
        } else {
            SlidingMenuMainActivity.drawerLayout.closeDrawer(SlidingMenuMainActivity.relativeLayoutMenu);
        }
    }

    public static void toggle(boolean show) {
        if (!SlidingMenuMainActivity.drawerLayout.isDrawerOpen(SlidingMenuMainActivity.relativeLayoutMenu) && show == true) {
            SlidingMenuMainActivity.drawerLayout.openDrawer(SlidingMenuMainActivity.relativeLayoutMenu);
        } else {
            SlidingMenuMainActivity.drawerLayout.closeDrawer(SlidingMenuMainActivity.relativeLayoutMenu);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShakeInfo = null;
        sd.stop();
        ImmersionBar.with(this).destroy();
    }

    /**
     * 触发摇一摇动作
     */
    private void shakeAction() {
        if (System.currentTimeMillis() - lastTime < 1500) {
            return;
        }
        lastTime = System.currentTimeMillis();
        vibrator.vibrate(200);
        if (!UserUtils.isLogin(this)) {
            return;
        }

        if (null == mShakeInfo) {
            return;
        }

        showProgressDialog(getString(R.string.executing));

        JSONObject object = new JSONObject();
        try {
            object.put("userId", UserUtils.getUserInfo(SlidingMenuMainActivity.this).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 此接口服务器返回异常，应该是code=1为成功，此接口为code=0成功
         */
        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ACTION_SHAKE + UserUtils.getUserInfo(this).access_token, object.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SlidingMenuMainActivity.this.cancelProgressDialog();
                        ToastUtil.showToast(SlidingMenuMainActivity.this, getString(R.string.setting_success));
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SlidingMenuMainActivity.this.cancelProgressDialog();
                        ToastUtil.showToast(SlidingMenuMainActivity.this, getString(R.string.failed));
                    }
                });
            }
        });
    }

    private void getShakeInfo() {

        if (!UserUtils.isLogin(this)) {
            return;
        }

        LoginResult userInfo = UserUtils.getUserInfo(this);

        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_CONFIG_SHAKE_INFO
                + userInfo.access_token + "&userId=" + userInfo.getId(), ShakeInfo.class, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                ShakeInfo mMShakeInfo = (ShakeInfo) result;
//                Logger.i("shake", "shakeInfo = " + mMShakeInfo);
                if (null != mMShakeInfo && null != mMShakeInfo.data && mMShakeInfo.data.size() > 0) {
                    mShakeInfo = mMShakeInfo.data.get(0);
                }

            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
//                Logger.i("shake", "shakeInfo = 失败");
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.set_logout));
        builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!UserUtils.isLogin(getBaseContext())) {
                    onActivityPressed(LoginActivity.class);
                    return;
                }
                // 调用登出
                logout();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * 退出登录
     */
    private void logout() {
        if (GlanceMainFragment.devicenodes != null || GlanceMainFragment.devicenodes.size() > 0) {
            GlanceMainFragment.devicenodes.clear();
        }
        SlidingMenuMainActivity.mShakeInfo = null;
        SharedPreferencesUtils.putObject(this, SharedPreferencesUtils.KEY_DEVICE_LIST, null);
        UserUtils.saveUserInfo(this, null);
        if (UserUtils.isExpires(this, SharedPreferencesUtils.KEY__ANONYMOUSINFO)) {
            //开始匿名登录
            AnonymousParams anonymousParams = UserUtils.getAnonymous(this);
            Gson gson = new Gson();
            String paramStr = gson.toJson(anonymousParams);
            HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ANONYMOUS_LOGIN, paramStr, AnonymousResult.class, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    //登录成功，设置到本次的UserUtils对象中
                    AnonymousResult ar = (AnonymousResult) result;
                    UserUtils.saveAnonymousInfo(getBaseContext(), ar);
                }

                @Override
                public void onHttpRequestFail(int code, String errMsg) {

                }
            });
        }
        //登出后回到首页
        Intent intent = new Intent(this, SlidingMenuMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
