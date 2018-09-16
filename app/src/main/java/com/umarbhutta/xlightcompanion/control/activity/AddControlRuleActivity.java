package com.umarbhutta.xlightcompanion.control.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.adapter.NewRuleAdapter;
import com.umarbhutta.xlightcompanion.control.activity.condition.EntryConditionActivity;
import com.umarbhutta.xlightcompanion.control.activity.result.ControlRuseltActivity;
import com.umarbhutta.xlightcompanion.control.bean.ControlRuleDevice;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleInfo;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actionnotify;
import com.umarbhutta.xlightcompanion.okHttp.model.Condition;
import com.umarbhutta.xlightcompanion.okHttp.model.CreateRuleResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleActioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleactionnotify;
import com.umarbhutta.xlightcompanion.okHttp.model.Rulecondition;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleconditions;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleresult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rules;
import com.umarbhutta.xlightcompanion.okHttp.model.Schedule;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestAddRules;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的规则页面
 */
public class AddControlRuleActivity extends BaseActivity {

    private String TAG = AddControlRuleActivity.class.getSimpleName();
    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private Rules rules;

    public static List<String> mScheduleListStr = new ArrayList<String>();

    /**
     * 条件
     */
    public static List<NewRuleItemInfo> mNewRuleConditionInfoList = new ArrayList<NewRuleItemInfo>();
    /**
     * 结果
     */
    public static List<NewRuleItemInfo> mNewRuleResultInfoList = new ArrayList<NewRuleItemInfo>();


    private ListView listview;
    private NewRuleAdapter mNewRuleAdapter;
    private RuleInfo mRuleInfo;
    private boolean isEditPage = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNewRuleConditionInfoList.clear();
        mNewRuleResultInfoList.clear();
        ImmersionBar.with(this).destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control_rule);
