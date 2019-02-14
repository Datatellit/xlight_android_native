package com.umarbhutta.xlightcompanion.rule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.xw.repo.BubbleSeekBar;
import com.aigestudio.wheelpicker.WheelPicker;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class RuleActionActivity extends BaseActivity {

    private LinearLayout llBack;
    private TextView tvTitle;
    private TextView tvEditSure;
    private ColorSeekBar colorSeekBar;
    private LinearLayout llColor;
    private LinearLayout llCCT;
    private LinearLayout llChange;
    private RadioGroup rbGroup;
    private Devicenodes node;
    private CheckBox chkSwitch;
    private SeekBar cct;
    private BubbleSeekBar brightness;
    private TextView txtChange;
    private TextView txtSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_action);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        node = (Devicenodes) getIntent().getSerializableExtra("device");
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
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvEditSure = (TextView) findViewById(R.id.tvEditSure);
        tvEditSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将结果传递回去
                Intent intent = new Intent();
                intent.putExtra("device", node);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        tvTitle.setText(R.string.device_action);
        chkSwitch = (CheckBox) findViewById(R.id.chkSwitch);
        cct = (SeekBar) findViewById(R.id.cctSeekBar);
        brightness = (BubbleSeekBar) findViewById(R.id.brightnessSeekBar);
        txtChange = (TextView) findViewById(R.id.txtChange);
        txtSwitch = (TextView) findViewById(R.id.txtSwitch);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.colorSlider);
        llColor = (LinearLayout) findViewById(R.id.ll_color);
        llCCT = (LinearLayout) findViewById(R.id.ll_cct);
        llChange = (LinearLayout) findViewById(R.id.llChange);
        rbGroup = (RadioGroup) findViewById(R.id.rb_group);
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("XLight", "click change cct or color");
                // 改变
                if (txtChange.getText().toString().equals(getString(R.string.txt_cct))) {
                    // 变为颜色
                    txtChange.setText(getString(R.string.txt_color));
                    // 将色温显示，颜色隐藏
                    llCCT.setVisibility(View.VISIBLE);
                    llColor.setVisibility(View.GONE);
                    node.scenarioId = "CCT";
                } else {
                    txtChange.setText(getString(R.string.txt_cct));
                    // 将色温显示，颜色隐藏
                    llCCT.setVisibility(View.GONE);
                    llColor.setVisibility(View.VISIBLE);
                    node.scenarioId = null;
                }
            }
        });
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                Log.d("XLight", i + "," + i1 + "," + i2);
                int red = (i2 & 0xff0000) >> 16;
                int green = (i2 & 0x00ff00) >> 8;
                int blue = (i2 & 0x0000ff);
                int color[] = {red, green, blue};
                node.color = color;
            }
        });

        chkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.ison = isChecked ? 1 : 0;
            }
        });

        cct.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                node.cct = 2700 + progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        brightness.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                node.brightness = progress;
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_0) {
                    node.filter = 0;
                } else if (checkedId == R.id.rb_1) {
                    node.filter = 1;
                } else if (checkedId == R.id.rb_2) {
                    node.filter = 2;
                } else if (checkedId == R.id.rb_3) {
                    node.filter = 3;
                } else if (checkedId == R.id.rb_4) {
                    node.filter = 4;
                }
            }
        });
        initDevice();
    }

    private void initDevice() {
        chkSwitch.setChecked(node.ison == 1 ? true : false);
        brightness.setProgress(node.brightness);
        cct.setProgress(node.cct - 2700);
        if (node.filter > 0) {
            // 进行设置选中
            int filter = node.filter;
            if (filter == 1) {
                ((RadioButton) findViewById(R.id.rb_1)).setChecked(true);
            } else if (filter == 2) {
                ((RadioButton) findViewById(R.id.rb_2)).setChecked(true);
            } else if (filter == 3) {
                ((RadioButton) findViewById(R.id.rb_3)).setChecked(true);
            } else if (filter == 4) {
                ((RadioButton) findViewById(R.id.rb_4)).setChecked(true);
            }
        }
        if (node.devicetype > 1) { // 彩灯控制
            llChange.setVisibility(View.VISIBLE);
            int[] color = node.color;
            llColor.setVisibility(View.VISIBLE);
            llCCT.setVisibility(View.GONE);
            txtChange.setText(getString(R.string.txt_cct));
            colorSeekBar.setColor(Color.rgb(color[0], color[1], color[2]));
        } else {
            llColor.setVisibility(View.GONE);
            llCCT.setVisibility(View.VISIBLE);
            node.scenarioId = "CCT";
            llChange.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
