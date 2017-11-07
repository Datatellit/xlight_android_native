package com.umarbhutta.xlightcompanion.control.activity.condition;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.activity.dialog.DialogWeelActivity;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.control.bean.SelectTime;
import com.umarbhutta.xlightcompanion.control.bean.SelectWeek;
import com.umarbhutta.xlightcompanion.okHttp.model.Schedule;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.pickerview.TimePickerView;
import com.umarbhutta.xlightcompanion.views.pickerview.lib.TimePickerUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/13.
 * 定时设置
 */

public class TimingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llBack;
    private RelativeLayout llStartTime, llWeek;
    private TextView btnSure;
    private TextView tvTitle, tv_week;
    private int requestCode = 210;
    private TextView tv_time;

    private Schedule mSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();
        ((App) getApplicationContext()).setActivity(this);
        mSchedule = (Schedule) getIntent().getBundleExtra("BUNDLE").getSerializable("SCHEDULE");
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.timer_setting);
        llStartTime = (RelativeLayout) findViewById(R.id.llStartTime);
        llWeek = (RelativeLayout) findViewById(R.id.llWeek);
        llWeek.setOnClickListener(this);
        llStartTime.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        tv_week = (TextView) findViewById(R.id.tv_week);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    private void onFabPressed(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, 222);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llStartTime:
                requestCode = 212;
                TimePickerView.Type type = TimePickerView.Type.HOURS_MINS;
                String format = "HH:mm";
                TimePickerUtils.alertTimerPicker(this, type, format, new TimePickerUtils.TimerPickerCallBack() {
                    @Override
                    public void onTimeSelect(Date date, String dateStr) {
                        tv_time.setText(dateStr);
                        mSchedule.hour = Integer.parseInt(dateStr.split(":")[0]);
                        mSchedule.minute = Integer.parseInt(dateStr.split(":")[1]);
                    }
                });
                break;
            case R.id.tvEditSure:
                if (TextUtils.isEmpty(tv_week.getText().toString())) {
                    ToastUtil.showToast(TimingActivity.this, getString(R.string.select_repeate_time));
                    return;
                }
                if (TextUtils.isEmpty(tv_time.getText().toString())) {
                    ToastUtil.showToast(TimingActivity.this, getString(R.string.please_select_time));
                    return;
                }

                NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
                mSchedule.ruleconditiontype = 1;
                mNewRuleItemInfo.setmSchedule(mSchedule, TimingActivity.this);
                AddControlRuleActivity.mNewRuleConditionInfoList.add(mNewRuleItemInfo);

                if (resultCodeA == 1) {
                    AddControlRuleActivity.mScheduleListStr.add(selectTime.name);
                } else if (resultCodeA == 2) {
                    String strWeekList = "";
                    for (int i = 0; i < array.size(); i++) {
                        strWeekList = strWeekList + array.get(i).name + ",";
                    }
                    AddControlRuleActivity.mScheduleListStr.add(strWeekList);
                }

                //编辑提交
                ((App) getApplicationContext()).finishActivity();
                break;
            case R.id.llWeek:
                requestCode = 213;
                onFabPressed(DialogWeelActivity.class);
                break;
        }
    }

    private SelectTime selectTime;
    private ArrayList<SelectWeek> array;
    private int resultCodeA = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 10:
                resultCodeA = 1;
                selectTime = (SelectTime) data.getSerializableExtra("SELECTTIME");
                tv_week.setText(selectTime.name);
                mSchedule.weekdays = selectTime.weekdays;
                mSchedule.isrepeat = selectTime.isrepeat;
                mSchedule.scheduleName = selectTime.name;
                break;
            case 20:
                resultCodeA = 2;
                array = data.getParcelableArrayListExtra("SELECTWEEK");
                String strWeekList = "";
                String weekDays = "";
                for (int i = 0; i < array.size(); i++) {
                    strWeekList = strWeekList + array.get(i).name + "、";
                    weekDays = weekDays + array.get(i).weekdays + "、";
                }
                mSchedule.weekdays = "[" + weekDays.substring(0, weekDays.length() - 1) + "]";
                mSchedule.isrepeat = 1;
                mSchedule.scheduleName = strWeekList.substring(0, strWeekList.length() - 1);
                tv_week.setText(strWeekList.substring(0, strWeekList.length() - 1));
                break;
            default:
                break;
        }
    }
}
