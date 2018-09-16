package com.umarbhutta.xlightcompanion.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.event.ColorEvent;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.scenario.ColorSelectActivity;
import com.umarbhutta.xlightcompanion.scenario.ScenarioMainFragment;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.CircleDotView;
import com.umarbhutta.xlightcompanion.views.DialogUtils;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 * 设置灯
 */

public class EditDeviceActivity_bak extends BaseActivity implements View.OnClickListener {
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
    private BubbleSeekBar brightnessSeekBar;

    private SeekBar cctSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;
    private ToggleButton ring1Button, ring2Button, ring3Button;
    private TextView deviceRingLabel;
    private ImageView lightImageView;

    private LinearLayout llBack;
    private TextView btnSure;

    private LayoutInflater mInflater;

    private ArrayList<String> scenarioDropdown;

    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    private Handler m_handlerControl;
    private CircleDotView circleIcon;
    private TextView cctLabelColor;
    private boolean isScrollView = true;
    private boolean codeChange = false;

    private xltDevice mCurrentDevice;
    private HorizontalScrollView mHorizontalScrollView;
    private int selectColor = 0;

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
        brightnessSeekBar = (BubbleSeekBar) findViewById(R.id.brightnessSeekBar);
        cctSeekBar = (SeekBar) findViewById(R.id.cctSeekBar);
        cctSeekBar.setMax(6500 - 3000);
        colorTextView = (TextView) findViewById(R.id.colorTextView);
        ring1Button = (ToggleButton) findViewById(R.id.ring1Button);
        ring2Button = (ToggleButton) findViewById(R.id.ring2Button);
        ring3Button = (ToggleButton) findViewById(R.id.ring3Button);
        deviceRingLabel = (TextView) findViewById(R.id.deviceRingLabel);
        lightImageView = (ImageView) findViewById(R.id.lightImageView);
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

        if (deviceInfo.devicetype == 1) {
            ((LinearLayout) findViewById(R.id.colorLL)).setVisibility(View.GONE);
            findViewById(R.id.line3).setVisibility(View.GONE);
            //((LinearLayout) findViewById(R.id.colorTemperature)).setVisibility(View.VISIBLE);
            //findViewById(R.id.line8).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) findViewById(R.id.colorLL)).setVisibility(View.VISIBLE);
            findViewById(R.id.line3).setVisibility(View.VISIBLE);
            //((LinearLayout) findViewById(R.id.colorTemperature)).setVisibility(View.GONE);
            //findViewById(R.id.line8).setVisibility(View.GONE);
        }

