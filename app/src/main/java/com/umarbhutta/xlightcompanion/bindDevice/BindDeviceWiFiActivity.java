package com.umarbhutta.xlightcompanion.bindDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.CloudAccount;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.help.ClsUtils;
import com.umarbhutta.xlightcompanion.help.WifiAdmin;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.CheckDeviceResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ScanAP;
import com.umarbhutta.xlightcompanion.okHttp.model.ScanAPs;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.RequestCheckDevice;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.devicesetup.ApConnector;
import io.particle.android.sdk.devicesetup.commands.CommandClient;
import io.particle.android.sdk.devicesetup.commands.ConfigureApCommand;
import io.particle.android.sdk.devicesetup.commands.ConnectAPCommand;
import io.particle.android.sdk.devicesetup.commands.DeviceIdCommand;
import io.particle.android.sdk.devicesetup.commands.PublicKeyCommand;
import io.particle.android.sdk.devicesetup.commands.ScanApCommand;
import io.particle.android.sdk.devicesetup.commands.SetCommand;
import io.particle.android.sdk.devicesetup.commands.data.WifiSecurity;
import io.particle.android.sdk.devicesetup.model.ScanAPCommandResult;
import io.particle.android.sdk.devicesetup.setupsteps.SetupStepException;
import io.particle.android.sdk.utils.Crypto;
import io.particle.android.sdk.utils.EZ;
import io.particle.android.sdk.utils.Funcy;
import io.particle.android.sdk.utils.SSID;

import static io.particle.android.sdk.utils.Py.set;
import static io.particle.android.sdk.utils.Py.truthy;

/**
 * Created by 75932 on 2017/11/1.
 */