//        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        rules = new Rules();

        Intent intent = getIntent();
        if (null != intent) {
            mRuleInfo = (RuleInfo) intent.getSerializableExtra("RuleInfo");
            if (null != mRuleInfo) {
                isEditPage = true;
                dealEditInfo();
            }
        }
        initViews();
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
    }


    /**
     * 把现有的规则数据放到list中
     */
    private void dealEditInfo() {

        if (null != mRuleInfo) {
            if (null != mRuleInfo.ruleconditions && mRuleInfo.ruleconditions.size() > 0) {  //条件
                for (Ruleconditions mRuleconditions : mRuleInfo.ruleconditions) {
                    //1:定时；2：亮度；3：活动；4：声音；5：温度；6：离家；7：回家；8：气体
                    switch (mRuleconditions.ruleconditiontype) {
                        case 1:
                            Schedule mSchedule = new Schedule();
                            mSchedule.ruleconditiontype = 1;
                            mSchedule.devicenodeId = mRuleconditions.devicenodeId;
                            mSchedule.ruleconditionname = mRuleconditions.ruleconditionname;
                            mSchedule.weekdays = mRuleconditions.weekdays;
                            mSchedule.hour = TextUtils.isEmpty(mRuleconditions.hour) ? 0 : Integer.parseInt(mRuleconditions.hour);
                            mSchedule.minute = TextUtils.isEmpty(mRuleconditions.minute) ? 0 : Integer.parseInt(mRuleconditions.minute);
                            mSchedule.status = TextUtils.isEmpty(mRuleconditions.status) ? 0 : Integer.parseInt(mRuleconditions.status);
                            mSchedule.isrepeat = TextUtils.isEmpty(mRuleconditions.isrepeat) ? 0 : Integer.parseInt(mRuleconditions.isrepeat);


                            NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
                            mNewRuleItemInfo.setmSchedule(mSchedule, AddControlRuleActivity.this);
                            mNewRuleConditionInfoList.add(mNewRuleItemInfo);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            Condition mCondition = new Condition();
                            mCondition.ruleconditiontype = mRuleconditions.ruleconditiontype;
                            mCondition.ruleconditionname = mRuleconditions.ruleconditionname;
                            mCondition.devicenodeId = mRuleconditions.devicenodeId;
                            mCondition.attribute = mRuleconditions.attribute;
                            mCondition.operator = mRuleconditions.operator;
                            mCondition.rightValue = mRuleconditions.rightValue;
                            mCondition.status = TextUtils.isEmpty(mRuleconditions.status) ? 0 : Integer.parseInt(mRuleconditions.status);
                            mCondition.temAbove = ("=".equals(mRuleconditions.operator) ? getString(R.string.equal) : (">".equals(mRuleconditions.operator) ? getString(R.string.higer) : getString(R.string.lower)));
                            NewRuleItemInfo mNewRuleItemInfo2 = new NewRuleItemInfo();
                            mNewRuleItemInfo2.setmCondition(mCondition);
                            mNewRuleConditionInfoList.add(mNewRuleItemInfo2);
                            break;

                    }
                }
            }

            if (null != mRuleInfo.ruleactionnotifies && mRuleInfo.ruleactionnotifies.size() > 0) {//结果通知

                for (Ruleactionnotify mRuleactionnotify : mRuleInfo.ruleactionnotifies) {

                    Actionnotify mActionnotify = new Actionnotify();
                    mActionnotify.msisdn = mRuleactionnotify.msisdn;
                    mActionnotify.emailaddress = mRuleactionnotify.emailaddress;
                    mActionnotify.content = mRuleactionnotify.content;
                    mActionnotify.subject = mRuleactionnotify.subject;
                    mActionnotify.actiontype = mRuleactionnotify.actiontype;

                    NewRuleItemInfo mNewRuleItemInfo3 = new NewRuleItemInfo();
                    mNewRuleItemInfo3.setmActionnotify(mActionnotify);
                    mNewRuleResultInfoList.add(mNewRuleItemInfo3);
                }
            }

            if (null != mRuleInfo.ruleactioncmds && mRuleInfo.ruleactioncmds.size() > 0) {//控制灯具和场景
                for (RuleActioncmd mRuleActioncmd : mRuleInfo.ruleactioncmds) {

                    NewRuleItemInfo mNewRuleItemInfo4 = new NewRuleItemInfo();
                    Actioncmd mActioncmd = new Actioncmd();
                    mActioncmd.actioncmdType = mRuleActioncmd.actiontype;
                    mActioncmd.devicenodeId = mRuleActioncmd.devicenodeId;
                    mActioncmd.actioncmdfield = mRuleActioncmd.actioncmdfields;

                    mNewRuleItemInfo4.setmActioncmd(mActioncmd);
                    mNewRuleResultInfoList.add(mNewRuleItemInfo4);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewRuleAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview = (ListView) findViewById(R.id.listview);

        NewRuleInfo mNewRuleInfo = new NewRuleInfo();
        mNewRuleInfo.mNewRuleConditionInfoList = mNewRuleConditionInfoList;
        mNewRuleInfo.mNewRuleResultInfoList = mNewRuleResultInfoList;
        if (null == mNewRuleAdapter) {
            mNewRuleAdapter = new NewRuleAdapter(this, mNewRuleInfo);
            mNewRuleAdapter.setOnClickAddIconListener(new NewRuleAdapter.OnClickAddIconListener() {
                @Override
                public void clickAddCondition() {
                    onFabPressed(EntryConditionActivity.class);
                }

                @Override
                public void clickAddResult() {
                    onFabPressed(ControlRuseltActivity.class);
                }

                @Override
                public void clickDeleteIcon(boolean isCondition, int position) {
                    if (isCondition) {
                        mNewRuleConditionInfoList.remove(position);
                    } else {
                        mNewRuleResultInfoList.remove(position);
                    }
                    mNewRuleAdapter.notifyDataSetChanged();
                }
            });

            listview.setAdapter(mNewRuleAdapter);
        } else {
            mNewRuleAdapter.notifyDataSetChanged();
        }

        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mNewRuleConditionInfoList.size() == 0) {
                    ToastUtil.showToast(AddControlRuleActivity.this, getString(R.string.no_conditions));
                    return;
                }
                if (mNewRuleResultInfoList.size() == 0) {
                    ToastUtil.showToast(AddControlRuleActivity.this, getString(R.string.no_results));
                    return;
                }
                Ruleresult mRuleresult = new Ruleresult();
                Rulecondition mRulecondition = new Rulecondition();
                //确定提交按钮
                rules.rulename = "rule1";
                rules.relationtype = 1;
                rules.type = 1;
                rules.status = 1;
                rules.userId = UserUtils.getUserInfo(getApplicationContext()).getId();

                List<List> lists = getResultList();


                List<Rulecondition> rulecondition = new ArrayList<Rulecondition>();
                List<Ruleresult> ruleresult = new ArrayList<Ruleresult>();
                mRulecondition.schedule = lists.get(0);
                mRulecondition.condition = lists.get(1);
                rulecondition.add(mRulecondition);
                mRuleresult.actionnotify = lists.get(3);
                mRuleresult.actioncmd = lists.get(2);
                ruleresult.add(mRuleresult);
                rules.rulecondition = rulecondition;
                rules.ruleresult = ruleresult;

                clickOkBtn();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (isEditPage) {
            tvTitle.setText(R.string.eidt_rule);
        } else {
            tvTitle.setText(R.string.create_rule);
        }
    }


    private void clickOkBtn() {
        if (isEditPage) {//编辑规则
            RequestAddRules.getInstance().editRule(AddControlRuleActivity.this, rules, mRuleInfo.id,
                    new RequestAddRules.OnCreateRuleCallback() {
                        @Override
                        public void mOnCreateRuleCallbackFail(int code, String errMsg) {
                            Logger.e(TAG, "errMsg=" + errMsg);
                            ToastUtil.showToast(AddControlRuleActivity.this, "" + errMsg);
                        }

                        @Override
                        public void mOnCreateRuleCallbackSuccess(CreateRuleResult mCreateRuleResult) {
//                    Logger.e(TAG, "mCreateRuleResult=" + mCreateRuleResult.code);
                            ToastUtil.showToast(AddControlRuleActivity.this, getString(R.string.rule_create_success));
                            finish();
                        }
                    });

        } else {//创建规则
            RequestAddRules.getInstance().createRule(AddControlRuleActivity.this, rules, new RequestAddRules.OnCreateRuleCallback() {

                @Override
                public void mOnCreateRuleCallbackFail(int code, String errMsg) {
                    Logger.e(TAG, "errMsg=" + errMsg);
                    ToastUtil.showToast(AddControlRuleActivity.this, "" + errMsg);
                }

                @Override
                public void mOnCreateRuleCallbackSuccess(CreateRuleResult mCreateRuleResult) {
                    Logger.e(TAG, "mCreateRuleResult=" + mCreateRuleResult.code);
                    ToastUtil.showToast(AddControlRuleActivity.this, getString(R.string.rule_create_success));
                    finish();
                }
            });
        }
    }


    /**
     * 过滤结果
     *
     * @return
     */
    private List<List> getResultList() {

        List<List> lists = new ArrayList<List>();

        List<Schedule> mScheduleList = new ArrayList<Schedule>();
        List<Condition> mConditionList = new ArrayList<Condition>();
        List<Actioncmd> mActioncmdList = new ArrayList<Actioncmd>();
        List<Actionnotify> mActionnotifyList = new ArrayList<Actionnotify>();
//        List<ControlRuleDevice> mControlRuleDeviceList = new ArrayList<ControlRuleDevice>();

        //条件
        for (NewRuleItemInfo info : mNewRuleConditionInfoList) {
            switch (info.getType()) {
                case 0:
                    mScheduleList.add(info.getmSchedule());
                    break;
                case 1:
                    mConditionList.add(info.getmCondition());
                    break;
                case 2:
                    mActioncmdList.add(info.getmActioncmd());
                    break;
                case 3:
                    mActionnotifyList.add(info.getmActionnotify());
                    break;
            }
        }

        //结果
        for (NewRuleItemInfo info : mNewRuleResultInfoList) {
            switch (info.getType()) {
                case 0:
                    mScheduleList.add(info.getmSchedule());
                    break;
                case 1:
                    mConditionList.add(info.getmCondition());
                    break;
                case 2:
                    mActioncmdList.add(info.getmActioncmd());
                    break;
                case 3:
                    mActionnotifyList.add(info.getmActionnotify());
                    break;
//                case 4:
//                    mControlRuleDeviceList.add(info.getmControlRuleDevice());
//                    break;
            }
        }

        lists.add(mScheduleList);
        lists.add(mConditionList);
        lists.add(mActioncmdList);
        lists.add(mActionnotifyList);

        return lists;
    }


    private void onFabPressed(Class activity) {
        Intent intent = new Intent(this, activity);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DEVICE_CONTROL", rules);
        intent.putExtra("BUNDLE", bundle);
        startActivityForResult(intent, 2018);
    }
}
