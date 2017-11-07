package com.umarbhutta.xlightcompanion.control.activity.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.adapter.DialogWeekListAdapter;
import com.umarbhutta.xlightcompanion.control.adapter.DialogWeeksListAdapter;
import com.umarbhutta.xlightcompanion.control.bean.SelectTime;
import com.umarbhutta.xlightcompanion.control.bean.SelectWeek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/15.
 * 弹出框Activity
 */

public class DialogWeelActivity extends Activity implements View.OnClickListener {

    private ArrayList<SelectTime> settingStr = new ArrayList<SelectTime>();

    private ArrayList<SelectWeek> settingWeekStr = new ArrayList<SelectWeek>();

    BaseAdapter dialogConditionListAdapter;
    ListView dialoglist;
    private Button cancel, sure;

    private ArrayList<SelectTime> settingSelectStr = new ArrayList<SelectTime>();

    private ArrayList<SelectWeek> settingSelectWeekStr = new ArrayList<SelectWeek>();

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initData();
        initViews();
    }

    private void initData() {
        type = 0;
        settingStr.clear();
        settingSelectStr.clear();
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E");
        String weekday = format.format(date);
        if (weekday.equals(getString(R.string.zhouyi))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "1", 0));
        } else if (weekday.equals(getString(R.string.zhouer))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "2", 0));
        } else if (weekday.equals(getString(R.string.zhousan))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "3", 0));
        } else if (weekday.equals(getString(R.string.zhousi))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "4", 0));
        } else if (weekday.equals(getString(R.string.zhouwu))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "5", 0));
        } else if (weekday.equals(getString(R.string.zhouilu))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "6", 0));
        } else if (weekday.equals(getString(R.string.zhouri))) {
            settingStr.add(new SelectTime(getString(R.string.execute_once), false, "7", 0));
        }

        settingStr.add(new SelectTime(getString(R.string.everyday), false, "1,2,3,4,5,6,7", 0));
        settingStr.add(new SelectTime(getString(R.string.zhouyi_zhouwu), false, "1,2,3,4,5", 0));
        settingStr.add(new SelectTime(getString(R.string.custom), false, "0", 0));
    }

    /**
     * 初始化控件
     */
    private void initViews() {

        dialoglist = (ListView) findViewById(R.id.dialoglist);
        dialogConditionListAdapter = new DialogWeekListAdapter(DialogWeelActivity.this.getApplicationContext(), settingStr);
        dialoglist.setAdapter(dialogConditionListAdapter);

        dialoglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 0) {
                    if (settingStr.get(position).name.equals(getString(R.string.custom))) {
                        initWeeks();
                    } else {
                        if (!settingStr.get(position).isSelect) {
                            for (int i = 0; i < settingStr.size(); i++) {
                                if (settingStr.get(i).isSelect) {
                                    settingStr.get(i).isSelect = false;
                                }
                            }
                            settingSelectStr.clear();
                            settingStr.get(position).isSelect = true;
                            settingSelectStr.add(settingStr.get(position));
                        }
                        Intent in = new Intent();
                        in.putExtra("SELECTTIME", settingStr.get(position));
                        setResult(10, in);
                        finish();
                    }

                } else {
                    if (!settingWeekStr.get(position).isSelect) {
                        settingWeekStr.get(position).isSelect = true;
                        settingSelectWeekStr.add(settingWeekStr.get(position));
                    } else {
                        settingWeekStr.get(position).isSelect = false;
                        settingSelectWeekStr.remove(settingWeekStr.get(position));
                    }
                }
                dialogConditionListAdapter.notifyDataSetChanged();
            }
        });
        dialogConditionListAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化周
     */
    private void initWeeks() {

        LayoutInflater.from(this).inflate(R.layout.foot_view, null);
        View footView = LayoutInflater.from(this).inflate(R.layout.foot_view, null);
        dialoglist.addFooterView(footView);
        cancel = (Button) footView.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        sure = (Button) footView.findViewById(R.id.sure);
        sure.setOnClickListener(this);

        type = 1;
        settingWeekStr.clear();
        settingSelectWeekStr.clear();
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqiyi), false, "1", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqier), false, "2", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqisan), false, "3", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqisi), false, "4", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqiwu), false, "5", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqiliu), false, "6", 1));
        settingWeekStr.add(new SelectWeek(getString(R.string.xingqiri), false, "7", 1));

        dialogConditionListAdapter = new DialogWeeksListAdapter(DialogWeelActivity.this.getApplicationContext(), settingWeekStr);
        dialoglist.setAdapter(dialogConditionListAdapter);
        dialogConditionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.sure:
                Intent in = new Intent();
                if (settingSelectWeekStr.size() > 0) {
                    in.putParcelableArrayListExtra("SELECTWEEK", settingSelectWeekStr);
                    setResult(20, in);
                    finish();
                } else {
                    ToastUtil.showToast(this, getString(R.string.please_select));
                }

                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
}
