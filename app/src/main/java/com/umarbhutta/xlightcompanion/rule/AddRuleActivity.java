package com.umarbhutta.xlightcompanion.rule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.MyHelper;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.adapter.RuleItemAdapter;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmdfield;
import com.umarbhutta.xlightcompanion.okHttp.model.Condition;
import com.umarbhutta.xlightcompanion.okHttp.model.ConditionOld;
import com.umarbhutta.xlightcompanion.okHttp.model.CreateRuleResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleActioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Rulecondition;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleconditions;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleresult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rules;
import com.umarbhutta.xlightcompanion.okHttp.model.Schedule;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestAddRules;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 75932 on 2017/11/1.
 */

public class AddRuleActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private TextView tvEditSure;
    private ImageView iv_add_trigger;
    private ImageView iv_add_action;
    private LinearLayout ll_actions;
    private LinearLayout ll_no_actions;
    private LinearLayout ll_triggers;
    private LinearLayout ll_no_triggers;
    private ImageView iv_change_symbol;
    private TextView txt_add_trigger;
    private TextView txt_add_action;
    private TextView tvRuleName;
    private AlertDialog dialog;
    private TextView tvConditon;
    private ListView lvCondition;
    private ListView lvAction;
    private RuleItemAdapter conditionItemAdapter;
    private RuleItemAdapter actionItemAdapter;
    private List<Condition> condition = new ArrayList<>();
    private List<Condition> action = new ArrayList<>();
    private RuleInfo rule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_add_rule);
        if (getIntent().getSerializableExtra("rule") != null) {
            rule = (RuleInfo) getIntent().getSerializableExtra("rule");
        }
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvEditSure = (TextView) findViewById(R.id.tvEditSure);
        tvEditSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将结果传递回去
                saveRule();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.rule);
        iv_add_action = (ImageView) findViewById(R.id.iv_add_action);
        iv_add_trigger = (ImageView) findViewById(R.id.iv_add_trigger);
        iv_change_symbol = (ImageView) findViewById(R.id.iv_change_symbol);
        txt_add_action = (TextView) findViewById(R.id.txt_add_action);
        txt_add_trigger = (TextView) findViewById(R.id.txt_add_trigger);
        tvConditon = (TextView) findViewById(R.id.tvConditon);
        tvRuleName = (TextView) findViewById(R.id.tvRuleName);
        lvAction = (ListView) findViewById(R.id.lv_action);
        lvCondition = (ListView) findViewById(R.id.lv_condition);
        ll_actions = (LinearLayout) findViewById(R.id.ll_actions);
        ll_no_actions = (LinearLayout) findViewById(R.id.ll_no_actions);
        ll_triggers = (LinearLayout) findViewById(R.id.ll_triggers);
        ll_no_triggers = (LinearLayout) findViewById(R.id.ll_no_triggers);
        iv_add_trigger.setOnClickListener(this);
        iv_add_action.setOnClickListener(this);
        txt_add_action.setOnClickListener(this);
        txt_add_trigger.setOnClickListener(this);
        findViewById(R.id.ivRuleName).setOnClickListener(this);
        tvRuleName.setOnClickListener(this);
        conditionItemAdapter = new RuleItemAdapter(this, condition);
        actionItemAdapter = new RuleItemAdapter(this, action);
        lvCondition.setAdapter(conditionItemAdapter);
        lvAction.setAdapter(actionItemAdapter);
//        if (rule == null) {
        iv_change_symbol.setOnClickListener(this);
        tvConditon.setOnClickListener(this);
        lvCondition.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position, 1);
                return false;
            }
        });
        lvAction.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position, 2);
                return false;
            }
        });
