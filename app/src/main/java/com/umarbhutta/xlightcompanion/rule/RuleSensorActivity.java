package com.umarbhutta.xlightcompanion.rule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class RuleSensorActivity extends BaseActivity {

    private LinearLayout llBack;
    private LinearLayout llMax;
    private TextView tvTitle;
    private TextView tvEditSure;
    private TextView tvSensorName;
    private TextView tvValue;
    private WheelPicker wpOp;
    private WheelPicker wpMin;
    private WheelPicker wpMax;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sensor);
        type = getIntent().getIntExtra("sensor", 0);
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
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvEditSure = (TextView) findViewById(R.id.tvEditSure);
        tvSensorName = (TextView) findViewById(R.id.tvSensorName);
        tvEditSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 验证
                if (wpOp.getCurrentItemPosition() == 6 || wpOp.getCurrentItemPosition() == 7) {
                    if (wpMin.getCurrentItemPosition() >= wpMax.getCurrentItemPosition()) {
                        ToastUtil.showToast(getBaseContext(), R.string.minthanmax);
                        return;
                    }
                }
                // 将结果传递回去
                Intent intent = new Intent();
                intent.putExtra("sensor", type);
                intent.putExtra("operator", wpOp.getCurrentItemPosition());
                intent.putExtra("min", wpMin.getCurrentItemPosition());
                intent.putExtra("max", wpMax.getCurrentItemPosition());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        tvTitle.setText(R.string.sensor);
        wpOp = (WheelPicker) findViewById(R.id.wp_op);
        wpMin = (WheelPicker) findViewById(R.id.wp_min);
        wpMax = (WheelPicker) findViewById(R.id.wp_max);
        llMax = (LinearLayout) findViewById(R.id.ll_max);
        tvValue = (TextView) findViewById(R.id.tvValue);
        if (type == 0) {
            tvSensorName.setText(R.string.dhtt);
        } else {
            tvSensorName.setText(R.string.dhth);
        }
        // 初始化数据
        initPicker();
    }

    private void initPicker() {
        List<String> op = new ArrayList<>();
        op.add("=");
        op.add("≠");
        op.add(">");
        op.add("≥");
        op.add("<");
        op.add("≤");
        op.add(getString(R.string.between));
        wpOp.setData(op);
        wpOp.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                if (position == 6 || position == 7) {
                    llMax.setVisibility(View.VISIBLE);
                    tvValue.setText(R.string.min);
                } else {
                    llMax.setVisibility(View.GONE);
                    tvValue.setText(R.string.value);
                }
            }
        });

        List<Integer> hour = new ArrayList<>();
        int max = type == 0 ? 50 : 100;
        for (int i = 0; i < max; i++) {
            hour.add(i);
        }
        wpMin.setData(hour);
        wpMax.setData(hour);
        wpOp.setItemSpace(38);
        wpOp.setItemTextSize(48);
        wpOp.setAtmospheric(true);
        wpMax.setItemSpace(38);
        wpMax.setItemTextSize(48);
        wpMax.setAtmospheric(true);
        wpMin.setItemSpace(38);
        wpMin.setItemTextSize(48);
        wpMin.setAtmospheric(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
