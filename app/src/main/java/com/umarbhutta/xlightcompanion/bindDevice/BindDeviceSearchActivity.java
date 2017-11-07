package com.umarbhutta.xlightcompanion.bindDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.help.WifiAdmin;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by 75932 on 2017/11/1.
 */

public class BindDeviceSearchActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice bluetoothXlight;
    private int scanTime = 60;
    private CircularProgressBar progressBar;
    private TextView txtTime;
    private boolean myThread = true;
    private int scanCount = 0;
    private int type = 0;
    private WifiReceiver mWifiReceiver;
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bind_device_two);

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
        tvTitle.setText(R.string.find_devices);

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTime.setText("" + scanTime);
        progressBar = (CircularProgressBar) findViewById(R.id.cpbProgress);
        new Thread(new MyThread()).start();
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            ((TextView) findViewById(R.id.txtTitle)).setText(R.string.add_device_two_title2);
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mWifiManager.startScan();
            //搜索网络并加入
            WlanScan();
        } else {
            BluetoothScan();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }

    public void unBindReceiver() {
        try {
            this.unregisterReceiver(mBluetoothReceiver);
        } catch (Exception e) {
            Log.e("XLight", e.getMessage(), e);
        }
    }

    final Handler handler = new Handler() {          // handle
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (scanTime > 0) {
                        scanTime--;
                        progressBar.setProgress((float) (scanTime * 1.66));
                        txtTime.setText("" + scanTime);
                    }
                    break;
                case 2:
                    // 跳转到下一步
                    myThread = false;
                    Log.d("XLight", "Search Timeout,Redirect to ErrorActivity");
                    Intent intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class MyThread implements Runnable {      // thread
        @Override
        public void run() {
            int t = 0;
            while (scanTime > 0) {
                try {
                    if (bluetoothXlight != null || !myThread) {
                        t = 1;
                        break;
                    }
                    if (scanTime > 1)
                        Thread.sleep(1000);     // sleep 1000ms
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
            if (t == 0) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }

    public void BluetoothScan() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBluetoothReceiver, filter);
        mBluetoothAdapter.startDiscovery();
        Log.d("XLight", "Bluetooth scan start");
    }

    public void WlanScan() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, filter);
        getWifiList();
        updateWifiHandler.sendEmptyMessageDelayed(1, 5000);
    }

    /**
     * wifi是否打开
     *
     * @return
     */
    private boolean isWifiContect() {
        if (mWifiManager.isWifiEnabled()) {
            return true;
        }
        return false;
    }

    private boolean stopScanWifi = false;

    /**
     * wifi列表
     */
    private void getWifiList() {
        if (isWifiContect() && !stopScanWifi) {
            if (mWifiManager.getConnectionInfo().getSSID().contains("Photon-") && mWifiManager.getConnectionInfo().getSSID().length() == 11) {
                connectWifi(mWifiManager.getConnectionInfo().getSSID());
                return;
            }
            Log.d("XLight", "get scan wifi result");
            //获取结果
            List<ScanResult> wifiScanList = mWifiManager.getScanResults();
            //查看是否存在Photon-xxxx
            for (ScanResult sr : wifiScanList) {
                if (sr.SSID.contains("Photon-") && sr.SSID.length() == 11) {
                    connectWifi(sr.SSID);
                }
            }
        }
    }

    public void connectWifi(String ssid) {
        //停止扫描
        stopScanWifi = true;
        updateWifiHandler.removeCallbacks(runnable);
        Log.d("XLight", "connect to photon wifi");
        //连接到此网络
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        WifiConfiguration tempConfig = wifiAdmin.IsExsits(ssid);
        if (tempConfig != null && tempConfig.networkId != -1) {
            Log.d("XLight", "photon netId:" + tempConfig.networkId);
            mWifiManager.enableNetwork(tempConfig.networkId, true);
        } else
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, "", 1));
        //连接失败回到自己的网络
        mWifiManager.reconnect();
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                //获取当前的wifi状态int类型数据
                int mWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (mWifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //已打开
                        getWifiList();
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        //打开中
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //已关闭
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        //关闭中
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        //未知
                        break;
                }
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
                Log.d("XLight", "网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.d("XLight", "wifi网络连接断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    //获取当前wifi名称
                    Log.d("XLight", "连接到网络 " + ssid + ssid.contains("Photon-") + ssid.length());
                    if (ssid.contains("Photon-") && ssid.length() >= 11) {
                        //连接成功，跳到下一个页面
                        Intent intent1 = new Intent(getApplicationContext(), BindDeviceWiFiActivity.class);
                        intent1.putExtra("type", type);
                        startActivity(intent1);
                        finish();
                    }
                }
            }
        }
    }

    Handler updateWifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!stopScanWifi) {
                updateWifiHandler.postDelayed(runnable, 100);
                updateWifiHandler.sendEmptyMessageDelayed(1, 5000);
            }
        }
    };

    /**
     * 定时更新wifi列表
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getWifiList();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (type == 1) {
            unregisterReceiver(mWifiReceiver);
        } else {
            unBindReceiver();
        }
        myThread = false;
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("XLight", "mBluetoothReceiver action =" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;
                Log.d("XLight", "name=" + scanDevice.getName() + ",address=" + scanDevice.getAddress());
                //蓝牙设备名称
                if (scanDevice.getName().toLowerCase().equals("XLight".toLowerCase())) {
                    bluetoothXlight = scanDevice;
                    //跳转到下一个页面
                    Log.d("XLight", "Bluetooth scan stop");
                    myThread = false;
                    Intent intent1 = new Intent(getApplicationContext(), BindDeviceWiFiActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", bluetoothXlight.getName());
                    bundle.putString("mac", bluetoothXlight.getAddress());
                    intent1.putExtra("bundle", bundle);
                    Log.d("XLight", "Bluetooth result:" + bluetoothXlight.getName() + "," + bluetoothXlight.getAddress());
                    startActivity(intent1);
                    finish();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //结束，跳转到扫描失败页面
                Log.d("XLight", "Bluetooth scan end");
                if (scanCount < 3) {
                    mBluetoothAdapter.startDiscovery();
                    scanCount++;
                } else {
                    myThread = false;
                    Log.d("XLight", "Scan end and scanCount > 3,Redirect to ErrorActivity," + scanCount);
                    Intent intent1 = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                    intent1.putExtra("type", type);
                    startActivity(intent1);
                    finish();
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.enable()) {
                    mBluetoothAdapter.startDiscovery();
                    Log.d("XLight", "Bluetooth scan start");
                } else {
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d("XLight", "Bluetooth scan stop");
                }
            } else {
                Log.e("XLight", "No Action Match:" + action);
                mBluetoothAdapter.startDiscovery();
            }
        }

    };
}
