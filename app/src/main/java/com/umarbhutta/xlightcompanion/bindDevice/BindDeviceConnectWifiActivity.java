package com.umarbhutta.xlightcompanion.bindDevice;

import android.os.Bundle;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

/**
 * Created by Administrator on 2017/3/4.
 */

public class BindDeviceConnectWifiActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device_connect_wifi);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
    }
}