        mCurrentDevice = SlidingMenuMainActivity.xltDeviceMaps.get(deviceInfo.coreid);
        if (mCurrentDevice == null)
            return;
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
        brightnessSeekBar.setProgress(deviceInfo.brightness);
        cctSeekBar.setProgress(deviceInfo.cct - 3000);
        if (deviceInfo.cct >= 3000 && deviceInfo.cct < 4000) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.nuan_bai);
        }
        if (deviceInfo.cct >= 4000 && deviceInfo.cct < 5500) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.zhengbai);
        }
        if (deviceInfo.cct >= 5500 && deviceInfo.cct < 6500) {
            cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.lengbai);
        }
        if (deviceInfo.devicetype > 1 && deviceInfo.color.length > 1) {
            circleIcon.setColor(Color.rgb(deviceInfo.color[0], deviceInfo.color[1], deviceInfo.color[2]));
            colorTextView.setText("RGB(" + deviceInfo.color[0] + "," + deviceInfo.color[1] + "," + deviceInfo.color[2] + ")");
            selectColor = Color.rgb(deviceInfo.color[0], deviceInfo.color[1], deviceInfo.color[2]);
            Log.e("XLight", deviceInfo.toString());
            Log.e("XLight", Arrays.toString(deviceInfo.color));
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
        brightnessSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if (codeChange)
                    return;
                Log.e(TAG, "The brightness value is " + progress);
                int brightnessInt = mCurrentDevice.ChangeBrightness(progress);
                Log.e(TAG, "brightnessInt value is= " + brightnessInt);
                deviceInfo.brightness = progress;

                if (isScrollView) {
                    if (null != viewList && viewList.size() > 0) {
                        viewList.get(0).callOnClick();
                        mHorizontalScrollView.smoothScrollTo(0, 0);
                    }

                } else {
                    isScrollView = true;
                }
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
        /**
         *色温
         */
        cctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "The CCT value is " + seekBar.getProgress() + 3000);
                int seekBarProgress = seekBar.getProgress() + 3000;
                if (seekBarProgress >= 3000 && seekBarProgress < 4500) {
                    cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.nuan_bai);
                }
                if (seekBarProgress >= 4500 && seekBarProgress < 5500) {
                    cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.zhengbai);
                }
                if (seekBarProgress >= 5500 && seekBarProgress < 6500) {
                    cctLabelColor.setText(com.umarbhutta.xlightcompanion.R.string.lengbai);
                }
                if (codeChange)
                    return;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ColorEvent colorEvent) {
        int color = colorEvent.color;
        if (-1 != color && color != selectColor) {
            red = (color & 0xff0000) >> 16;
            green = (color & 0x00ff00) >> 8;
            blue = (color & 0x0000ff);
        } else {
            return;
        }
        Log.e("XLight", "select color return");
        circleIcon.setColor(color);
        colorTextView.setText("RGB(" + red + "," + green + "," + blue + ")");

        int br = brightnessSeekBar.getProgress();
        int ww = 0;
        mCurrentDevice.ChangeColor(xltDevice.RING_ID_ALL, true, br, ww, red, green, blue);
        mCurrentDevice.setRed(xltDevice.RING_ID_ALL, red);
        mCurrentDevice.setGreen(xltDevice.RING_ID_ALL, green);
        mCurrentDevice.setBlue(xltDevice.RING_ID_ALL, blue);
        this.selectColor = color;
        if (isScrollView) {
            if (null != viewList && viewList.size() > 0) {
                viewList.get(0).callOnClick();
                mHorizontalScrollView.smoothScrollTo(0, 0);
            }
        } else {
            isScrollView = true;
        }
    }

    public int[] rgb2rgbw(int r, int g, int b) {
        //'Ri', 'Gi', and 'Bi' correspond to the Red, Green, and Blue inputs.
        int M = Math.max(r, Math.max(g, b)); //The maximum value between R,G, and B.
        int Wo = 0; //White output
        int Ro = 0; //Red output
        int Go = 0; //Green output
        int Bo = 0; //Blue output

        int av = 0; //Average between the two minimum values
        int hR = 0; //Red with 100% hue
        int hG = 0; //Green with 100% hue
        int hB = 0; //Blue with 100% hue

        //These 4 lines serve to figure out what the input color is with 100% hue.
        float multiplier = 255.0f / M;
        hR = (int) (r * multiplier);
        hG = (int) (g * multiplier);
        hB = (int) (b * multiplier);

        //Depending on the maximum value, get an average of the least used colors, weighted for their importance in the overall hue.
        //This is the problematic part
        if (M == r)
            av = (b * hB + g * hG) / (hB + hG);
        else if (M == g)
            av = (r * hR + b * hB) / (hR + hB);
        else if (M == b)
            av = (g * hG + r * hR) / (hG + hR);

        //Set the rgbw colors
        Wo = av;
        Bo = b - av;
        Ro = r - av;
        Go = g - av;
        if (Wo < 1) Wo = 0;
        if (Bo < 1) Bo = 0;
        if (Ro < 1) Ro = 0;
        if (Go < 1) Go = 0;
        if (Wo > 255) Wo = 255;
        if (Bo > 255) Bo = 255;
        if (Ro > 255) Ro = 255;
        if (Go > 255) Go = 255;
        int[] array = {Wo, Ro, Go, Bo};
        return array;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //获取监听
    private void upUIDateAddHandler() {
        m_handlerControl = new Handler() {
            public void handleMessage(Message msg) {
                Logger.e(TAG, "handler_msg=" + msg.getData().toString());
                if (deviceInfo.coreid.equals(msg.getData().getString("coreId")) && deviceInfo.nodeno == msg.getData().getInt("nd", -1)) {
                    codeChange = true;
                    int intValue = msg.getData().getInt("State", -255);
                    if (intValue != -255) {
                        powerSwitch.setChecked(intValue > 0);
                        deviceInfo.ison = intValue > 0 ? xltDevice.STATE_ON : xltDevice.STATE_OFF;
                    }

                    intValue = msg.getData().getInt("BR", -255);
                    if (intValue != -255) {
//                    brightnessSeekBar.setProgress(intValue);
                        brightnessSeekBar.setProgress(intValue);
                        deviceInfo.brightness = intValue;
                    }
                    intValue = msg.getData().getInt("CCT", -255);
                    if (intValue != -255) {
                        deviceInfo.cct = intValue;
                        cctSeekBar.setProgress(intValue - 3000);
                    }
                    //颜色
                    int R = -255;
                    int G = -255;
                    int B = -255;
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
                    if (R != -255 && G != -255 && B != -255) {
                        int[] tmpColor = {R, G, B};
                        deviceInfo.color = tmpColor;
                        int color = Color.rgb(R, G, B);
                        circleIcon.setColor(color);
                        selectColor = color;
                        Log.d("XLight", "update auto color return");
                        colorTextView.setText("RGB(" + R + "," + G + "," + B + ")");
                    }
                    GlanceMainFragment.devicenodes.remove(mPositon);
                    GlanceMainFragment.devicenodes.add(mPositon, deviceInfo);
                    codeChange = false;
                }
            }
        };
        mCurrentDevice.addDeviceEventHandler(m_handlerControl);
    }

    private EditText et;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scenarioName:
                String title = getString(R.string.edit_device_name);
                et = (EditText) mInflater.inflate(R.layout.layout_edittext, null);
                new DialogUtils().getEditTextDialog(EditDeviceActivity_bak.this, title, mscenarioName.getText().toString(), new DialogUtils.OnClickOkBtnListener() {
                    @Override
                    public void onClickOk(String editTextStr) {
                        if (TextUtils.isEmpty(editTextStr)) {
                            ToastUtil.showToast(EditDeviceActivity_bak.this, getString(R.string.content_is_null));
                            return;
                        }
                        editDeViceInfo(editTextStr);
                    }
                });
                break;
        }

    }

    public void onFilterClick(View view) {
        Log.e("XLight", Integer.toString(view.getId()));
        int filter = 0;
        switch (view.getId()) {
            case R.id.filter_one:
                filter = 1;
                break;
            case R.id.filter_two:
                filter = 2;
                break;
            case R.id.filter_three:
                filter = 3;
                break;
            case R.id.filter_four:
                filter = 4;
                break;
        }
        if (filter != 0) {
            mCurrentDevice.SetSpecialEffect(filter);
        }
    }

    @Override
    public void onDestroy() {
        if (mCurrentDevice != null)
            mCurrentDevice.removeDeviceEventHandler(m_handlerControl);
        //mCurrentDevice.Disconnect();
        super.onDestroy();
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
        Intent intent = new Intent(EditDeviceActivity_bak.this, ColorSelectActivity.class);
        intent.putExtra("color", selectColor);
        startActivity(intent);
    }

    private List<View> viewList = new ArrayList<View>();

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
            jsonObject.put("devicenodename", deviceName);
            jsonObject.put("ison", powerSwitch.isChecked() ? 1 : 0);
            jsonObject.put("cct", cctSeekBar.getProgress() + 3000);
            jsonObject.put("brightness", brightnessSeekBar.getProgress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_EDIT_DEVICE_INFO, deviceInfo.id, UserUtils.getAccessToken(this)),
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
                                ToastUtil.showToast(EditDeviceActivity_bak.this, getString(R.string.modify_fail) + errMsg);

                            }
                        });
                    }
                });
    }
}
