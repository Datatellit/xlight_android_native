package com.umarbhutta.xlightcompanion.bindDevice;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

/**
 * Created by 75932 on 2017/11/1.
 */

public class BindDeviceSuccessActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private String coreID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bind_device_success);

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
        TextView btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.find_devices);
        coreID = getIntent().getStringExtra("coreID");
        Log.d("XLight", "coreID:" + coreID);
        EditText et = (EditText) findViewById(R.id.etControllerName);
        et.setSelection(et.getText().length());
    }

    @Override
    public void onClick(View v) {

    }

    public void nextClick(View v) {
        EditText et = (EditText) findViewById(R.id.etControllerName);
        Log.d("XLight", et.getText().toString());
        if (et.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, getResources().getString(R.string.add_device_success_name));
            return;
        }
        // 进入下一步
        Intent intent = new Intent(this, BindDeviceBulbActivity.class);
        intent.putExtra("coreID", coreID);
        intent.putExtra("deviceName", et.getText().toString());
        intent.putExtra("mainDevice", (((CheckBox) findViewById(R.id.chkMain)).isChecked() ? 1 : 0));
        startActivity(intent);
    }
}
