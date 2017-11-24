package com.umarbhutta.xlightcompanion.scenario;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaygoo.widget.RangeSeekBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.model.EditScenarionodes;
import com.umarbhutta.xlightcompanion.okHttp.model.EditSceneParams;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.Scenarionodes;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestAddScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestEditScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequstCallback;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.CircleDotView;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.新建场景
 */
public class AddScenarioNewActivity extends BaseActivity {

    private static final String TAG = AddScenarioNewActivity.class.getSimpleName();
    private RangeSeekBar brightnessSeekBar;
    private TextView colorTextView;
    private Button addButton;
    private EditText nameEditText;
    private ImageView backImageView;

    private LinearLayout llBack;
    private TextView btnSure;

    private int scenarioBrightness = 0;
    private int c = 0, cw = 0, ww = 0, r = 0, g = 0, b = 0;
    private String colorHex, scenarioName, scenarioInfo, scenarioFilter;
    private TextView tvTitle;
    private SeekBar colorTemperatureSeekBar;
    private TextView cctLabelColor;
    private int red = 130;
    private int green = 255;
    private int blue = 0;
    private Rows mSceneInfo;
    String from;
    private CircleDotView circleIcon;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scenario);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));

//        getSupportActionBar().hide();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        brightnessSeekBar = (RangeSeekBar) findViewById(R.id.brightnessSeekBar);
        colorTextView = (TextView) findViewById(R.id.colorTextView);
        addButton = (Button) findViewById(R.id.addButton);
        cctLabelColor = (TextView) findViewById(R.id.cctLabelColor);

        colorTemperatureSeekBar = (SeekBar) findViewById(R.id.colorTemperatureSeekBar);
        colorTemperatureSeekBar.setMax(6500 - 2700);

        colorTemperatureSeekBar.setProgress(1200);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        backImageView = (ImageView) findViewById(R.id.backImageView);

        circleIcon = new CircleDotView(this);

        RelativeLayout dotLayout = (RelativeLayout) findViewById(R.id.dotLayout);
        dotLayout.addView(circleIcon);

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
                // 确定提交按钮
                //send info back to ScenarioMainFragment


                scenarioName = nameEditText.getText().toString();


                if (TextUtils.isEmpty(scenarioName)) {
                    ToastUtil.showToast(AddScenarioNewActivity.this, getString(R.string.input_scene_name));
                    return;
                }

                scenarioInfo = "A " + colorHex + " color with " + scenarioBrightness + "% brightness";

                //SEND TO PARTICLE CLOUD FOR ALL RINGS
//                SlidingMenuMainActivity.m_mainDevice.sceAddScenario(ScenarioMainFragment.name.size(), scenarioBrightness, cw, ww, r, g, b, xltDevice.DEFAULT_FILTER_ID);

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScenarioMainFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScenarioMainFragment.SCENARIO_INFO, scenarioInfo);
                setResult(Activity.RESULT_OK, returnIntent);
