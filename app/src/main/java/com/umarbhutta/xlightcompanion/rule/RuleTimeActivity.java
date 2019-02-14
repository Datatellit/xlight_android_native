package com.umarbhutta.xlightcompanion.rule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.SettingListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class RuleTimeActivity extends BaseActivity {

    private LinearLayout llBack;
    private TextView tvTitle;
    private TextView tvEditSure;
    private WheelPicker wpWeek;
    private WheelPicker wpHour;
    private WheelPicker wpMinute;
    private CheckBox chkRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_weektime);
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
        tvEditSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将结果传递回去
                Intent intent = new Intent();
                intent.putExtra("week", wpWeek.getCurrentItemPosition());
                intent.putExtra("hour", wpHour.getCurrentItemPosition());
                intent.putExtra("minute", wpMinute.getCurrentItemPosition());
                intent.putExtra("repeat", chkRepeat.isChecked());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        tvTitle.setText(R.string.schedule);
        wpWeek = (WheelPicker) findViewById(R.id.wp_week);
        wpHour = (WheelPicker) findViewById(R.id.wp_hour);
        wpMinute = (WheelPicker) findViewById(R.id.wp_minute);
        chkRepeat = (CheckBox) findViewById(R.id.chkRepeat);
        // 初始化时间数据
        initPicker();
    }

    private void initPicker() {
        List<String> week = new ArrayList<>();
        week.add(getString(R.string.week0));
        week.add(getString(R.string.week7));
        week.add(getString(R.string.week1));
        week.add(getString(R.string.week2));
        week.add(getString(R.string.week3));
        week.add(getString(R.string.week4));
        week.add(getString(R.string.week5));
        week.add(getString(R.string.week6));
        wpWeek.setData(week);

        List<Integer> hour = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hour.add(i);
        }
        wpHour.setData(hour);

        List<Integer> minute = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minute.add(i);
        }
        wpMinute.setData(minute);
        // 获取当前时间，然后设置
        int curWeek = 0;
        Calendar calendar = Calendar.getInstance();
        curWeek = calendar.get(calendar.DAY_OF_WEEK) - 1;
        curWeek++;
        wpWeek.setSelectedItemPosition(curWeek);
        wpWeek.setItemSpace(38);
        wpWeek.setItemTextSize(54);
        wpWeek.setAtmospheric(true);
        wpHour.setSelectedItemPosition(calendar.get(Calendar.HOUR_OF_DAY));
        wpHour.setItemSpace(38);
        wpHour.setItemTextSize(54);
        wpHour.setAtmospheric(true);
        wpMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE));
        wpMinute.setItemSpace(38);
        wpMinute.setItemTextSize(54);
        wpMinute.setAtmospheric(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
