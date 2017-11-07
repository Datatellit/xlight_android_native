package com.umarbhutta.xlightcompanion.control.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.adapter.DialogTimeListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/15.
 * 弹出框Activity
 */

public class DialogTimeActivity extends Activity {

    private ArrayList<String> settingStr = new ArrayList<String>();

    BaseAdapter dialogConditionListAdapter;
    ListView dialoglist;

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
//        settingStr.clear();
//        settingStr.add("00:00");
//        settingStr.add("01:00");
//        settingStr.add("02:00");
//        settingStr.add("03:00");
//        settingStr.add("04:00");
//        settingStr.add("05:00");
//        settingStr.add("06:00");
//        settingStr.add("07:00");
//        settingStr.add("08:00");
//        settingStr.add("09:00");
//        settingStr.add("10:00");
//        settingStr.add("11:00");
//        settingStr.add("12:00");
//        settingStr.add("13:00");
//        settingStr.add("14:00");
//        settingStr.add("15:00");
//        settingStr.add("16:00");
//        settingStr.add("17:00");
//        settingStr.add("18:00");
//        settingStr.add("19:00");
//        settingStr.add("20:00");
//        settingStr.add("21:00");
//        settingStr.add("22:00");
//        settingStr.add("23:00");
//        settingStr.add("24:00");
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        dialoglist = (ListView) findViewById(R.id.dialoglist);
//        settingStr = EntryConditionActivity.listStr;
        dialogConditionListAdapter = new DialogTimeListAdapter(DialogTimeActivity.this.getApplicationContext(), settingStr);
        dialoglist.setAdapter(dialogConditionListAdapter);

        dialoglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.showToast(getApplicationContext(), settingStr.get(position) + ":" + position);
            }
        });
        dialogConditionListAdapter.notifyDataSetChanged();
    }
}