public class BindDeviceWiFiActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice bluetoothXlight;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<String>();
    private List<ScanResult> wifiScanList;
    private List<ScanResult> wifiList24 = new ArrayList<ScanResult>();
    private ScanResult connectResult;
    private WifiAdmin wifiAdmin;
    private BluetoothInfo bluetoothInfo;
    private xltDevice _xltDevice = null;
    private TextView etSSID = null;
    private String coreID = null;
    final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
    private int step = 0;
    private WifiReceiver mWifiReceiver;
    private final int WIFI_PERMISSION_REQ_CODE = 100;
    private int type = 0;
    private EditText etPassword;
    private List<ScanApCommand.Scan> aps;
    private ScanApCommand.Scan selectAp;
    final java.util.Timer timer = new java.util.Timer(true);
    CommandClient commandClient = null;


    public class BluetoothInfo {
        String name;
        String address;

        public BluetoothInfo(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }

    public class WifiInfo {
        String ssid;
        String password;
        String capabilities = "WPA2";

        public WifiInfo(String ssid, String password, String capabilities) {
            this.ssid = ssid;
            this.password = password;
            this.capabilities = capabilities;
        }

        public WifiInfo(String ssid, String password) {
            this.ssid = ssid;
            this.password = password;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bind_device_wifi);

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

        etPassword = (EditText) findViewById(R.id.etPassword);
        etSSID = (TextView) findViewById(R.id.etWifi);

        type = getIntent().getIntExtra("type", 0);
        mySpinner = (Spinner) findViewById(R.id.spinner);
        list.add(getResources().getString(R.string.add_device_wifi_scan));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                String selectWifi = adapter.getItem(arg2);
                if (type == 1) {
                    if (aps != null && aps.size() > 0) {
                        //找到这个Wifi
                        for (ScanApCommand.Scan ap : aps) {
                            if (ap.ssid.equals(selectWifi)) {
                                selectAp = ap;
                                break;
                            }
                        }
                    }
                } else {
                    if (wifiScanList != null && wifiScanList.size() > 0) {
                        //找到这个Wifi
                        for (ScanResult scanResult : wifiScanList) {
                            if (scanResult.SSID.equals(selectWifi)) {
                                connectResult = scanResult;
                                break;
                            }
                        }
                    }
                }
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                etPassword.setText("");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        wifiAdmin = new WifiAdmin(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (type == 0) {
            // 隐藏wifi不可设置
            ((CheckBox) findViewById(R.id.chkHide)).setVisibility(View.GONE);
            // 获取xlight蓝牙信息
            Bundle bundle = getIntent().getBundleExtra("bundle");
            bluetoothInfo = new BluetoothInfo(bundle.getString("name"), bundle.getString("mac"));
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // 配对蓝牙设备
            bluetoothXlight = mBluetoothAdapter.getRemoteDevice(bluetoothInfo.address);
            Log.d("XLight", "open wifi");
            wifiAdmin.openWifi();
            Log.d("XLight", "start scan wifi");
//            wifiAdmin.startScan(mReceiver);
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

            mWifiReceiver = new WifiReceiver();
            registerReceiver(mWifiReceiver, filter);
            getWifiList();
            updateWifiHandler.sendEmptyMessageDelayed(1, 5000);
            ReceiverBluetooth();
        } else {
            apConnector = new ApConnector(this, null, null);
            stopScanWifi = true;
            getWifiList();
            initAPInfo();
        }
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
    }


    final Handler timeoutHandler = new Handler() {          // handle
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what) {
                case 1:
                    // 跳转到下一步
                    myThread = false;
                    Log.e("XLight", "Connect BLE timeout,Redirect to ErrorActivity");
                    intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    myThread = false;
                    ToastUtil.showToast(BindDeviceWiFiActivity.this, R.string.add_device_wifi_step7);
                    intent = new Intent(getApplication(), BindDeviceSuccessActivity.class);
                    intent.putExtra("coreID", coreID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private int connectTimeout = 60;
    private boolean myThread = true;

    public class MyThread implements Runnable {      // thread
        @Override
        public void run() {
            while (connectTimeout > 0) {
                try {
                    if (!myThread) {
                        break;
                    }
                    connectTimeout--;
                    if (connectTimeout > 1)
                        Thread.sleep(1000);     // sleep 1000ms
                } catch (Exception e) {
                }
            }
            // 超时
            if (connectTimeout == 0) {
                Message message = new Message();
                message.what = 1;
                timeoutHandler.sendMessage(message);
            }
        }
    }

    public class DelayThread implements Runnable {      // thread
        @Override
        public void run() {
            try {
                Thread.sleep(10000);
                new Thread(new CheckStateThread()).start();
            } catch (Exception e) {

            }
        }
    }

    public class CheckStateThread implements Runnable {      // thread
        @Override
        public void run() {
            while (connectTimeout > 0) {
                try {
                    if (!myThread) {
                        break;
                    }
                    RequestCheckDevice.getInstance().checkDevice(getApplicationContext(), coreID, new RequestCheckDevice.OnAddDeviceCallBack() {
                        @Override
                        public void mOnAddDeviceCallBackFail(int code, String errMsg) {
                        }

                        @Override
                        public void mOnAddDeviceCallBackSuccess(CheckDeviceResult device) {
                            //判断状态，并传递
                            if (device.id != null && device.connected) {
                                if (connectTimeout > 0) {
                                    connectTimeout = 60;
                                    Message message = new Message();
                                    message.what = 2;
                                    timeoutHandler.sendMessage(message);
                                }
                            }
                        }
                    });
                    if (connectTimeout > 1)
                        Thread.sleep(2000);     // sleep 1000ms
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            myThread = false;
            ToastUtil.dismissLoading();
            if (type == 0) {
                Log.d("XLight", "Destroy WiFiActivity");
                stopScanWifi = true;
                updateWifiHandler.removeCallbacks(runnable);
                unBindReceiver();
                //wifiAdmin.unRegisterReceiver(mReceiver);
                unregisterReceiver(mWifiReceiver);
                //取消蓝牙配对
                ClsUtils.removeBond(bluetoothXlight.getClass(), bluetoothXlight);
                if (_xltDevice != null)
                    _xltDevice.Disconnect();
            } else if (type == 1) {
                timer.cancel();
            }
        } catch (Exception ex) {
            Log.e("XLight", ex.getMessage(), ex);
        }

        ImmersionBar.with(this).destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void ChangeClick(View v) {
        if (((CheckBox) findViewById(R.id.chkHide)).isChecked()) {
            // 显示
            ((TextView) findViewById(R.id.etWifi)).setVisibility(View.VISIBLE);
            // 隐藏
            ((Spinner) findViewById(R.id.spinner)).setVisibility(View.GONE);
        } else {
            // 显示
            ((TextView) findViewById(R.id.etWifi)).setVisibility(View.GONE);
            // 隐藏
            ((Spinner) findViewById(R.id.spinner)).setVisibility(View.VISIBLE);
        }
    }

    public void ConnectClick(View v) {
        try {
            //验证输入的密码
            if (!etPassword.getText().toString().equals("") && etPassword.getText().toString().length() < 8) {
                ToastUtil.showToast(this, R.string.add_device_wifi_check);
                return;
            }
            if (type == 1) {
                //开始计时
                new Thread(new MyThread()).start();
                ConnectWLAN();
                return;
            }
            if (bluetoothXlight.getBondState() == BluetoothDevice.BOND_NONE) {
                //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                Log.d("XLight", "开始配对");
                new Thread(new MyThread()).start();
                ToastUtil.showLoading(this, null, getResources().getString(R.string.add_device_wifi_step1));
                ClsUtils.pair(bluetoothXlight.getAddress(), "1234");
            } else if (bluetoothXlight.getBondState() == BluetoothDevice.BOND_BONDED) {
                ClsUtils.removeBond(bluetoothXlight.getClass(), bluetoothXlight);
                ConnectClick(null);
            }
        } catch (Exception e) {

        }
    }

    private PublicKey publicKey;

    /********AP模式初始化开始*********/
    public void initAPInfo() {
        try {
            softApSSID = SSID.from(wifiAdmin.getWifiInfo());
            apConnector = new ApConnector(getApplicationContext(), null, null);
            WifiConfiguration config = ApConnector.buildUnsecuredConfig(softApSSID);
            apConnector.connectToAP(config, new ApConnector.Client() {
                @Override
                public void onApConnectionSuccessful(WifiConfiguration config) {
                    Log.e("XLight", "controller socket connect success");
                    new Thread(runnableInit).start();
                }

                @Override
                public void onApConnectionFailed(WifiConfiguration config) {
                    Log.d("XLight", config.toString());
                    //连接失败
                    Log.e("XLight", "controller socket connect failed");
                    ToastUtil.dismissLoading();
                }
            });
        } catch (Exception e) {
            Log.d("XLight", e.getMessage(), e);
        }
    }


    private ApConnector apConnector;
    private SSID softApSSID;
    private int retry = 0;

    private boolean isConnectedToSoftAp() {
        return softApSSID.equals(SSID.from(wifiAdmin.getWifiInfo()));
    }

    Runnable runnableInit = new Runnable() {
        @Override
        public void run() {
            try {
//                commandClient = new CommandClient("", 10, null);
//                //1、获取coreID
//                DeviceIdCommand.Response response = commandClient.sendCommand(
//                        new DeviceIdCommand(), DeviceIdCommand.Response.class);
//                coreID = response.deviceIdHex.toLowerCase(Locale.ROOT);
//                //2、获取密钥
//                PublicKeyCommand.Response response1 = commandClient.sendCommand(
//                        new PublicKeyCommand(), PublicKeyCommand.Response.class);
//                publicKey = Crypto.readPublicKeyFromHexEncodedDerString(response1.publicKey);
//                //3、check ownership
//                SetCommand.Response response2 = commandClient.sendCommand(
//                        new SetCommand("cc", "AYKZwDUEe8ZoFL+CoujbjRa/6h1h8kmbN3roGvnFpTW/5EThZThcQ4z7o7sVZKk"), SetCommand.Response.class);
//                if (truthy(response2.responseCode)) {
//                    // a non-zero response indicates an error, ala UNIX return codes
//                    Log.e("XLight", "Received non-zero return code from set command: "
//                            + response2.responseCode);
//                }
//                //4、搜索wifi信息
//                ScanApCommand.Response response3 = commandClient.sendCommand(new ScanApCommand(), ScanApCommand.Response.class);
//                aps = response3.getScans();
//                Message message = new Message();
//                message.what = 11;
//                updateWifiHandler.sendMessage(message);
            } catch (Exception e) {
                if (e.getMessage() == "timeout") {
                    retry++;
                } else {
                    retry = 3;
                }
                Message message = new Message();
                message.what = 2;
                updateWifiHandler.sendMessage(message);
                Log.e("XLight", e.getMessage(), e);
            }
        }
    };

    public void ConnectWLAN() {
        try {
            //获取wifi信息
            WifiInfo wifiInfo = getWifiInfo();
            final ScanApCommand.Scan configAp;
            String pwd = "";
            if (wifiInfo == null) {
                configAp = selectAp == null ? aps.get(0) : selectAp;
                pwd = etPassword.getText().toString().trim();
            } else {
                //隐藏的
                configAp = new ScanApCommand.Scan(wifiInfo.ssid, 4194308, 0);
                pwd = wifiInfo.password;
            }
            if (!pwd.equals("")) {
                pwd = Crypto.encryptAndEncodeToHex(pwd, publicKey);
            }
            WifiSecurity wifiSecurity = WifiSecurity.fromInteger(configAp.wifiSecurityType);
            ConfigureApCommand.Builder builder = ConfigureApCommand.newBuilder()
                    .setSsid(configAp.ssid)
                    .setSecurityType(wifiSecurity)
                    .setChannel(configAp.channel)
                    .setIdx(0);
            builder.setEncryptedPasswordHex(pwd);
            final ConfigureApCommand command = builder.build();
            if (isConnectedToSoftAp()) {
                ToastUtil.showLoading(this, null, getString(R.string.add_device_wifi_step2));
                Runnable settingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ConfigureApCommand.Response response = commandClient.sendCommand(command, ConfigureApCommand.Response.class);
                            if (response.isOk())
                                Log.d("XLight", "Ensuring connection to AP");
                            else
                                Log.e("XLight", "Ensuring connection to AP response code: " +
                                        response.responseCode);
                            ConnectAPCommand.Response response1 = commandClient.sendCommand(
                                    // FIXME: is hard-coding zero here correct?  If so, document why
                                    new ConnectAPCommand(0), ConnectAPCommand.Response.class);
                            if (!response1.isOK()) {
                                Log.e("XLight", "ConnectAPCommand returned non-zero response code: " +
                                        response.responseCode);
                            }
                            apConnector.stop();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getString(R.string.add_device_two_title2));
                                }
                            });
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    //每次需要执行的代码放到这里面。
                                    boolean result = ping();
                                    Log.d("XLight", "ping cloud server," + result);
                                    if (result) {
                                        timer.cancel();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getString(R.string.add_device_wifi_step6));
                                            }
                                        });
                                        if (isBind()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.dismissLoading();
                                                    ToastUtil.showToast(getBaseContext(), R.string.add_device_bulb_bind);
                                                }
                                            });
                                            Intent intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                                            intent.putExtra("type", type);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        } else {
                                            StartCheckDevice();
                                        }
                                    }
                                }
                            };
                            timer.schedule(task, 2000, 2000);
                        } catch (Exception e) {
                            Log.e("XLight", e.getMessage(), e);
                        }
                    }
                };
                new Thread(settingRunnable).start();
            } else {
                ToastUtil.dismissLoading();
                ToastUtil.showToast(this, R.string.add_device_wifi_disconnect);
                Intent intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            ToastUtil.dismissLoading();
            ToastUtil.showToast(this, R.string.add_device_wifi_disconnect);
            Log.e("XLight", e.getMessage(), e);
            Intent intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
            finish();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getString(R.string.add_device_wifi_step4));
            }
        });
    }

    /********AP模式初始化完成*********/

    boolean once = true;

    public void initXltDevice() {
        Log.d("XLight", "Init Bluetooth xltDevice");
        _xltDevice = new xltDevice();
        _xltDevice.setBridgePriority(xltDevice.BridgeType.BLE, 99);
        _xltDevice.useBridge(xltDevice.BridgeType.BLE);
        _xltDevice.Init(this);
        if (_xltDevice.isBLEOK()) {
            Log.d("XLight", "ble not need connect");
            clearWiFi();
        } else {
            Log.d("XLight", "ble connect start");
            _xltDevice.ConnectBLE(m_bcsHandler, new xltDevice.callbackConnect() {
                @Override
                public void onConnected(xltDevice.BridgeType bridge, boolean connected) {
                    Log.d("XLight", "connected callback");
                    if (once) {
                        once = false;
                        clearWiFi();
                    }
                }
            }, bluetoothXlight);
        }
    }

    public boolean isBind() {
        return RequestCheckDevice.getInstance().checkDeviceSync(coreID);
    }

    public void TestWiFi() {
        WifiInfo wifiInfo = getWifiInfo();
        wifiAdmin.openWifi();
        WifiConfiguration tempConfig = wifiAdmin.IsExsits(wifiInfo.ssid);
        if (tempConfig != null && tempConfig.networkId != -1) {
            Log.d("XLight", "remove ssid at:" + tempConfig.networkId);
            wifiAdmin.RemoveSSID(tempConfig.networkId);
        }
        int type = 1;
        if (wifiInfo.capabilities.toUpperCase().contains("WPA")) {
            type = 3;
        } else if (wifiInfo.capabilities.toUpperCase().contains("WEP")) {
            type = 2;
        }
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(wifiInfo.ssid, wifiInfo.password, type));
    }

    Handler m_bcsHandler = new Handler() {
        public void handleMessage(Message msg) {
            String bridgeName = (String) msg.obj;
            Log.d("XLight", "bridgeName->" + bridgeName);
            switch (msg.what) {
                case xltDevice.BCS_CONNECTED:
                    Log.d("XLight", "Bluetooth device connected");
                    break;
                case xltDevice.BCS_NOT_CONNECTED:
                case xltDevice.BCS_CONNECTION_FAILED:
                case xltDevice.BCS_CONNECTION_LOST:
                    break;
                case xltDevice.BCS_FUNCTION_ACK:
                    Log.d("XLight", msg.arg1 == 1 ? "OK" : "Failed");
                    if (step == 1) {
                        WifiInfo wifiInfo = getWifiInfo();
                        Log.d("XLight", String.format("设置当前新的Wi-Fi信息,%s,%s", wifiInfo.ssid, wifiInfo.password));
                        if (wifiInfo.capabilities.toUpperCase().contains("WPA2")) {
                            //做一点事
                            _xltDevice.sysWiFiSetup(wifiInfo.ssid, wifiInfo.password, xltDevice.WLAN_SEC_WPA2);
                        } else if (wifiInfo.capabilities.toUpperCase().contains("WPA")) {
                            //做一点事
                            _xltDevice.sysWiFiSetup(wifiInfo.ssid, wifiInfo.password, xltDevice.WLAN_SEC_WPA);
                        } else if (wifiInfo.capabilities.toUpperCase().contains("WEP")) {
                            //做一点事
                            _xltDevice.sysWiFiSetup(wifiInfo.ssid, wifiInfo.password, xltDevice.WLAN_SEC_WEP);
                        } else {
                            _xltDevice.sysWiFiSetup(wifiInfo.ssid, wifiInfo.password, xltDevice.WLAN_SEC_UNSEC);
                        }
                        ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getResources().getString(R.string.add_device_wifi_step4));
                        step++;
                    } else if (step == 2) {
                        //Log.d("XLight", "ControllerID:" + _xltDevice.sysQueryCoreID());
                        //保存设备CoreID
                        if (bridgeName.split(":").length == 3)
                            coreID = bridgeName.split(":")[2];
                        ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getResources().getString(R.string.add_device_wifi_step5));
                        Log.d("XLight", "重启控制器");
                        _xltDevice.sysControl("reset");
                        step++;
                    } else if (step == 3) {
                        Log.d("XLight", "响应->重启控制器");
                        //开始检测状态
                        ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getResources().getString(R.string.add_device_wifi_step6));
                        //先检测一次当前状态
                        StartCheckDevice();
                    }
                    break;
                case xltDevice.BCS_FUNCTION_COREID:
                    Log.d("XLight", "CoreID: " + bridgeName);
                    break;
            }
        }
    };

    public void StartCheckDevice() {
        RequestCheckDevice.getInstance().checkDevice(getApplicationContext(), coreID, new RequestCheckDevice.OnAddDeviceCallBack() {
            @Override
            public void mOnAddDeviceCallBackFail(int code, String errMsg) {
                //重试
                StartCheckDevice();
            }

            @Override
            public void mOnAddDeviceCallBackSuccess(CheckDeviceResult device) {
                // 判断是否启动延时
                if (device.id != null && device.connected) {
                    //重置超时时间，并启动延时状态检查
                    connectTimeout = 60;
                    new Thread(new DelayThread()).start();
                } else {
                    new Thread(new CheckStateThread()).start();
                }
            }
        });
    }

    /*********WiFi相关*********/
    public void clearWiFi() {
        Log.d("XLight", "蓝牙可用:" + _xltDevice.isBLEOK());
        ToastUtil.showLoading(this, null, getResources().getString(R.string.add_device_wifi_step3));
        _xltDevice.sysControl("clear credentials");
        Log.d("XLight", "清除当前Wi-Fi信息:" + _xltDevice.isBLEOK());
        step++;
    }

    public WifiInfo getWifiInfo() {
        if (((CheckBox) findViewById(R.id.chkHide)).isChecked()) {
            return new WifiInfo(etSSID.getText().toString(), etPassword.getText().toString());
        } else {
            if (type == 1)
                return null;
            if (connectResult == null) {
                connectResult = wifiList24.get(0);
            }
            return new WifiInfo(connectResult.SSID, etPassword.getText().toString(), connectResult.capabilities);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WIFI_PERMISSION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {// 允许
                    getWifiList();
                    updateWifiHandler.sendEmptyMessageDelayed(1, 5000);
                } else { // 不允许
                    ToastUtil.showToast(this, R.string.please_open_wifi);
                }
                break;
        }
    }

    private WifiManager wifiManager;

    /**
     * wifi列表
     */
    private void getWifiList() {
        if (!isWifiContect()) {
            wifiManager.setWifiEnabled(true);
        }
        Log.e("XLight", "start scan wifi");
        ToastUtil.showLoading(this, getString(R.string.add_device_wifi_scan));
        if (type == 0) {
            //获取结果
            wifiScanList = wifiManager.getScanResults();
            LinkedHashMap<String, ScanResult> linkedMap = new LinkedHashMap<>(wifiScanList.size());
            //处理结果
            for (ScanResult scanResult : wifiScanList) {
                try {
                    // 去除5g和名称为空的SSID
                    if (scanResult.SSID.equals("") || (scanResult.frequency > 4900 && scanResult.frequency < 5900))
                        continue;
                    if (linkedMap.containsKey(scanResult.SSID)) {
                        if (scanResult.level > linkedMap.get(scanResult.SSID).level) {
                            linkedMap.put(scanResult.SSID, scanResult);
                        }
                        continue;
                    }
                    linkedMap.put(scanResult.SSID, scanResult);
                } catch (Exception ex) {
                    Log.e("XLight", ex.getMessage());
                }
            }
            list.clear();
            list.addAll(linkedMap.keySet());
            wifiList24.clear();
            wifiList24.addAll(linkedMap.values());
            adapter.notifyDataSetChanged();
        }
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
            } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                Log.d("XLight", "Get WiFi Result:" + linkWifiResult);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    ToastUtil.showToast(getApplicationContext(), "密码错误");
                }
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
                Log.d("XLight", "网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.d("XLight", "wifi网络连接断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    Log.d("XLight", "连接到网络 " + wifiInfo.getSSID());
                }
            }
        }
    }

    /**
     * wifi是否打开
     *
     * @return
     */
    private boolean isWifiContect() {
        if (wifiManager.isWifiEnabled()) {
            return true;
        }
        return false;
    }

    private boolean stopScanWifi = false;

    Handler updateWifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11: {
                    if (aps != null & aps.size() > 0)
                        list.clear();
                    // 显示Wifi信息
                    for (ScanApCommand.Scan ap : aps)
                        list.add(ap.ssid);
                    adapter.notifyDataSetChanged();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.dismissLoading();
                        }
                    });
                }
                break;
                case 2: {
                    if (retry < 3) {
                        Log.d("XLight", "retry connect apconnect");
                        new Thread(runnableInit).start();
                    } else {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(getApplicationContext(), R.string.add_device_wifi_disconnect);
                        Intent intent = new Intent(getApplicationContext(), BindDeviceErrorActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
            }
//            if (!stopScanWifi) {
//                updateWifiHandler.postDelayed(runnable, 100);
//                updateWifiHandler.sendEmptyMessageDelayed(1, 5000);
//            }
        }
    };

    public boolean ping() {
        String result = null;
        try {
            String ip = NetConfig.SERVER_IP;// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    /**
     * 定时更新wifi列表
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getWifiList();
        }
    };

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // 该扫描已成功完成。
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.d("XLight", "get scan wifi result");
                //获取结果
                wifiScanList = wifiAdmin.getWifiList();
                LinkedHashMap<String, ScanResult> linkedMap = new LinkedHashMap<>(wifiScanList.size());
                //处理结果
                for (ScanResult scanResult : wifiScanList) {
                    try {
                        // 去除5g和名称为空的SSID
                        if (scanResult.SSID.equals("") || (scanResult.frequency > 4900 && scanResult.frequency < 5900))
                            continue;
                        if (linkedMap.containsKey(scanResult.SSID)) {
                            if (scanResult.level > linkedMap.get(scanResult.SSID).level) {
                                linkedMap.put(scanResult.SSID, scanResult);
                            }
                            continue;
                        }
                        linkedMap.put(scanResult.SSID, scanResult);
                    } catch (Exception ex) {
                        Log.e("XLight", ex.getMessage());
                    }
                }
                list.clear();
                list.addAll(linkedMap.keySet());
                wifiList24.clear();
                wifiList24.addAll(linkedMap.values());
                adapter.notifyDataSetChanged();
            }
        }
    };

    /*******蓝牙相关*********/

    public void ReceiverBluetooth() {
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(ACTION_PAIRING_REQUEST);
        filter.setPriority(1000);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBluetoothReceiver, filter);
    }

    public void unBindReceiver() {
        try {
            this.unregisterReceiver(mBluetoothReceiver);
        } catch (Exception e) {
            Log.e("XLight", e.getMessage());
        }
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("XLight", "mBluetoothReceiver action =" + action);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("XLight", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("XLight", "完成配对");
                        //进行下一步动作
                        initXltDevice();
                        ToastUtil.showLoading(BindDeviceWiFiActivity.this, null, getResources().getString(R.string.add_device_wifi_step2));
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("XLight", "取消配对，仅是取消对话框");
                    default:
                        break;
                }
            } else if (intent.getAction().equals(ACTION_PAIRING_REQUEST)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    abortBroadcast();
                    ClsUtils.setPin(device.getClass(), device, "1234"); // 手机和蓝牙采集器配对
                    //ClsUtils.createBond(device.getClass(), device);
                    //ClsUtils.cancelPairingUserInput(device.getClass(), device);
                    Log.d("XLight", "setPin is success ");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
}