//        }
        initDialog();
        if (rule != null) {
            initEdit();
        }
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public void hideSoftInput(IBinder token) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, RuleConditionActivity.class);
        switch (v.getId()) {
            case R.id.iv_add_trigger:
            case R.id.txt_add_trigger:
                intent.putExtra("type", 1);
                int exist = 0; // 0 代表不存在 1 存在Sensor  2 存在定时
                for (Condition c : condition) {
                    if (c.type.equals("sensor")) {
                        exist = 1;
                    } else if (c.type.equals("time")) {
                        exist = 2;
                    }
                }
                intent.putExtra("exist", exist);
                startActivityForResult(intent, 1);
                break;
            case R.id.iv_add_action:
            case R.id.txt_add_action:
                intent.putExtra("type", 3);
                startActivityForResult(intent, 2);
                break;
            case R.id.tvRuleName:
            case R.id.ivRuleName:
                dialog.show();
                break;
            case R.id.iv_change_symbol:
            case R.id.tvConditon:
                if (tvConditon.getText().toString().equals(getString(R.string.ifall))) {
                    tvConditon.setText(getString(R.string.ifany));
                } else {
                    tvConditon.setText(getString(R.string.ifall));
                }
                break;
        }
    }

    private EditText editText;
    private AlertDialog.Builder builder;

    private void initDialog() {
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.layout_edittext, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
        editText = (EditText) view.findViewById(R.id.etName);
        editText.setText(tvRuleName.getText().toString().equals(getString(R.string.edit_name)) ? "" : tvRuleName.getText().toString());
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyBoard();
                // 关闭
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rll_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用保存接口
                hintKeyBoard();
                // 设置名称
                if (!editText.getText().toString().equals("")) {
                    tvRuleName.setText(editText.getText().toString());
                } else {
                    tvRuleName.setText(R.string.edit_name);
                }
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    public void hintKeyBoard() {
        editText.clearFocus();
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public String getWeek(int week) {
        switch (week) {
            case 0:
                return getString(R.string.week0);
            case 2:
                return getString(R.string.week1);
            case 3:
                return getString(R.string.week2);
            case 4:
                return getString(R.string.week3);
            case 5:
                return getString(R.string.week4);
            case 6:
                return getString(R.string.week5);
            case 7:
                return getString(R.string.week6);
            case 1:
                return getString(R.string.week7);
            default:
                return getString(R.string.week1);
        }
    }

    public String getOperator(int operator) {
        switch (operator) {
            case 0:
                return "=";
            case 1:
                return "≠";
            case 2:
                return ">";
            case 3:
                return "≥";
            case 4:
                return "<";
            case 5:
                return "≤";
            case 6:
                return getString(R.string.between);
            case 7:
                return getString(R.string.notbetween);
            default:
                return "=";
        }
    }

    public void initEdit() {
        // 将当前对象进行转换
//        tvEditSure.setVisibility(View.GONE);
//        iv_add_trigger.setVisibility(View.GONE);
//        iv_add_action.setVisibility(View.GONE);
        tvRuleName.setText(rule.rulename);
        tvConditon.setText(rule.relationtype == 1 ? getString(R.string.ifall) : getString(R.string.ifany));
        try {
            // 开始解析生成条件
            for (Ruleconditions rc : rule.ruleconditions) {
                if (rc.ruleconditiontype == 1) {
                    Condition c = new Condition();
                    c.type = "time";
                    c.object = new JSONObject();
                    c.object.put("isRepeat", rc.isrepeat == 1 ? true : false);
                    c.object.put("week", rc.weekdays);
                    c.object.put("hour", rc.hour);
                    c.object.put("minute", rc.minute);
                    refreshCondition(c);
                } else if (rc.ruleconditiontype == 0) {
                    Condition c = new Condition();
                    c.type = "sensor";
                    c.object = new JSONObject();
                    c.object.put("sensor", rc.sensorId);
                    c.object.put("symbol", rc.operator);
                    c.object.put("value", rc.rightValue.split(",")[0]);
                    if (rc.operator.equals("6") || rc.operator.equals("7")) {
                        c.object.put("value1", rc.rightValue.split(",")[1]);
                    } else {
                        c.object.put("value1", 0);
                    }
                    refreshCondition(c);
                }
            }
            // 开始解析生成动作
            for (RuleActioncmd rs : rule.ruleactioncmds) {
                for (Actioncmdfield af : rs.actioncmdfields) {
                    Devicenodes nd = new Devicenodes();
                    nd.nodeno = rs.devicenodeId;
                    JSONObject jb = new JSONObject(af.paralist);
                    nd.coreid = jb.getString("deviceId");
                    nd.ison = jb.getInt("state");
                    if (!jb.isNull("filter")) {
                        nd.filter = jb.getInt("filter");
                    }
                    if (!jb.isNull("br")) {
                        nd.brightness = jb.getInt("br");
                    }
                    if (!jb.isNull("cct")) {
                        nd.cct = jb.getInt("cct");
                    }
                    if (!jb.isNull("r")) {
                        nd.color = new int[]{jb.getInt("r"), jb.getInt("g"), jb.getInt("b")};
                    }
                    for (Devicenodes node : GlanceMainFragment.devicenodes) {
                        if (node.nodeno == nd.nodeno && node.coreid.equals(nd.coreid)) {
                            nd.devicenodename = node.devicenodename;
                        }
                    }
                    refreshAction(nd);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshCondition(Condition c) {
        try {
            // 保存条件
            if (c.type.equals("sensor")) {
                c.name = c.object.getInt("sensor") == 0 ? getString(R.string.dhtt) : getString(R.string.dhth);
                c.remark = c.name + " " + getOperator(c.object.getInt("symbol")) + " " + c.object.getInt("value");
                if (c.object.getInt("symbol") == 6 || c.object.getInt("symbol") == 7) {
                    c.remark = c.remark + " " + getString(R.string.and) + " " + c.object.getInt("value1");
                }
                // 判断是否已经包含，如果已经包含的话，采用替换原则
                for (int i = 0; i < condition.size(); i++) {
                    if (condition.get(i).type.equals(c.type) && condition.get(i).object.getInt("sensor") == c.object.getInt("sensor"))
                        condition.remove(i);
                }
            } else if (c.type.equals("time")) {
                c.state = c.object.getBoolean("isRepeat") ? getString(R.string.repeat) : getString(R.string.once);
                c.remark = getWeek(c.object.getInt("week"));
                c.name = getString(R.string.timer) + " " + (c.object.getInt("hour") < 10 ? ("0" + c.object.getInt("hour")) : c.object.getInt("hour")) + ":" + (c.object.getInt("minute") < 10 ? ("0" + c.object.getInt("minute")) : c.object.getInt("minute"));
                // 判断是否已经包含，如果已经包含的话，采用替换原则
                for (int i = 0; i < condition.size(); i++) {
                    if (condition.get(i).type.equals(c.type))
                        condition.remove(i);
                }
            }
            condition.add(c);
            conditionItemAdapter.notifyDataSetChanged();
            if (condition.size() > 0) {
                ll_triggers.setVisibility(View.VISIBLE);
                ll_no_triggers.setVisibility(View.GONE);
            } else {
                ll_triggers.setVisibility(View.GONE);
                ll_no_triggers.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {

        }
    }

    private void refreshAction(Devicenodes nd) {
        Condition c = new Condition();
        c.type = "light";
        c.name = nd.devicenodename;
        c.state = nd.ison == 1 ? getString(R.string.on) : getString(R.string.off);
        if (nd.ison == 1) {
            c.remark = getString(R.string.brightness) + ": " + nd.brightness;
            if (nd.scenarioId != null && nd.scenarioId.equals("CCT")) {
                c.remark = c.remark + " " + getString(R.string.txt_cct) + ": " + nd.cct;
            } else {
                c.remark = c.remark + " " + getString(R.string.txt_color) + ": (" + nd.color[0] + "," + nd.color[1] + "," + nd.color[2] + ")";
            }
            if (nd.filter > 0) {
                c.remark = c.remark + " " + getString(R.string.tab_4) + ": " + (nd.filter == 1 ? getString(R.string.filter1) : nd.filter == 2 ? getString(R.string.filter2) : nd.filter == 3 ? getString(R.string.filter3) : getString(R.string.filter4));
            }
        }
        try {
            c.object = new JSONObject();
            c.object.put("light", nd);
            action.clear();
        } catch (Exception ex) {
        }
        action.add(c);
        actionItemAdapter.notifyDataSetChanged();
        if (action.size() > 0) {
            ll_actions.setVisibility(View.VISIBLE);
            ll_no_actions.setVisibility(View.GONE);
        } else {
            ll_actions.setVisibility(View.GONE);
            ll_no_actions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == 1) {
                // 保存条件
                Condition c = new Condition();
                if (data.getStringExtra("type").equals("sensor")) {
                    c.type = "sensor";
                    c.object = new JSONObject();
                    c.object.put("sensor", data.getIntExtra("sensor", 0));
                    c.object.put("symbol", data.getIntExtra("operator", 0));
                    c.object.put("value", data.getIntExtra("min", 0));
                    if (data.getIntExtra("operator", 0) == 6 || data.getIntExtra("operator", 0) == 7) {
                        c.object.put("value1", data.getIntExtra("max", 0));
                    } else {
                        c.object.put("value1", 0);
                    }
                    refreshCondition(c);
                } else if (data.getStringExtra("type").equals("time")) {
                    c.type = "time";
                    c.object = new JSONObject();
                    c.object.put("isRepeat", data.getBooleanExtra("repeat", true));
                    c.object.put("week", data.getIntExtra("week", 0));
                    c.object.put("hour", data.getIntExtra("hour", 0));
                    c.object.put("minute", data.getIntExtra("minute", 0));
                    refreshCondition(c);
                }
            } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                Devicenodes nd = (Devicenodes) data.getSerializableExtra("device");
                refreshAction(nd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showDeleteDialog(final int position, final int type) {
        AlertDialog mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.delete_notify_title))
                .setMessage(getString(R.string.delete_notify))
                .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                            condition.remove(position);
                            conditionItemAdapter.notifyDataSetChanged();
                            if (condition.size() == 0) {
                                ll_triggers.setVisibility(View.GONE);
                                ll_no_triggers.setVisibility(View.VISIBLE);
                            }
                        } else {
                            action.remove(position);
                            actionItemAdapter.notifyDataSetChanged();
                            if (action.size() == 0) {
                                ll_actions.setVisibility(View.GONE);
                                ll_no_actions.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

        Button btn1 = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void saveRule() {
        // 保存
        if (isValid()) {
            try {
                Devicenodes nd = new Gson().fromJson(action.get(0).object.getString("light").split("Devicenodes")[1].replaceAll("=", ":"), Devicenodes.class);
                // sendCommand(SlidingMenuMainActivity.xltDeviceMaps.get(nd.coreid), nd);
                requestUpdateRule(nd);
//                xltDevice m_xltDevice;
//                if (SlidingMenuMainActivity.xltDeviceMaps.get(nd.coreid) != null) {
//                    m_xltDevice = SlidingMenuMainActivity.xltDeviceMaps.get(nd.coreid);
//                } else {
//                    m_xltDevice = new xltDevice();
//                    m_xltDevice.Init(getBaseContext(), UserUtils.getUserInfo(getBaseContext()).username, UserUtils.getUserInfo(getBaseContext()).password);
//                }
//                m_xltDevice.Connect(nd.coreid, new xltDevice.callbackConnect() {
//                    @Override
//                    public void onConnected(xltDevice.BridgeType bridge, boolean connected) {
//                        if (connected) {
//                            // sendCommand(m_xltDevice, nd);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    sendCommand(m_xltDevice, nd);
//                                }
//                            });
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ToastUtil.showToast(getBaseContext(), R.string.rule_device_offline);
//                                }
//                            });
//                        }
//                    }
//                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isValid() {
        if (tvRuleName.getText().toString().equals("") || tvRuleName.getText().toString().equals(getString(R.string.edit_name))) {
            ToastUtil.showToast(this, R.string.rule_name_empty);
            return false;
        } else if (condition.size() == 0) {
            ToastUtil.showToast(this, R.string.rule_condition_empty);
            return false;
        } else if (action.size() == 0) {
            ToastUtil.showToast(this, R.string.rule_action_empty);
            return false;
        }
        return true;
    }

    private void sendCommand(xltDevice m_xltDevice, Devicenodes nd) {
        try {
            m_xltDevice.clearSequential();
            // 添加场景
            m_xltDevice.sceAddScenario(4, nd.ison, nd.brightness, nd.cct, nd.color[0], nd.color[1], nd.color[2], nd.filter);
            int[][] cond = new int[2][];
            int scheduleId = 0;
            int i = 0;
            for (Condition c : condition) {
                // 添加定时任务
                if (c.type.equals("time")) {
                    scheduleId = 1;
                    m_xltDevice.sceAddSchedule(4, c.object.getBoolean("isRepeat"), c.object.getInt("week"), c.object.getInt("hour"), c.object.getInt("minute"), 0);
                } else {
                    // 开始构造cond
                    cond[i] = new int[]{c.object.getInt("symbol"), 1, c.object.getInt("sensor"), c.object.getInt("value"), c.object.getInt("value1")};
                    i++;
                }
            }
            m_xltDevice.sceAddRule(4, cond, scheduleId, 4);
            m_xltDevice.sequentialExecutor();
            // finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void requestUpdateRule(Devicenodes nd) {
        Rules rule = new Rules();
        if (this.rule != null && this.rule.id != 0) {
            rule.id = this.rule.id;
        }
        List<Rulecondition> rulecondition = new ArrayList<>();
        List<Ruleresult> ruleresult = new ArrayList<>();
        Rulecondition rd = new Rulecondition();
        Ruleresult rs = new Ruleresult();
        List<ConditionOld> lstConditons = new ArrayList<>();
        List<Schedule> lstSchedule = new ArrayList<>();
        List<Actioncmd> actioncmds = new ArrayList<>();
        rd.condition = lstConditons;
        rd.schedule = lstSchedule;
        rs.actioncmd = actioncmds;
        rulecondition.add(rd);
        ruleresult.add(rs);
        rule.rulecondition = rulecondition;
        rule.ruleresult = ruleresult;
        // 开始构建对象
        rule.rulename = tvRuleName.getText().toString();
        rule.relationtype = tvConditon.getText().toString().equals(getString(R.string.ifall)) ? 1 : 2;
        rule.type = 1;
        rule.status = 1;
        rule.userId = UserUtils.getUserInfo(this).id;
        for (Condition c : condition) {
            if (c.type.equals("time")) {
                try {
                    Schedule s = new Schedule();
                    s.ruleconditiontype = 1; // 定时 2  ， 传感器 1
                    s.ruleconditionname = "Schedule";
                    s.weekdays = c.object.getString("week");
                    s.hour = c.object.getInt("hour");
                    s.minute = c.object.getInt("minute");
                    s.status = 1;
                    s.isrepeat = c.object.getBoolean("isRepeat") ? 1 : 0;
                    lstSchedule.add(s);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (c.type.equals("sensor")) {
                try {
                    ConditionOld co = new ConditionOld();
                    co.ruleconditiontype = 0; // 定时 1  ， 传感器 0
                    co.ruleconditionname = "Sensor";
                    co.sensorId = c.object.getInt("sensor");
                    co.attribute = c.object.getInt("sensor") == 0 ? getString(R.string.dhtt) : getString(R.string.dhth);
                    co.operator = c.object.getString("symbol");
                    co.rightValue = c.object.getString("value");
                    if (co.operator.equals("6") || co.operator.equals("7")) {
                        co.rightValue = co.rightValue + "," + c.object.getString("value1");
                    }
                    co.status = 1;
                    lstConditons.add(co);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        for (Condition a : action) {
            if (a.type.equals("light")) {
                Actioncmd action = new Actioncmd();
                action.devicenodeId = nd.nodeno;
                action.actiontype = 1;
                try {
                    JSONObject jb = new JSONObject();
                    rule.deviceId = nd.coreid;
                    jb.put("deviceId", nd.coreid);
                    jb.put("state", nd.ison);
                    jb.put("nd", nd.nodeno);
                    if (nd.ison == 1) {
                        jb.put("br", nd.brightness);
                        if (nd.scenarioId.equals("CCT")) {
                            jb.put("cct", nd.cct);
                        } else {
                            jb.put("r", nd.color[0]);
                            jb.put("g", nd.color[1]);
                            jb.put("b", nd.color[2]);
                        }
                        if (nd.filter > 0) {
                            jb.put("filter", nd.filter);
                        }
                    }
                    Actioncmdfield af = new Actioncmdfield();
                    af.cmd = "Rule Action Command";
                    af.paralist = jb.toString();
                    action.actioncmdfield = new ArrayList<Actioncmdfield>();
                    action.actioncmdfield.add(af);
                    actioncmds.add(action);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        ToastUtil.showLoading(this);
        if (rule.id > 0) {
            edit(rule);
        } else {
            create(rule);
        }
    }

    private void create(Rules rule) {
        RequestAddRules.getInstance().createRule(this, rule, new RequestAddRules.OnCreateRuleCallback() {
            @Override
            public void mOnCreateRuleCallbackFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        if (code == 40000) {
                            ToastUtil.showToast(getBaseContext(), R.string.rule_device_offline);
                        } else if (code == 40001) {
                            ToastUtil.showToast(getBaseContext(), R.string.rule_max_length);
                        } else {
                            ToastUtil.showToast(getBaseContext(), R.string.save_error);
                        }
                    }
                });
            }

            @Override
            public void mOnCreateRuleCallbackSuccess(CreateRuleResult mCreateRuleResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(getBaseContext(), R.string.save_success);
                        finish();
                    }
                });
            }
        });
    }

    private void edit(Rules rule) {
        RequestAddRules.getInstance().editRule(this, rule, rule.id, new RequestAddRules.OnCreateRuleCallback() {
            @Override
            public void mOnCreateRuleCallbackFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        if (code == 40000) {
                            ToastUtil.showToast(getBaseContext(), R.string.rule_device_offline);
                        } else if (code == 40001) {
                            ToastUtil.showToast(getBaseContext(), R.string.rule_max_length);
                        } else {
                            ToastUtil.showToast(getBaseContext(), R.string.save_error);
                        }
                    }
                });
            }

            @Override
            public void mOnCreateRuleCallbackSuccess(CreateRuleResult mCreateRuleResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(getBaseContext(), R.string.save_success);
                        finish();
                    }
                });
            }
        });
    }
}