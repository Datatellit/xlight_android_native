package com.umarbhutta.xlightcompanion.bindDevice;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ExecutionError;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.imgloader.Utils;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

/**
 * Created by 75932 on 2017/11/1.
 */

public class BindDeviceConfirmActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private BluetoothAdapter bluetoothAdapter;
    private boolean myThread = true;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bind_device_one);

        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }

        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        ViewGroup.LayoutParams params = rootLayout.getLayoutParams();
        params.height = DisplayUtils.getScreenHeight(this) - 100;
        rootLayout.setLayoutParams(params);

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
        tvTitle.setText(R.string.confirm_status);

        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            ((TextView) findViewById(R.id.txtDesc)).setVisibility(View.VISIBLE);
            ((RadioButton) findViewById(R.id.rbSure)).setText(R.string.add_device_one_sure_desc);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void checkClick(View v) {
        RadioButton rb = (RadioButton) v;
        if (rb.isChecked()) {
            findViewById(R.id.btnNext).setEnabled(true);
            findViewById(R.id.btnNext).setBackgroundColor(getResources().getColor(R.color.bar_color));
        } else {
            findViewById(R.id.btnNext).setEnabled(false);
            findViewById(R.id.btnNext).setBackgroundColor(getResources().getColor(R.color.pickerview_wheelview_textcolor_divider));
        }
    }

    public void nextClick(View v) {
        // 打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // 请打开蓝牙
            ToastUtil.showToast(this, R.string.notfound_bluetooth);
        } else {
            try {
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                    myThread = true;
                    new Thread(new MyThread()).start();
                } else {
                    Intent intent = new Intent(this, BindDeviceSearchActivity.class);
                    intent.putExtra("type", type);
                    startActivityForResult(intent, 1);
                }
            } catch (Exception e) {
                // 请打开蓝牙
                ToastUtil.showToast(this, R.string.please_open_blue);
            }
        }
    }

    final Handler handler = new Handler() {          // handle
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //已打开蓝牙，跳转
                    myThread = false;
                    Intent intent = new Intent(getApplicationContext(), BindDeviceSearchActivity.class);
                    startActivityForResult(intent, 1);
                    break;
                case 2:
                    // 超时未打开
                    ToastUtil.showToast(getApplicationContext(), R.string.please_open_blue);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class MyThread implements Runnable {      // thread
        @Override
        public void run() {
            int t = 0;
            while (t < 30) {
                try {
                    if (!myThread) {
                        break;
                    }
                    Thread.sleep(1000);     // sleep 1000ms
                    if (bluetoothAdapter.isEnabled()) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        break;
                    }
                } catch (Exception e) {
                }
            }
            if (t >= 30) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }

}
