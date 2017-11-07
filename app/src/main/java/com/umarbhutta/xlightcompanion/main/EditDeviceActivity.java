package com.umarbhutta.xlightcompanion.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jaygoo.widget.RangeSeekBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.StatusReceiver;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.Scenarionodes;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeviceDetailInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSceneListInfo;
import com.umarbhutta.xlightcompanion.scenario.ColorSelectActivity;
import com.umarbhutta.xlightcompanion.scenario.ScenarioMainFragment;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.CircleDotView;
import com.umarbhutta.xlightcompanion.views.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 * 设置灯
 */

public class EditDeviceActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle;
    public SceneListResult mDeviceInfoResult;
    private Devicenodes deviceInfo;
    private int mPositon;
    private int red = 130;
    private int green = 255;
    private int blue = 0;
    private TextView mscenarioName;

    private static final String TAG = "XLight";

    private static final String DEFAULT_LAMP_TEXT = "LIVING ROOM";
    private static final String RINGALL_TEXT = "ALL RINGS";
    private static final String RING1_TEXT = "RING 1";
    private static final String RING2_TEXT = "RING 2";
    private static final String RING3_TEXT = "RING 3";

    private CheckBox powerSwitch;
    //    private SeekBar brightnessSeekBar;
    private RangeSeekBar brightnessSeekBar;

    private SeekBar cctSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;
    private LinearLayout scenarioNoneLL;
    private ToggleButton ring1Button, ring2Button, ring3Button;
    private TextView deviceRingLabel, powerLabel, brightnessLabel, cctLabel, colorLabel;
    private ImageView lightImageView;

    private LinearLayout llBack;
    private TextView btnSure;
    private LinearLayout linear;

    private LayoutInflater mInflater;

    private ArrayList<String> scenarioDropdown;

    private String colorHex;
    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    private Handler m_handlerControl;
    private CircleDotView circleIcon;
    private TextView cctLabelColor;
    private RelativeLayout rl_scenario;
    private LinearLayout colorLL;
    private View line2;
    private boolean isScrollView = true;
    private boolean codeChange = false;

    private xltDevice mCurrentDevice;
    private HorizontalScrollView mHorizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_control);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        mInflater = LayoutInflater.from(this);

        deviceInfo = (Devicenodes) getIntent().getSerializableExtra("info");
        mPositon = getIntent().getIntExtra("position", 0);

        scenarioDropdown = new ArrayList<>(ScenarioMainFragment.name);
        scenarioDropdown.add(0, "None");

        powerSwitch = (CheckBox) findViewById(R.id.powerSwitch);
        brightnessSeekBar = (RangeSeekBar) findViewById(R.id.brightnessSeekBar);
        cctSeekBar = (SeekBar) findViewById(R.id.cctSeekBar);
        cctSeekBar.setMax(6500 - 2700);
        colorTextView = (TextView) findViewById(R.id.colorTextView);
        scenarioNoneLL = (LinearLayout) findViewById(R.id.scenarioNoneLL);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hor_scroll_view);
        mHorizontalScrollView.setSmoothScrollingEnabled(true);
        scenarioNoneLL.setAlpha(1);
        ring1Button = (ToggleButton) findViewById(R.id.ring1Button);
        ring2Button = (ToggleButton) findViewById(R.id.ring2Button);
        ring3Button = (ToggleButton) findViewById(R.id.ring3Button);
        deviceRingLabel = (TextView) findViewById(R.id.deviceRingLabel);
        brightnessLabel = (TextView) findViewById(R.id.brightnessLabel);
        cctLabel = (TextView) findViewById(R.id.cctLabel);
        powerLabel = (TextView) findViewById(R.id.powerLabel);
        colorLabel = (TextView) findViewById(R.id.colorLabel);
        lightImageView = (ImageView) findViewById(R.id.lightImageView);
        linear = (LinearLayout) findViewById(R.id.ll_horizontal_scrollview);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.edit_device);
        mscenarioName = (TextView) findViewById(R.id.scenarioName);
        mscenarioName.setOnClickListener(this);
        cctLabelColor = (TextView) findViewById(R.id.cctLabelColor);

        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(this, R.layout.control_scenario_spinner_item, scenarioDropdown);
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scenarioSpinner.setAdapter(scenarioAdapter);

        circleIcon = new CircleDotView(this);

        RelativeLayout dotLayout = (RelativeLayout) findViewById(R.id.dotLayout);
        dotLayout.addView(circleIcon);
        rl_scenario = (RelativeLayout) findViewById(R.id.rl_scenario);
        line2 = findViewById(R.id.line2);
        colorLL = (LinearLayout) findViewById(R.id.colorLL);

        if (deviceInfo.devicetype == 1) {
            rl_scenario.setVisibility(View.GONE);
            colorLL.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        } else {
            rl_scenario.setVisibility(View.VISIBLE);
            colorLL.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
        }

        mCurrentDevice = SlidingMenuMainActivity.xltDeviceMaps.get(deviceInfo.coreid);
        mscenarioName.setText(deviceInfo.devicenodename);
        if (mCurrentDevice.getEnableEventSendMessage()) {
            upUIDateAddHandler();
        } else {
            //打开
            mCurrentDevice.setEnableEventSendMessage(true);
            upUIDateAddHandler();
        }
        // 设置并查询
        mCurrentDevice.setDeviceID(deviceInfo.nodeno);
        mCurrentDevice.QueryStatus();//查询所有的状态，通过handler或者广播发送过来。

        findViewById(com.umarbhutta.xlightcompanion.R.id.colorLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed();
            }
        });
        codeChange = true;
        powerSwitch.setChecked(deviceInfo.ison == 1 ? true : false);
        brightnessSeekBar.setValue(deviceInfo.brightness);
        cctSeekBar.setProgress(deviceInfo.cct - 2700);
        if (deviceInfo.cct >= 2700 && deviceInfo.cct < 3500) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.nuan_bai);
        }
        if (deviceInfo.cct >= 3500 && deviceInfo.cct < 5500) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.zhengbai);
        }
        if (deviceInfo.cct >= 5500 && deviceInfo.cct < 6500) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.lengbai);
        }
        codeChange = false;
        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (codeChange)
                    return;
                //check if on or off
                Log.e(TAG, "The power value is " + (isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF));
                state = isChecked;
                int stateInt = mCurrentDevice.PowerSwitch(isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
                Log.e(TAG, "stateInt value is= " + stateInt);
                deviceInfo.ison = isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF;
                GlanceMainFragment.devicenodes.remove(mPositon);
                GlanceMainFragment.devicenodes.add(mPositon, deviceInfo);
            }
        });
        /**
         * 亮度
         */
        brightnessSeekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {

            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (!isFromUser) {
                    if (codeChange)
                        return;
                    Log.e(TAG, "The brightness value is " + (int) min + "view.getCurrentRange()=" + view.getCurrentRange()[0]);
                    int brightnessInt = mCurrentDevice.ChangeBrightness((int) min);
                    Log.e(TAG, "brightnessInt value is= " + brightnessInt);
                    deviceInfo.brightness = (int) min;

                    if (isScrollView) {
                        if (null != viewList && viewList.size() > 0) {
                            viewList.get(0).callOnClick();
                            mHorizontalScrollView.smoothScrollTo(0, 0);
                        }

                    } else {
                        isScrollView = true;
                    }
                }
            }
        });
        /**
         *色温
         */
        cctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    int seekBarProgress = seekBar.getProgress() + 2700;
                    if (seekBarProgress > 2700 && seekBarProgress < 3500) {
                        cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.nuan_bai);
                    }
                    if (seekBarProgress > 3500 && seekBarProgress < 5500) {
                        cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.zhengbai);
                    }
                    if (seekBarProgress > 5500 && seekBarProgress < 6500) {
                        cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.lengbai);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (codeChange)
                    return;
                Log.d(TAG, "The CCT value is " + seekBar.getProgress() + 2700);
                int seekBarProgress = seekBar.getProgress() + 2700;
                int cctInt = mCurrentDevice.ChangeCCT(seekBarProgress);
                deviceInfo.cct = seekBarProgress;
                Log.e(TAG, "cctInt value is= " + cctInt);
                if (isScrollView) {
                    if (null != viewList && viewList.size() > 0) {
                        viewList.get(0).callOnClick();
                        mHorizontalScrollView.smoothScrollTo(0, 0);
                    }
                } else {
                    isScrollView = true;
                }
            }
        });

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString() == "None") {
                    disableEnableControls(true);
                } else {
                    disableEnableControls(false);
                    mCurrentDevice.ChangeScenario(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ring1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring1 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring2 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring3Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring3 = isChecked;
                updateDeviceRingLabel();
            }
        });
        initScenario();//初始化场景
    }

    //获取监听
    private void upUIDateAddHandler() {
        m_handlerControl = new Handler() {
            public void handleMessage(Message msg) {
                Logger.e(TAG, "handler_msg=" + msg.getData().toString());
                codeChange = true;
                int intValue = msg.getData().getInt("State", -255);
                if (intValue != -255) {
                    powerSwitch.setChecked(intValue > 0);
                }

                intValue = msg.getData().getInt("BR", -255);
                if (intValue != -255) {
//                    brightnessSeekBar.setProgress(intValue);
                    brightnessSeekBar.setValue(intValue);
                }
                intValue = msg.getData().getInt("CCT", -255);
                if (intValue != -255) {
                    cctSeekBar.setProgress(intValue - 2700);
                }
                //颜色
                int R = 0;
                int G = 0;
                int B = 0;
                intValue = msg.getData().getInt("R", -255);
                if (intValue != -255) {
                    R = intValue;
                }
                intValue = msg.getData().getInt("G", -255);
                if (intValue != -255) {
                    G = intValue;
                }
                intValue = msg.getData().getInt("B", -255);
                if (intValue != -255) {
                    B = intValue;
                }
                int color = Color.rgb(R, G, B);
                circleIcon.setColor(color);
                colorTextView.setText("RGB(" + R + "," + G + "," + B + ")");
                codeChange = false;
            }
        };
        mCurrentDevice.addDeviceEventHandler(m_handlerControl);
//      updateDeviceRingLabel();//这个作用是 切换灯上部图片的
    }

    private EditText et;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scenarioName:
                String title = getString(R.string.edit_device_name);
                et = (EditText) mInflater.inflate(R.layout.layout_edittext, null);
                new DialogUtils().getEditTextDialog(EditDeviceActivity.this, title, mscenarioName.getText().toString(), new DialogUtils.OnClickOkBtnListener() {
                    @Override
                    public void onClickOk(String editTextStr) {
                        if (TextUtils.isEmpty(editTextStr)) {
                            ToastUtil.showToast(EditDeviceActivity.this, getString(R.string.content_is_null));
                            return;
                        }
                        editDeViceInfo(editTextStr);
                    }
                });
                break;
        }
    }

    @Override
    public void onDestroy() {
        mCurrentDevice.removeDeviceEventHandler(m_handlerControl);
        //mCurrentDevice.Disconnect();
        super.onDestroy();
    }

    private void disableEnableControls(boolean isEnabled) {
        powerSwitch.setEnabled(isEnabled);
        colorTextView.setEnabled(isEnabled);
        brightnessSeekBar.setEnabled(isEnabled);
        cctSeekBar.setEnabled(isEnabled);

        int selectColor = R.color.colorAccent, allLabels = R.color.textColorPrimary;
        if (isEnabled) {
            selectColor = R.color.colorAccent;
            allLabels = R.color.textColorPrimary;
        } else {
            selectColor = R.color.colorDisabled;
            allLabels = R.color.colorDisabled;
        }
        colorTextView.setTextColor(ContextCompat.getColor(this, selectColor));
        powerLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        brightnessLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        cctLabel.setTextColor(ContextCompat.getColor(this, allLabels));
        colorLabel.setTextColor(ContextCompat.getColor(this, allLabels));
    }

    private void updateDeviceRingLabel() {
        String label = mCurrentDevice.getDeviceName();

        if (ring1 && ring2 && ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring123);
        } else if (!ring1 && !ring2 && !ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        } else if (ring1 && ring2) {
            label += ": " + RING1_TEXT + " & " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring12);
        } else if (ring2 && ring3) {
            label += ": " + RING2_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring23);
        } else if (ring1 && ring3) {
            label += ": " + RING1_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring13);
        } else if (ring1) {
            label += ": " + RING1_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring1);
        } else if (ring2) {
            label += ": " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring2);
        } else if (ring3) {
            label += ": " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring3);
        } else {
            label += "";
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        }

        deviceRingLabel.setText(label);
    }

    private void onFabPressed() {
        Intent intent = new Intent(EditDeviceActivity.this, ColorSelectActivity.class);
        startActivityForResult(intent, 1);
    }

    private void initScenario() {
        RequestSceneListInfo.getInstance().getSceneListInfo(this, new RequestSceneListInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final SceneListResult mDeviceInfoResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditDeviceActivity.this.mDeviceInfoResult = mDeviceInfoResult;
                        initSceneList();
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(EditDeviceActivity.this, "" + errMsg);
                    }
                });
            }
        });


        // 场景列表跳到对应的位置
        RequestDeviceDetailInfo.getInstance().getDeviceInfo(this, deviceInfo.id, new RequestDeviceDetailInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(DeviceInfoResult mDeviceInfoResult) {
                Logger.i("详细信息 = " + mDeviceInfoResult.toString());
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, String errMsg) {

            }
        });
    }

    private List<View> viewList = new ArrayList<View>();
    private List<TextView> textViews = new ArrayList<TextView>();

    private void initSceneList() {
        for (int i = 0; i < mDeviceInfoResult.rows.size() + 1; i++) {
            View view;
            TextView textView;
            if (i == 0) {
                view = mInflater.inflate(R.layout.add_scenario_zdy_item,
                        linear, false);
                textView = (TextView) view.findViewById(R.id.textView);
                view.setBackgroundResource(R.drawable.add_scenario_blue_bg);
            } else {
                Rows info = mDeviceInfoResult.rows.get(i - 1);
                view = mInflater.inflate(R.layout.add_scenario_item,
                        linear, false);
                view.setBackgroundResource(R.drawable.add_scenario_bg);
                textView = (TextView) view.findViewById(R.id.sceneName);
                textView.setText(info.scenarioname);
            }

            viewList.add(view);
            textViews.add(textView);
            view.setTag(i);
            view.setOnClickListener(mSceneClick);
            linear.addView(view);
        }

        linear.invalidate();

        if (deviceInfo.devicenodetype != 1 && !TextUtils.isEmpty(deviceInfo.scenarioId)) {
//        if (deviceInfo.devicenodetype != 1) {//测试专用
            for (int i = 0; i < viewList.size(); i++) {
                if (i != 0 && deviceInfo.scenarioId.equals(String.valueOf(mDeviceInfoResult.rows.get(i - 1).id))) {
//                if (i != 0 && "53".equals(String.valueOf(mDeviceInfoResult.rows.get(i - 1).id))) {
                    View cView = viewList.get(i);
                    cView.callOnClick();

                    Message msg = Message.obtain();
                    msg.arg1 = i;

                    handler.sendMessageDelayed(msg, 100);
                    break;

                }
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int index = msg.arg1;

            View cView = viewList.get(index);
            int[] location = new int[2];
            cView.getLocationOnScreen(location);
//            Logger.i("left1 = " + location[0] + ", " + location[1]);
            mHorizontalScrollView.scrollTo(location[0] - 100, 0);
        }
    };

    /**
     * 当前场景
     */
    private Rows curSene = null;

    View.OnClickListener mSceneClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();

            if (0 == index) {
                curSene = null;
            } else {
                Rows sceneInfo = mDeviceInfoResult.rows.get(index - 1);
                curSene = sceneInfo;
                isScrollView = false;
                updateSceneInfo(sceneInfo);
            }

            for (int i = 0; i < viewList.size(); i++) {
                View view = viewList.get(i);
                TextView textView = textViews.get(i);
                view.setBackgroundResource(R.drawable.add_scenario_bg);
                textView.setTextColor(getResources().getColor(R.color.black));
            }

            View mView = viewList.get(index);
            mView.setBackgroundResource(R.drawable.add_scenario_blue_bg);
            TextView mText = textViews.get(index);
            mText.setTextColor(getResources().getColor(R.color.white));
            editDeViceInfo(null);

        }
    };

    /**
     * 选择了某一个场景
     *
     * @param sceneInfo
     */
    private void updateSceneInfo(Rows sceneInfo) {
        if (null == sceneInfo) {
            return;
        }
//        powerSwitch.setChecked((1 == sceneInfo.ison) ? true : false);
        if (mCurrentDevice.isSunny()) {
//            brightnessSeekBar.setProgress(sceneInfo.brightness);
            brightnessSeekBar.setValue(sceneInfo.brightness);
            mCurrentDevice.ChangeBrightness(sceneInfo.brightness);
            cctSeekBar.setProgress(sceneInfo.cct - 2700);
            mCurrentDevice.ChangeCCT(sceneInfo.cct - 2700);
            mCurrentDevice.ChangeScenario(sceneInfo.scenarionodes.get(0).scenarioId);
        } else {
            cctSeekBar.setProgress(sceneInfo.cct - 2700);
            mCurrentDevice.ChangeCCT(sceneInfo.cct - 2700);
            Scenarionodes currentNode = sceneInfo.scenarionodes.get(0);//当前灯
            int R = 0;
            int G = 0;
            int B = 0;
            R = currentNode.R;
            G = currentNode.G;
            B = currentNode.B;
            int color = Color.rgb(R, G, B);
            circleIcon.setColor(color);
            colorTextView.setText("RGB(" + R + "," + G + "," + B + ")");
            mCurrentDevice.ChangeColor(xltDevice.RING_ID_ALL, true, sceneInfo.brightness, 26, currentNode.R, currentNode.G, currentNode.B);
        }
//        brightnessSeekBar.setProgress(sceneInfo.brightness);
//        cctSeekBar.setProgress(sceneInfo.cct - 2700);
        //颜色
//        int R = 0;
//        int G = 0;
//        int B = 0;
//        Scenarionodes currentNode= sceneInfo.scenarionodes.get(0);//当前灯
//        R = currentNode.R;
//        G = currentNode.G;
//        B = currentNode.B;
//        int color = Color.rgb(R, G, B);
//        circleIcon.setColor(color);
//        colorTextView.setText("RGB(" + R + "," + G + "," + B + ")");
    }

    /**
     * 提交编辑设备
     */
    private void editDeViceInfo(String newDeviceName) {
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }

        final String deviceName = TextUtils.isEmpty(newDeviceName) ? mscenarioName.getText().toString() : newDeviceName;
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtil.showToast(this, getString(R.string.please_set_lamp_name));
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ison", powerSwitch.isChecked() ? 1 : 0);
            jsonObject.put("userId", UserUtils.getUserInfo(this).getId());
