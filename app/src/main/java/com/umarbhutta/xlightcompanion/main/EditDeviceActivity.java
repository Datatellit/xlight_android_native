package com.umarbhutta.xlightcompanion.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/5.
 * 设置灯
 */

public class EditDeviceActivity extends BaseActivity implements View.OnClickListener {


    private String TAG = "XLight";
    private LinearLayout llBack;
    private TextView tvTitle;
    private Devicenodes devicenodes;
    private TextView txtName;
    private LinearLayout llRemove;
    private LinearLayout llEdit;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_light);
        devicenodes = (Devicenodes) getIntent().getSerializableExtra("info");
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 修改数据，同步名称
                Intent intent = new Intent();
                intent.putExtra("name", devicenodes.devicenodename);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.tvEditSure).setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.setting);
        txtName = (TextView) findViewById(R.id.txtName);
        llRemove = (LinearLayout) findViewById(R.id.ll_remove);
        llEdit = (LinearLayout) findViewById(R.id.ll_name);
        txtName.setText(devicenodes.devicenodename);

        llRemove.setOnClickListener(this);
        llEdit.setOnClickListener(this);
        initDialog();
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(R.id.ll_top_edit).init();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("name", devicenodes.devicenodename);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    private void initDialog() {
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.layout_edittext, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
        editText = (EditText) view.findViewById(R.id.etName);
        editText.setText(devicenodes.devicenodename);
        ((ImageView) view.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyBoard();
                // 关闭
                dialog.dismiss();
            }
        });
        ((RoundLinearLayout) view.findViewById(R.id.rll_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用保存接口
                hintKeyBoard();
                editDeViceInfo(editText.getText().toString());
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_name) {
            // 弹出编辑名称页面
            dialog.show();
        } else if (v.getId() == R.id.ll_remove) {
            // 提示删除，当前暂不实现
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Intent intent = new Intent();
//        intent.putExtra("name", devicenodes.devicenodename);
//        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    /**
     * 提交编辑设备
     */
    private void editDeViceInfo(String newDeviceName) {
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }
        ToastUtil.showLoading(this);
        final String deviceName = TextUtils.isEmpty(newDeviceName) ? "" : newDeviceName;
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtil.showToast(this, getString(R.string.please_set_lamp_name));
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("devicenodename", deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_EDIT_DEVICE_INFO, devicenodes.id, UserUtils.getAccessToken(this)),
                jsonObject.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
                    @Override
                    public void onHttpRequestSuccess(Object result) {
                        Logger.i("编辑成功 = " + result.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(deviceName)) {
                                    ToastUtil.dismissLoading();
                                    txtName.setText(deviceName);
                                    devicenodes.devicenodename = deviceName;
                                    ToastUtil.showToast(getBaseContext(), getString(R.string.deivce_modify_success));
                                }
                            }
                        });
                    }

                    @Override
                    public void onHttpRequestFail(int code, final String errMsg) {
                        Logger.i("编辑失败 = " + errMsg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.dismissLoading();
                                ToastUtil.showToast(getBaseContext(), getString(R.string.modify_fail) + errMsg);
                            }
                        });
                    }
                });
    }
}
