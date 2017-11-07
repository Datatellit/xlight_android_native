package com.umarbhutta.xlightcompanion.control.activity.result;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Actionnotify;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

/**
 * Created by Administrator on 2017/3/15.
 * email 通知页面
 */

public class AppNotifyActivity extends BaseActivity {


    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private EditText et_time;

    private Actionnotify mActionnotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_notify);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();
        ((App) getApplicationContext()).setActivity(this);
        mActionnotify = (Actionnotify) getIntent().getSerializableExtra("MACTIONNOTIFY");
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
        tvTitle.setText(R.string.app_notify);
        btnSure.setText(R.string.complete);
        et_time = (EditText) findViewById(R.id.tv_time);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定提交按钮
                String appContext = et_time.getText().toString();
                if (TextUtils.isEmpty(appContext)) {
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.please_input_notify_content));
                    return;
                }
                mActionnotify.msisdn = UserUtils.getUserInfo(AppNotifyActivity.this.getApplicationContext()).getUsergroupId();
                mActionnotify.content = appContext;
                mActionnotify.subject = getString(R.string.app_notify)+"：";
                mActionnotify.actionnotifyType = 2;
                mActionnotify.name = getString(R.string.app_notify);
                mActionnotify.actiontype = 2;

                NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
                mNewRuleItemInfo.setmActionnotify(mActionnotify);
                AddControlRuleActivity.mNewRuleResultInfoList.add(mNewRuleItemInfo);
                ((App) getApplicationContext()).finishActivity();
            }
        });
    }

}
