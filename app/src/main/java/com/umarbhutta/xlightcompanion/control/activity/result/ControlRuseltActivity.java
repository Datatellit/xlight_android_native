package com.umarbhutta.xlightcompanion.control.activity.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.adapter.ControlResultListAdapter;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actionnotify;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 * 设置执行结果
 */

public class ControlRuseltActivity extends BaseActivity {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;

    private List<String> settingStr = new ArrayList<String>();
    private List<Integer> imgInter = new ArrayList<Integer>();

    ControlResultListAdapter controlResultListAdapter;
    ListView entryConditionListView;

    private Actionnotify mActionnotify;
    private Actioncmd mActioncmd;
    private TextView tvEditSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();
        ((App) getApplicationContext()).setActivity(this);
        mActionnotify = new Actionnotify();
        mActioncmd = new Actioncmd();

        entryConditionListView = (ListView) findViewById(R.id.entryConditionListView);
        controlResultListAdapter = new ControlResultListAdapter(this, settingStr, imgInter);
        entryConditionListView.setAdapter(controlResultListAdapter);

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.VISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.execute_result);
        tvEditSure = (TextView) findViewById(R.id.tvEditSure);
        tvEditSure.setVisibility(View.INVISIBLE);

        settingStr.add(getString(R.string.lamp_control));
        imgInter.add(R.drawable.result_control);
        settingStr.add(getString(R.string.scene_switch));
        imgInter.add(R.drawable.result_scenario);
        settingStr.add(getString(R.string.email_notify));
        imgInter.add(R.drawable.result_email);
        settingStr.add(getString(R.string.app_notify));
        imgInter.add(R.drawable.result_notify);

        controlResultListAdapter.notifyDataSetChanged();
        controlResultListAdapter.setmOnItemClickListener(new ControlResultListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0://灯具控制
                       // onFabPressed(DeviceControlSelectActivity.class);
                        break;
                    case 1://场景切换
                        onFabPressed(SelectScenarioActivity.class);
                        break;
                    case 2://Email通知
                        onFabPressed(EmailActivity.class);
                        break;
                    case 3://APP通知
                        onFabPressed(AppNotifyActivity.class);
                        break;
                }
            }
        });
    }

    private void onFabPressed(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("MACTIONNOTIFY", mActionnotify);
        intent.putExtra("MACTIONCMD", mActioncmd);
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void logout() {
        UserUtils.saveUserInfo(getApplicationContext(), null);
    }

}