//            jsonObject.put("devicename", deviceName);。。。。。。。。。
            JSONArray devicenodes = new JSONArray();
            jsonObject.put("devicenodes", devicenodes);

            JSONObject devicenodesJSONObject = new JSONObject();
            devicenodes.put(devicenodesJSONObject);

            devicenodesJSONObject.put("devicenodeId", deviceInfo.id);
            devicenodesJSONObject.put("devicenodename", deviceName);
            devicenodesJSONObject.put("ison", powerSwitch.isChecked() ? 1 : 0);
            if (null == curSene) {
                devicenodesJSONObject.put("scenarioId", 0);  // 场景id,自定义场景
            } else {
                devicenodesJSONObject.put("scenarioId", curSene.id);  // 场景id
            }
            JSONArray deviceringsArr = new JSONArray();
            devicenodesJSONObject.put("devicerings", deviceringsArr);


            for (int i = 0; i < 3; i++) {

                JSONObject deviceringsObj = new JSONObject();
                deviceringsArr.put(deviceringsObj);

                deviceringsObj.put("ison", powerSwitch.isChecked() ? 1 : 0);
                deviceringsObj.put("R", red);
                deviceringsObj.put("G", green);
                deviceringsObj.put("B", blue);
                deviceringsObj.put("color", "rgb(" + red + "," + green + "," + blue + ")");
                deviceringsObj.put("cct", cctSeekBar.getProgress() + 2700);
                deviceringsObj.put("brightness", brightnessSeekBar.getCurrentRange()[0]);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpUtils.getInstance().putRequestInfo(NetConfig.URL_EDIT_DEVICE_INFO + deviceInfo.deviceId + "?access_token=" + UserUtils.getUserInfo(this).getAccess_token(),
                jsonObject.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
                    @Override
                    public void onHttpRequestSuccess(Object result) {
                        Logger.i("编辑成功 = " + result.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                ToastUtil.showToast(EditDeviceActivity.this, getString(R.string.modify_success));
//                                setResult(0);
//                                EditDeviceActivity.this.finish();

                                if (!TextUtils.isEmpty(deviceName)) {
                                    deviceInfo.devicenodename = deviceName;
                                    mscenarioName.setText(deviceName);
                                }

                            }
                        });
                    }

                    @Override
                    public void onHttpRequestFail(int code, final String errMsg) {
                        Logger.i("编辑失败 = " + errMsg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(EditDeviceActivity.this, getString(R.string.modify_fail) + errMsg);

                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            int color = data.getIntExtra("color", -1);
            if (-1 != color) {
                red = (color & 0xff0000) >> 16;
                green = (color & 0x00ff00) >> 8;
                blue = (color & 0x0000ff);
            }
            circleIcon.setColor(color);
            colorTextView.setText("RGB(" + red + "," + green + "," + blue + ")");

            int br = (int) brightnessSeekBar.getCurrentRange()[0];
            int ww = 0;
            mCurrentDevice.ChangeColor(xltDevice.RING_ID_ALL, state, br, ww, red, green, blue);
            mCurrentDevice.setRed(xltDevice.RING_ID_ALL, red);
            mCurrentDevice.setGreen(xltDevice.RING_ID_ALL, green);
            mCurrentDevice.setBlue(xltDevice.RING_ID_ALL, blue);
            if (isScrollView) {
                if (null != viewList && viewList.size() > 0) {
                    viewList.get(0).callOnClick();
                    mHorizontalScrollView.smoothScrollTo(0, 0);
                }
            } else {
                isScrollView = true;
            }
        }
    }
}
