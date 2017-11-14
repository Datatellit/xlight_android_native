package com.umarbhutta.xlightcompanion.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.BLE.BLEPairedDeviceList;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ShakeInfo;
import com.umarbhutta.xlightcompanion.settings.BaseFragmentActivity;
import com.umarbhutta.xlightcompanion.settings.ShakeActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator
 */

public class SlidingMenuMainActivity extends BaseFragmentActivity {
    private Fragment mContent;

    public static Map<String, xltDevice> xltDeviceMaps;
    public static xltDevice m_mainDevice;
    public static ShakeInfo mShakeInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        // 设置主视图界面
        setContentView(R.layout.responsive_content_frame);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }
        initSlidingMenu(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();

        // Check Bluetooth
        BLEPairedDeviceList.init(this);
        if (!App.isRequestBlue && BLEPairedDeviceList.IsSupported() && !BLEPairedDeviceList.IsEnabled()) {
            App.isRequestBlue = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLEPairedDeviceList.REQUEST_ENABLE_BT);
        }

        xltDeviceMaps = new HashMap<String, xltDevice>();
        // Initialize SmartDevice SDK
//        m_mainDevice = new xltDevice();
//        m_mainDevice.Init(this);

        // 测试数据
        // Setup Device/Node List
//        for( int lv_idx = 0; lv_idx < 3; lv_idx++ ) {
//            m_mainDevice.addNodeToDeviceList(deviceNodeIDs[lv_idx], xltDevice.DEFAULT_DEVICE_TYPE, deviceNames[lv_idx]);
//        }
//        m_mainDevice.setDeviceID(deviceNodeIDs[0]);
//
//        // Connect to Controller
//        m_mainDevice.Connect(CloudAccount.DEVICE_ID);

        if (null == sensorManager) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }


        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }

    }

    private void initSlidingMenu(Bundle savedInstanceState) {
        // check if the content frame contains the menu frame
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu()
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        // 设置主界面Fragment视图内容
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new GlanceMainFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        // set the Behind View Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new MainMenuFragment()).commit();

        // 设置滑动菜单的属性值
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);
    }

    public void switchContent(final Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                getSlidingMenu().showContent();
            }
        }, 50);
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
            showContent();
        }
        return super.dispatchKeyEvent(event);
    }

    private long startTime = 0;

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

    private SensorManager sensorManager;
    private Vibrator vibrator;
    private static final int SENSOR_SHAKE = 10;

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getShakeInfo();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShakeInfo = null;
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
//        SharedPreferencesUtils.putObject(this, SharedPreferencesUtils.KEY_DEVICE_LIST, null);
    }

    /**
     * * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
//            Log.i("xlight", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
            //Logger.i("shake", "x = " + Math.abs(x) + ",y = " + y + ",z = " + z);
            int medumValue = 18;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {

                if (!UserUtils.isLogin(SlidingMenuMainActivity.this) || null == mShakeInfo) {
                    return;
                }

                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = SENSOR_SHAKE;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENSOR_SHAKE:
                    //ToastUtil.showToast(ShakeActivity.this, "检测到摇晃，执行操作！");
                    shakeAction();
                    break;
            }
        }

    };

    long lastTime = 0;

    /**
     * 触发摇一摇动作
     */
    private void shakeAction() {

        if (System.currentTimeMillis() - lastTime < 1000) {
            return;
        }
        lastTime = System.currentTimeMillis();

        if (!UserUtils.isLogin(this)) {
            return;
        }

        if (null == mShakeInfo) {
            return;
        }

        showProgressDialog(getString(R.string.commit_img));

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
                        ToastUtil.showToast(SlidingMenuMainActivity.this, getString(R.string.config_success));
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SlidingMenuMainActivity.this.cancelProgressDialog();
                        ToastUtil.showToast(SlidingMenuMainActivity.this, getString(R.string.config_fail));
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

}
