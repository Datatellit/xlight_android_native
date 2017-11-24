package com.umarbhutta.xlightcompanion.control.activity.result;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaygoo.widget.RangeSeekBar;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.activity.dialog.DialogRowNameActivity;
import com.umarbhutta.xlightcompanion.control.bean.ControlRuleDevice;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmdfield;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSceneListInfo;
import com.umarbhutta.xlightcompanion.scenario.ColorSelectActivity;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.CircleDotView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 * 设置灯
 */

public class DeviceControlSelectActivity extends BaseActivity {

    private String TAG = DeviceControlSelectActivity.class.getSimpleName();
    private TextView tvTitle;
    private Actioncmd mActioncmd;

    private Devicenodes curMainNodes;
    private TextView cctLabelColor;
    private TextView lampName;
    private View rl_scenario;
    private View colorLL;
    private boolean isScrollView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_control);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        mInflater = LayoutInflater.from(this);
        ((App) getApplicationContext()).setActivity(this);

        mActioncmd = (Actioncmd) getIntent().getSerializableExtra("MACTIONCMD");

        powerSwitch = (CheckBox) findViewById(R.id.powerSwitch);
        brightnessSeekBar = (RangeSeekBar) findViewById(R.id.brightnessSeekBar);
        cctSeekBar = (SeekBar) findViewById(R.id.cctSeekBar);
        cctSeekBar.setMax(6500 - 2700);
        colorTextView = (TextView) findViewById(R.id.colorTextView);
        scenarioNoneLL = (LinearLayout) findViewById(R.id.scenarioNoneLL);
        scenarioNoneLL.setAlpha(1);
//        ring1Button = (ToggleButton) findViewById(R.id.ring1Button);
//        ring2Button = (ToggleButton) findViewById(R.id.ring2Button);
//        ring3Button = (ToggleButton) findViewById(R.id.ring3Button);
//        deviceRingLabel = (TextView) findViewById(R.id.deviceRingLabel);
        brightnessLabel = (TextView) findViewById(R.id.brightnessLabel);
        cctLabel = (TextView) findViewById(R.id.cctLabel);
        powerLabel = (TextView) findViewById(R.id.powerLabel);
        colorLabel = (TextView) findViewById(R.id.colorLabel);
        lightImageView = (ImageView) findViewById(R.id.lightImageView);
        linear = (LinearLayout) findViewById(R.id.ll_horizontal_scrollview);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hor_scroll_view);
        mHorizontalScrollView.setSmoothScrollingEnabled(true);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        rl_scenario = findViewById(R.id.rl_scenario);
        colorLL = findViewById(R.id.colorLL);
        circleIcon = new CircleDotView(this);
        RelativeLayout dotLayout = (RelativeLayout) findViewById(R.id.dotLayout);
        dotLayout.addView(circleIcon);

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定提交按钮 先判断是否控制了。
                if (TextUtils.isEmpty(lampName.getText().toString())) {
                    ToastUtil.showToast(DeviceControlSelectActivity.this, getString(R.string.select_device));
                    return;
                }
//                ControlRuleDevice mContolRuleDevice = new ControlRuleDevice();
//                mContolRuleDevice.roomName = lampName.getText().toString();
//                mContolRuleDevice.brightness = brightnessSeekBar.getProgress();
//                mContolRuleDevice.cct = cctSeekBar.getProgress();
//                mContolRuleDevice.statues = (state == false ? "关" : "开");
//                mContolRuleDevice.actiontype = 1;

                NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
                mActioncmd.actioncmdType  =1;
                mNewRuleItemInfo.setmActioncmd(mActioncmd);
                AddControlRuleActivity.mNewRuleResultInfoList.add(mNewRuleItemInfo);
                ((App) getApplicationContext()).finishActivity();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.select_device);

        powerSwitch.setChecked(true);
        brightnessSeekBar.setValue(20);
        cctSeekBar.setProgress(10);

        spinner = (ImageView) findViewById(R.id.spinner);
        findViewById(R.id.scenarioNameLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null == GlanceMainFragment.devicenodes || GlanceMainFragment.devicenodes.size()<=0){
                    ToastUtil.showToast(DeviceControlSelectActivity.this,getString(R.string.no_device));
                    return;
                }

                Intent intent = new Intent(DeviceControlSelectActivity.this, DialogRowNameActivity.class);
                startActivityForResult(intent, 29);
            }
        });

