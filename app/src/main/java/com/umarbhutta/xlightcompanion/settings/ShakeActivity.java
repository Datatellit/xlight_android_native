package com.umarbhutta.xlightcompanion.settings;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.activity.dialog.DialogRowNameActivity;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/5.
 */

public class ShakeActivity extends BaseActivity {

    private String TAG = ShakeActivity.class.getSimpleName();

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private Devicenodes curMainNodes;
    private TextView deviceName;
    private CheckBox powerSwitch;
    private CheckBox scene_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
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
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configDeviceInfo();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.shake);
        deviceName = (TextView) findViewById(R.id.scenarioName);
        powerSwitch = (CheckBox) findViewById(R.id.powerSwitch);
        scene_switch = (CheckBox) findViewById(R.id.scene_switch);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (null == SlidingMenuMainActivity.mShakeInfo) {
                    powerSwitch.setChecked(!isChecked);
                    ToastUtil.showToast(ShakeActivity.this, R.string.select_device);
                    return;
                }

                if (isChecked) {
                    if (scene_switch.isChecked()) {
                        scene_switch.setChecked(false);
                    }
                }
            }
        });

        scene_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null == SlidingMenuMainActivity.mShakeInfo) {
                    scene_switch.setChecked(!isChecked);
                    ToastUtil.showToast(ShakeActivity.this, R.string.select_device);
                    return;
                }
                if (isChecked) {
                    if (powerSwitch.isChecked()) {
                        powerSwitch.setChecked(false);
                    }
                }
            }
        });

        findViewById(R.id.scenarioNameLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == GlanceMainFragment.devicenodes || GlanceMainFragment.devicenodes.size() <= 0) {
                    ToastUtil.showToast(ShakeActivity.this, getString(R.string.no_device));
                    return;
                }

                Intent intent = new Intent(ShakeActivity.this, DialogRowNameActivity.class);
                startActivityForResult(intent, 29);
            }
        });


        if (null != SlidingMenuMainActivity.mShakeInfo) {
            deviceName.setText("" + SlidingMenuMainActivity.mShakeInfo.devicenodename);
            if (1 == SlidingMenuMainActivity.mShakeInfo.shakeaction) {
                scene_switch.setChecked(false);
                powerSwitch.setChecked(true);
            } else {
                powerSwitch.setChecked(false);
                scene_switch.setChecked(true);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 35:

                Devicenodes mDevicenodes = (Devicenodes) data.getSerializableExtra("deviceInfo");

                String coreId = getCoreId(mDevicenodes);
                if (TextUtils.isEmpty(coreId)) {
                    ToastUtil.showToast(ShakeActivity.this, getString(R.string.do_not_supoort_shake));
                    return;
                }

                curMainNodes = mDevicenodes;
                deviceName.setText("" + curMainNodes.devicenodename);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * 配置设备信息
     */
    private void configDeviceInfo() {

        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }
        if (curMainNodes == null) {

            if (null != SlidingMenuMainActivity.mShakeInfo) {
                ToastUtil.showToast(this, R.string.config_success);
                return;
            }

            ToastUtil.showToast(this, R.string.select_device);
            return;
        }
        showProgressDialog(getString(R.string.commit_img));

        LoginResult userInfo = UserUtils.getUserInfo(this);

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userInfo.id);
            object.put("deviceId", curMainNodes.deviceId);
            object.put("devicenodeId", curMainNodes.id);
            object.put("devicenodename", curMainNodes.devicenodename);
            object.put("coreid", getCoreId(curMainNodes));
            object.put("shakeaction", powerSwitch.isChecked() ? 1 : 2);  //摇一摇要触发的动作。1：切换开关；2：切换场景

        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().postRequestInfo(NetConfig.URL_CONFIG_SHAKE_INFO + userInfo.access_token, object.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShakeActivity.this.cancelProgressDialog();
                        ToastUtil.showToast(ShakeActivity.this, R.string.config_success);
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShakeActivity.this.cancelProgressDialog();
                        ToastUtil.showToast(ShakeActivity.this, R.string.config_fail);
                    }
                });
            }
        });
    }

    /**
     * 获取灯所在设备的coreid
     *
     * @return
     */
    private String getCoreId(Devicenodes curMainNodes) {
        if (null != GlanceMainFragment.deviceList && GlanceMainFragment.deviceList.size() > 0) {
            for (Rows rows : GlanceMainFragment.deviceList) {
                if (curMainNodes.deviceId == rows.id) {
                    return rows.coreid;
                }
            }
        }
        return null;
    }

}
