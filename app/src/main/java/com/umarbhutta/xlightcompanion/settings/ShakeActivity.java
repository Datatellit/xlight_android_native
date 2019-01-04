package com.umarbhutta.xlightcompanion.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
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
import com.umarbhutta.xlightcompanion.okHttp.model.ShakeInfo;

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
    private LinearLayout ll_all;
    private CheckBox chkEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
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
        chkEnable = (CheckBox) findViewById(R.id.chkEnabled);
        ll_all = (LinearLayout) findViewById(R.id.ll_all);
        powerSwitch = (CheckBox) findViewById(R.id.powerSwitch);
        scene_switch = (CheckBox) findViewById(R.id.scene_switch);

        chkEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 如果是禁用，那么就隐藏，否则显示
                if (isChecked) {
                    ll_all.setVisibility(View.VISIBLE);
                } else {
                    ll_all.setVisibility(View.GONE);
                }
            }
        });

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                scene_switch.setChecked(!isChecked);
            }
        });

        scene_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                powerSwitch.setChecked(!isChecked);
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
            chkEnable.setChecked(true);
            if (1 == SlidingMenuMainActivity.mShakeInfo.shakeaction) {
                scene_switch.setChecked(false);
                powerSwitch.setChecked(true);
            } else {
                powerSwitch.setChecked(false);
                scene_switch.setChecked(true);
            }
            curMainNodes = new Devicenodes();
            curMainNodes.devicenodename = SlidingMenuMainActivity.mShakeInfo.devicenodename;
            curMainNodes.deviceId = SlidingMenuMainActivity.mShakeInfo.deviceId;
            curMainNodes.id = SlidingMenuMainActivity.mShakeInfo.devicenodeId;
            curMainNodes.coreid = SlidingMenuMainActivity.mShakeInfo.coreid;
        } else {
            // 隐藏响应信息
            ll_all.setVisibility(View.GONE);
            chkEnable.setChecked(false);
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

        LoginResult userInfo = UserUtils.getUserInfo(this);
        if (!chkEnable.isChecked()) {
            // 需要删除
            if (null == SlidingMenuMainActivity.mShakeInfo) {
                ToastUtil.showToast(this, R.string.config_success);
                return;
            }
            HttpUtils.getInstance().deleteRequestInfo(String.format(NetConfig.URL_DELETE_CONFIG_SHAKE_INFO, SlidingMenuMainActivity.mShakeInfo.id) + userInfo.access_token, "", null, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SlidingMenuMainActivity.mShakeInfo = null;
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
            return;
        }
        if (curMainNodes == null) {
            ToastUtil.showToast(this, R.string.select_device);
            return;
        }
        showProgressDialog(getString(R.string.commit_img));

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
                        updateShakeInfo();
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

    private void updateShakeInfo() {

        if (!UserUtils.isLogin(this)) {
            return;
        }

        LoginResult userInfo = UserUtils.getUserInfo(this);

        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_CONFIG_SHAKE_INFO
                + userInfo.access_token + "&userId=" + userInfo.getId(), ShakeInfo.class, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                ShakeInfo mMShakeInfo = (ShakeInfo) result;
                if (null != mMShakeInfo && null != mMShakeInfo.data && mMShakeInfo.data.size() > 0) {
                    SlidingMenuMainActivity.mShakeInfo = mMShakeInfo.data.get(0);
                } else {
                    SlidingMenuMainActivity.mShakeInfo = null;
                }
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                SlidingMenuMainActivity.mShakeInfo = null;
            }
        });
    }

    /**
     * 获取灯所在设备的coreid
     *
     * @return
     */
    private String getCoreId(Devicenodes curMainNodes) {
        if (curMainNodes.coreid != null) {
            return curMainNodes.coreid;
        }
        if (null != GlanceMainFragment.deviceList && GlanceMainFragment.deviceList.size() > 0) {
            for (Rows rows : GlanceMainFragment.deviceList) {
                if (curMainNodes.deviceId == rows.id) {
                    return rows.coreid;
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