//        spinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DeviceControlSelectActivity.this, DialogRowNameActivity.class);
//                startActivityForResult(intent, 29);
//            }
//        });

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check if on or off
                state = isChecked;
                //ParticleAdapter.JSONCommandPower(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                // 测试sdk 这里的id 需要确定一下。 deviceList.get(mPositon).id 这里的id代表什么意思。
//                if (null != SlidingMenuMainActivity.m_mainDevice && null != curMainRows) {
//                    SlidingMenuMainActivity.m_mainDevice.setDeviceID(curMainRows.id);
//                    SlidingMenuMainActivity.m_mainDevice.PowerSwitch(isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
//                }
            }
        });

        findViewById(R.id.colorLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed();
            }
        });
        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed();
            }
        });
        cctLabelColor = (TextView) findViewById(R.id.cctLabelColor);
        lampName = (TextView) findViewById(R.id.scenarioName);
        /**
         * 亮度
         */
        brightnessSeekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener(){

            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                //
                Log.e(TAG, "The brightness value is " + (int)min+"view.getCurrentRange()="+view.getCurrentRange()[0]);
                if (!isFromUser) {
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
//        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.e(TAG, "The brightness value is " + seekBar.getProgress());
//                //ParticleAdapter.JSONCommandBrightness(ParticleAdapter.DEFAULT_DEVICE_ID, seekBar.getProgress());
//            }
//        });

        cctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "The CCT value is " + seekBar.getProgress() + 2700);
                int seekBarProgress = seekBar.getProgress() + 2700;
//                int cctInt = SlidingMenuMainActivity.m_mainDevice.ChangeCCT(seekBarProgress);
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

//        ring1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                ring1 = isChecked;
//                updateDeviceRingLabel();
//            }
//        });
//        ring2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                ring2 = isChecked;
//                updateDeviceRingLabel();
//            }
//        });
//        ring3Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                ring3 = isChecked;
//                updateDeviceRingLabel();
//            }
//        });
        initScenario();//初始化场景
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
//    private static final String RINGALL_TEXT = "ALL RINGS";
//    private static final String RING1_TEXT = "RING 1";
//    private static final String RING2_TEXT = "RING 2";
//    private static final String RING3_TEXT = "RING 3";

    private CheckBox powerSwitch;
//    private SeekBar brightnessSeekBar;
    private RangeSeekBar brightnessSeekBar;

    private SeekBar cctSeekBar;
    private TextView colorTextView;
    private ImageView spinner;
    private LinearLayout scenarioNoneLL;
//    private ToggleButton ring1Button, ring2Button, ring3Button;
//    private TextView deviceRingLabel;
    private TextView powerLabel, brightnessLabel, cctLabel, colorLabel;
    private ImageView lightImageView;

    private LinearLayout llBack;
    private TextView btnSure;
    private LinearLayout linear;
    private HorizontalScrollView mHorizontalScrollView;

    private LayoutInflater mInflater;

    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    private void updateDeviceRingLabel() {
//        String label = SlidingMenuMainActivity.m_mainDevice.getDeviceName();
//
//        if (ring1 && ring2 && ring3) {
//            label += ": " + RINGALL_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring123);
//        } else if (!ring1 && !ring2 && !ring3) {
//            label += ": " + RINGALL_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_noring);
//        } else if (ring1 && ring2) {
//            label += ": " + RING1_TEXT + " & " + RING2_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring12);
//        } else if (ring2 && ring3) {
//            label += ": " + RING2_TEXT + " & " + RING3_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring23);
//        } else if (ring1 && ring3) {
//            label += ": " + RING1_TEXT + " & " + RING3_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring13);
//        } else if (ring1) {
//            label += ": " + RING1_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring1);
//        } else if (ring2) {
//            label += ": " + RING2_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring2);
//        } else if (ring3) {
//            label += ": " + RING3_TEXT;
//            lightImageView.setImageResource(R.drawable.aquabg_ring3);
//        } else {
//            label += "";
//            lightImageView.setImageResource(R.drawable.aquabg_noring);
//        }
//
//        deviceRingLabel.setText(label);
//    }

    private void onFabPressed() {
        Intent intent = new Intent(DeviceControlSelectActivity.this, ColorSelectActivity.class);
        startActivityForResult(intent, 1);
    }

    private int red = 130;
    private int green = 255;
    private int blue = 0;
    private CircleDotView circleIcon;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 35: //TODO
                curMainNodes = (Devicenodes) data.getSerializableExtra("deviceInfo");
                mActioncmd.devicenodeId = curMainNodes.id;
                Actioncmdfield actioncmdfield = new Actioncmdfield();
                actioncmdfield.cmd = curMainNodes.devicenodename;
                actioncmdfield.paralist = "{" + getString(R.string.brightness) + ":" + curMainNodes.brightness + "," + getString(R.string.color_temp) + ":"
                        + curMainNodes.cct + "," + getString(R.string.color) + ":" + curMainNodes.cct + "," + getString(R.string.scene) + ":" + curMainNodes.scenarioId + "} ";
                if (mActioncmd.actioncmdfield == null) {
                    mActioncmd.actioncmdfield = new ArrayList<Actioncmdfield>();
                }
                mActioncmd.actiontype = 1;
                mActioncmd.actioncmdfield.add(actioncmdfield);
                updateViews();
                break;
            default:
                if (resultCode == -1) {
                    int color = data.getIntExtra("color", -1);
                    if (-1 != color) {
                        red = (color & 0xff0000) >> 16;
                        green = (color & 0x00ff00) >> 8;
                        blue = (color & 0x0000ff);
                    }
                    circleIcon.setColor(color);
                    colorTextView.setText("RGB(" + red + "," + green + "," + blue + ")");
                    if (isScrollView) {
                        if (null != viewList && viewList.size() > 0) {
                            viewList.get(0).callOnClick();
                            mHorizontalScrollView.smoothScrollTo(0, 0);
                        }

                    } else {
                        isScrollView = true;
                    }
                }
                break;
        }

    }

    private void updateViews() {

        cctSeekBar.setMax(6500 - 2700);
        scenarioNoneLL.setAlpha(1);
        tvTitle.setText(R.string.living_room_lamp);

        powerSwitch.setChecked(true);
        brightnessSeekBar.setValue(20);
        cctSeekBar.setProgress(10);

        if (null != curMainNodes) {
            lampName.setText(curMainNodes.devicenodename);
            tvTitle.setText(curMainNodes.devicenodename);

            if (1 == curMainNodes.devicenodetype) {
                rl_scenario.setVisibility(View.GONE);
                colorLL.setVisibility(View.GONE);
            } else {
                rl_scenario.setVisibility(View.VISIBLE);
                colorLL.setVisibility(View.VISIBLE);
                cctSeekBar.setProgress(curMainNodes.cct - 2700);
            }
            brightnessSeekBar.setValue(curMainNodes.brightness);
        }

    }

    private List<View> viewList = new ArrayList<View>();
    private List<TextView> textViews = new ArrayList<TextView>();

    /**
     * 请求场景
     */
    private void initScenario() {
        RequestSceneListInfo.getInstance().getSceneListInfo(this, new RequestSceneListInfo.OnRequestSceneInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final List<SceneResult> mDeviceInfoResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //DeviceControlSelectActivity.this.mDeviceInfoResult = mDeviceInfoResult;
                        initSceneList();
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(DeviceControlSelectActivity.this, "" + errMsg);
                    }
                });
            }
        });
    }

    public SceneListResult mDeviceInfoResult;

    /**
     * 显示场景
     */
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

//        if (deviceInfo.devicenodetype != 1 && !TextUtils.isEmpty(deviceInfo.scenarioId)) {
////        if (deviceInfo.devicenodetype != 1) {//测试专用
//            for (int i = 0; i < viewList.size(); i++) {
//                if (i != 0 && deviceInfo.scenarioId.equals(String.valueOf(mDeviceInfoResult.rows.get(i - 1).id))) {
////                if (i != 0 && "53".equals(String.valueOf(mDeviceInfoResult.rows.get(i - 1).id))) {
//                    View cView = viewList.get(i);
//                    cView.callOnClick();
//
//                    Message msg = Message.obtain();
//                    msg.arg1 = i;
//
//                    handler.sendMessageDelayed(msg, 100);
//                    break;
//
//                }
//            }
//        }
    }

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
        powerSwitch.setChecked((1 == sceneInfo.ison) ? true : false);
        brightnessSeekBar.setValue(sceneInfo.brightness);
        cctSeekBar.setProgress(sceneInfo.cct - 2700);
    }


}
