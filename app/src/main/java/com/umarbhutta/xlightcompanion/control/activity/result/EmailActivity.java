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
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Actionnotify;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

/**
 * Created by Administrator on 2017/3/15.
 * email 通知页面
 */

public class EmailActivity extends BaseActivity {


    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;

    private EditText et_email, et_title, et_context;

    private Actionnotify mActionnotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();
        ((App)getApplicationContext()).setActivity(this);
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
        btnSure.setText(R.string.complete);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.email_notify);

        et_email = (EditText) findViewById(R.id.et_email);
        et_title = (EditText) findViewById(R.id.et_title);
        et_context = (EditText) findViewById(R.id.et_context);

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String emailTitle = et_title.getText().toString();
                String emailContext = et_context.getText().toString();
                if(TextUtils.isEmpty(email)){
                    ToastUtil.showToast(getApplicationContext(),getString(R.string.please_input_email));
                    return;
                }
                if(!StringUtil.isEmail(email)){
                    ToastUtil.showToast(getApplicationContext(),getString(R.string.please_input_right_email));
                    return;
                }
                if(TextUtils.isEmpty(emailTitle)){
                    ToastUtil.showToast(getApplicationContext(),getString(R.string.please_input_notify_email_theme));
                    return;
                }
                if(TextUtils.isEmpty(emailContext)){
                    ToastUtil.showToast(getApplicationContext(),getString(R.string.please_input_notify_content));
                    return;
                }
                mActionnotify.emailaddress = email;
                mActionnotify.content = emailContext;
                mActionnotify.subject = emailTitle;
                mActionnotify.actionnotifyType = 1;
                mActionnotify.name = getString(R.string.email_notify)+"：";
                mActionnotify.actiontype = 1;
                NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
                mNewRuleItemInfo.setmActionnotify(mActionnotify);
                AddControlRuleActivity.mNewRuleResultInfoList.add(mNewRuleItemInfo);

                ((App)getApplicationContext()).finishActivity();
            }
        });
    }

}
