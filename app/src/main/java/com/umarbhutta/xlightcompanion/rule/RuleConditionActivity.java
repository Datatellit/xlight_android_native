package com.umarbhutta.xlightcompanion.rule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.CommentResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.ModifyPwdParam;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.SettingListAdapter;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class RuleConditionActivity extends BaseActivity {

    private LinearLayout llBack;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvEditSure;
    private ListView lv_condition;
    SettingListAdapter settingListAdapter;
    private List<String> conditions = new ArrayList<String>();
    private int type;
    private int exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_condition);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        // 根据过来的页面，判断加载何种数据，当前支持三种 1、conditions  2、Sensor  3、Light
        type = getIntent().getIntExtra("type", 1);
        exist = getIntent().getIntExtra("exist", 0);
        initViews();
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
        settingListAdapter = new SettingListAdapter(this, conditions);
        lv_condition.setAdapter(settingListAdapter);
        lv_condition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击了指定的项，需要区分点击的什么，分为三种 1、条件  2、传感器  3、设备
                if (type == 1) {
                    if (exist == 2) {
                        Intent intent = new Intent(getBaseContext(), RuleTimeActivity.class);
                        startActivityForResult(intent, 4);
                    } else if (exist == 1) {
                        Intent intent = new Intent(getBaseContext(), RuleConditionActivity.class);
                        intent.putExtra("type", 2);
                        startActivityForResult(intent, 3);
                    } else {
                        if (position == 0) { // 继续加载，进入下一级页面，保留当前页面，因为有可能会返回
                            Intent intent = new Intent(getBaseContext(), RuleConditionActivity.class);
                            intent.putExtra("type", 2);
                            startActivityForResult(intent, 3);
                        } else if (position == 1) { // 时间，进入时间页面，并释放当前页面
                            Intent intent = new Intent(getBaseContext(), RuleTimeActivity.class);
                            startActivityForResult(intent, 4);
                        }
                    }
                } else if (type == 2) {
                    // 选中了指定的传感器，那么此时返回上一级页面，然后将返回值带回，并销毁当前页面
                    Intent intent = new Intent();
                    intent.putExtra("sensor", position == 0 ? 0 : 16);
                    // 进入具体的设置页面
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else if (type == 3) { // 进入灯具设置页面，并关闭当前页面
                    Intent intent = new Intent(getBaseContext(), RuleActionActivity.class);
                    intent.putExtra("device", GlanceMainFragment.devicenodes.get(position));
                    startActivityForResult(intent, 6);
                }
            }
        });
        initListView();
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
        tvEditSure.setVisibility(View.GONE);
        tvDesc = (TextView) findViewById(R.id.txt_desc);
        if (type == 1) {
            tvTitle.setText(R.string.choose_condition);
        } else if (type == 2) {
            tvTitle.setText(R.string.choose_condition);
            tvDesc.setVisibility(View.GONE);
        } else {
            tvTitle.setText(R.string.choose_device);
            tvDesc.setVisibility(View.GONE);
        }
        lv_condition = (ListView) findViewById(R.id.lv_condition);
    }

    private void initListView() {
        if (type == 1) {
            if (exist == 1 || exist == 0) {
                conditions.add(getString(R.string.sensor));
            }
            if (exist == 2 || exist == 0) {
                conditions.add(getString(R.string.time));
            }
            settingListAdapter.notifyDataSetChanged();
        } else if (type == 2) {
            conditions.add(getString(R.string.dhtt));
            conditions.add(getString(R.string.dhth));
            settingListAdapter.notifyDataSetChanged();
        } else {
            // 加载当前的设备列表，然后进行显示选择
            if (GlanceMainFragment.devicenodes != null && GlanceMainFragment.devicenodes.size() > 0) {
                for (Devicenodes d : GlanceMainFragment.devicenodes) {
                    conditions.add(d.devicenodename);
                }
                settingListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            // 设置时间回来了
            data.putExtra("type", "time");
            setResult(Activity.RESULT_OK, data);
            finish();
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            // 获取返回数据，并打开新页面
            Intent intent = new Intent(getBaseContext(), RuleSensorActivity.class);
            intent.putExtra("sensor", data.getIntExtra("sensor", 0));
            startActivityForResult(intent, 5);
        } else if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            data.putExtra("type", "sensor");
            setResult(Activity.RESULT_OK, data);
            finish();
        } else if (requestCode == 6 && resultCode == Activity.RESULT_OK) {
            data.putExtra("type", "light");
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
