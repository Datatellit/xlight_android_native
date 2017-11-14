package com.umarbhutta.xlightcompanion.control.activity.condition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.activity.dialog.DialogActivity;
import com.umarbhutta.xlightcompanion.control.adapter.EntryConditionListAdapter;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.control.bean.Ruleconditions;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.Condition;
import com.umarbhutta.xlightcompanion.okHttp.model.Schedule;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 * 启动条件
 */

public class EntryConditionActivity extends BaseActivity {

    private String TAG = EntryConditionActivity.class.getSimpleName();

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;

    private int requestCode = 111;

    private List<String> settingStr = new ArrayList<String>();
    private List<Integer> imgInter = new ArrayList<Integer>();

    public ArrayList<String> listStr = new ArrayList<String>();

    EntryConditionListAdapter entryConditionListAdapter;
    ListView entryConditionListView;

    private Schedule mSchedule;

    private Condition mCondition;

    private Ruleconditions ruleconditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();

        ((App) getApplicationContext()).setActivity(this);
        mSchedule = new Schedule();
        mCondition = new Condition();

        entryConditionListView = (ListView) findViewById(R.id.entryConditionListView);
        entryConditionListAdapter = new EntryConditionListAdapter(this, settingStr, imgInter);
        entryConditionListView.setAdapter(entryConditionListAdapter);

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.luanch_condition);
        btnSure.setVisibility(View.INVISIBLE);

        settingStr.add(getString(R.string.timer));
        imgInter.add(R.drawable.rule_time);
        settingStr.add(getString(R.string.brightness));
        imgInter.add(R.drawable.rule_brightness);
        settingStr.add(getString(R.string.detection_to_the_active));
        imgInter.add(R.drawable.rule_activity);
        settingStr.add(getString(R.string.detection_to_the_voice));
        imgInter.add(R.drawable.rule_souce);
        settingStr.add(getString(R.string.temperature));
        imgInter.add(R.drawable.rule_tem);
        settingStr.add(getString(R.string.leave_home));
        imgInter.add(R.drawable.rule_fromhome);
        settingStr.add(getString(R.string.go_home));
        imgInter.add(R.drawable.rule_gohome);
        settingStr.add(getString(R.string.gas));
        imgInter.add(R.drawable.rule_gas);
        getRuleconditions();//获取规则条件详细信息
        entryConditionListAdapter.notifyDataSetChanged();
        entryConditionListAdapter.setmOnItemClickListener(new EntryConditionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {// 0是定时 1是亮度 2是活动，3是声音，4是温度 5是离家，6是回家，7是气体
                    case 0://定时
                        listStr.clear();
                        onFabPressed(TimingActivity.class, 0);
                        break;
                    case 1://亮度
                        listStr.clear();
                        requestCode = 111;
                        onFabPressed(BriressControlActivity.class, 1);
//                        onFabPressed(DialogActivity.class, 1);
                        break;
                    case 2://检测到活动
                        listStr.clear();
                        requestCode = 112;
                        onFabPressed(DialogActivity.class, 2);
                        break;
                    case 3://检测到声音
                        listStr.clear();
                        requestCode = 113;
                        onFabPressed(DialogActivity.class, 3);
                        break;
                    case 4://温度
                        listStr.clear();
                        requestCode = 114;
                        onFabPressed(TemControlActivity.class, 4);
                        break;
                    case 5://离家
//                        listStr.clear();
//                        requestCode = 115;
//                        onFabPressed(DialogActivity.class, 5);
                        mCondition.attribute = getString(R.string.leave_home);
                        mCondition.rightValue = ruleconditions.data.get(0).getLeavehome().get(0).value + "";
                        mCondition.operator = "=";
                        mCondition.ruleconditionname = ruleconditions.data.get(0).getLeavehome().get(0).name;
                        mCondition.status = 0;
                        mCondition.conditionType = 5;

                        NewRuleItemInfo mNewRuleItemInfo3 = new NewRuleItemInfo();
                        mCondition.ruleconditiontype = 6;
                        mNewRuleItemInfo3.setmCondition(mCondition);
                        AddControlRuleActivity.mNewRuleConditionInfoList.add(mNewRuleItemInfo3);
                        ((App) getApplicationContext()).finishActivity();
                        break;
                    case 6://回家
//                        listStr.clear();
//                        requestCode = 116;
//                        onFabPressed(DialogActivity.class, 6);
                        mCondition.attribute = getString(R.string.go_home);
                        mCondition.rightValue = ruleconditions.data.get(0).getGohome().get(0).value + "";
                        mCondition.operator = "=";
                        mCondition.ruleconditionname = ruleconditions.data.get(0).getGohome().get(0).name;
                        mCondition.status = 0;
                        mCondition.conditionType = 6;

                        NewRuleItemInfo mNewRuleItemInfo4 = new NewRuleItemInfo();
                        mCondition.ruleconditiontype = 7;
                        mNewRuleItemInfo4.setmCondition(mCondition);
                        AddControlRuleActivity.mNewRuleConditionInfoList.add(mNewRuleItemInfo4);

                        ((App) getApplicationContext()).finishActivity();
                        break;
                    case 7://气体
                        listStr.clear();
                        requestCode = 117;
                        onFabPressed(DialogActivity.class, 7);
                        break;
                }
            }
        });

    }

    /**
     * 获取规则条件详细信息
     */
    private void getRuleconditions() {
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_RULES_RULECONDITIONS + "?access_token=" + UserUtils.getAccessToken(getApplicationContext()),
                Ruleconditions.class, new HttpUtils.OnHttpRequestCallBack() {
                    @Override
                    public void onHttpRequestSuccess(Object result) {
                        //
                        ruleconditions = (Ruleconditions) result;
                        Logger.e(TAG, ruleconditions.toString());
                    }

                    @Override
                    public void onHttpRequestFail(int code, String errMsg) {
                        Logger.e(TAG, "code=" + code + ";errMsg=" + errMsg);
                    }
                });
    }

    private void onFabPressed(Class activity, int type) {
        Intent intent = new Intent(this, activity);
        Bundle bundle = new Bundle();
        bundle.putInt("TYPE", type);
        bundle.putSerializable("SCHEDULE", mSchedule);
        bundle.putSerializable("CONDITION", mCondition);
        bundle.putSerializable("RULECONDITIONS", ruleconditions);
        intent.putExtra("BUNDLE", bundle);
        if (ruleconditions != null) {
            startActivityForResult(intent, requestCode);
        } else {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.load_base_info_fail));
        }

    }

    /**
     * 退出登录
     */
    private void logout() {
        UserUtils.saveUserInfo(getApplicationContext(), null);
    }
}