//                finish();

                if ("list".equals(from)) {
                    editScene();
                } else {
                    addScence();
                }


            }
        });

        findViewById(R.id.colorLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出选择颜色的页面
                onFabPressed();
            }
        });
        /**
         * 亮度
         */
        brightnessSeekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener(){

            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                //
                Log.e(TAG, "The brightness value is " + (int)min+"view.getCurrentRange()="+view.getCurrentRange()[0]);
                scenarioBrightness = (int)min;
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
//                scenarioBrightness = seekBar.getProgress();
//            }
//        });
        colorTemperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
//                Log.e(TAG, "cctInt value is= " + cctInt);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scenarioName = nameEditText.getText().toString();
                scenarioInfo = "A " + colorHex + " color with " + scenarioBrightness + "% brightness";
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScenarioMainFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScenarioMainFragment.SCENARIO_INFO, scenarioInfo);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();

        if (null != intent) {
            from = intent.getStringExtra("from");
            if ("list".equals(from)) { //是从场景列表点击进来的
                mSceneInfo = (Rows) intent.getSerializableExtra("infos");
                initViewState();
            }
        }

        if (!TextUtils.isEmpty(from) && "list".equals(from)) {
            tvTitle.setText(R.string.edit_scene);
        } else {
            tvTitle.setText(R.string.add_scene);
        }


        if (null != mSceneInfo && null != mSceneInfo.scenarionodes && mSceneInfo.scenarionodes.size() > 0) {
            Scenarionodes scenarionodes = mSceneInfo.scenarionodes.get(0);
            int R = scenarionodes.R;
            int G = scenarionodes.G;
            int B = scenarionodes.B;


            red = R;
            green = G;
            blue = B;

            int color = Color.rgb(R, G, B);
            circleIcon.setColor(color);
            colorTextView.setText("RGB(" + R + "," + G + "," + B + ")");
        }
    }

    private void initViewState() {
        nameEditText.setText(mSceneInfo.scenarioname);
        colorTemperatureSeekBar.setProgress(mSceneInfo.cct - 2700);
        brightnessSeekBar.setValue(mSceneInfo.brightness);
    }

    private void onFabPressed() {
        Intent intent = new Intent(AddScenarioNewActivity.this, ColorSelectActivity.class);
        startActivityForResult(intent, 1);
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
        }
    }

    public String toHexEncoding(int color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("#");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }


    /**
     * 编辑场景
     */
    private void editScene() {
        if (!UserUtils.isLogin(this)) {
            ToastUtil.showToast(this, getString(R.string.login_first));
            return;
        }
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }

        String sceneName = nameEditText.getText().toString();
        if (TextUtils.isEmpty(sceneName)) {
            ToastUtil.showToast(this, getString(R.string.please_input_scene_name));
            return;
        }

        mProgressDialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.commiting));
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        RequestEditScene.getInstance().editScene(this, mSceneInfo.id, getParams(1), new CommentRequstCallback() {
            @Override
            public void onCommentRequstCallbackSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtil.showToast(AddScenarioNewActivity.this, getString(R.string.edit_scene_success));
                        AddScenarioNewActivity.this.finish();
                    }
                });
            }

            @Override
            public void onCommentRequstCallbackFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtil.showToast(AddScenarioNewActivity.this, "" + errMsg);
                    }
                });
            }
        });


    }

    /**
     * 添加场景
     */
    private void addScence() {

        if (!UserUtils.isLogin(this)) {
            ToastUtil.showToast(this, getString(R.string.login_first));
            return;
        }

        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }

        String sceneName = nameEditText.getText().toString();
        if (TextUtils.isEmpty(sceneName)) {
            ToastUtil.showToast(this, R.string.please_input_scene_name);
            return;
        }

        mProgressDialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.commiting));
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        RequestAddScene.getInstance().addScene(this, getParams(2), new CommentRequstCallback() {
            @Override
            public void onCommentRequstCallbackSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtil.showToast(AddScenarioNewActivity.this, getString(R.string.add_scene_success));
                        AddScenarioNewActivity.this.finish();
                    }
                });
            }

            @Override
            public void onCommentRequstCallbackFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtil.showToast(AddScenarioNewActivity.this, "" + errMsg);
                    }
                });
            }
        });


    }

    /**
     * @param mType 添加场景2，编辑场景1
     * @return
     */
    private EditSceneParams getParams(int mType) {
        int brightNess = (int)brightnessSeekBar.getCurrentRange()[0];  // 亮度
        int colorTemper = colorTemperatureSeekBar.getProgress() + 2700;  // 色温
        int type = mType;
        String sceneName = nameEditText.getText().toString();

        EditSceneParams params = new EditSceneParams();
        params.type = type;
        params.userId = UserUtils.getUserInfo(this).id;
        params.scenarioname = sceneName;
        params.cct = colorTemper;
        params.brightness = brightNess;
        params.color = "rgb(" + red + "," + green + "," + blue + ")";

        List<EditScenarionodes> list = new ArrayList<EditScenarionodes>();
        list.add(new EditScenarionodes(params.brightness, red, green, blue, params.cct, "rgb(" + red + "," + green + "," + blue + ")"));
        list.add(new EditScenarionodes(params.brightness, red, green, blue, params.cct, "rgb(" + red + "," + green + "," + blue + ")"));
        list.add(new EditScenarionodes(params.brightness, red, green, blue, params.cct, "rgb(" + red + "," + green + "," + blue + ")"));

        params.scenarionodes = list;
        return params;
    }

}
