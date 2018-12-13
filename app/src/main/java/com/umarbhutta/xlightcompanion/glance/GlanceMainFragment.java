package com.umarbhutta.xlightcompanion.glance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.CloudAccount;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.SensorTool;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.bindDevice.BindDeviceConfirmActivity;
import com.umarbhutta.xlightcompanion.main.ControlDeviceActivity;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceState;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Light;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSensorInfo;
import com.umarbhutta.xlightcompanion.settings.utils.BaseFragment;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.particle.android.sdk.cloud.ParticleDevice;

/**
 */
public class GlanceMainFragment extends BaseFragment implements ImageView.OnClickListener {

    private static final String TAG = "XLight";
    /**
     * 设备列表
     */
    private LightItemAdapter adapterLight;
    private GridView gvLight;
    private ProgressDialog progressDialog;
    private Handler m_handlerGlance;
    private Handler m_deviceHandler;
    private Handler m_sparkHandler;
    public static List<Rows> deviceList = new ArrayList<Rows>();
    public static List<Devicenodes> devicenodes = new ArrayList<Devicenodes>();
    public boolean codeChange = false;
    WeatherDetails mWeatherDetails;

    TextView save_money;
    TextView default_text;
    TextView DHTt;
    TextView DHTh;
    TextView ALS;
    TextView PM25;
    TextView CO2;
    TextView CH2O;
    TextView TVOC;
    TextView txtSensor;
    TextView txtUnit;
    TextView txtCity;
    TextView txtWeather;
    TextView txtRefresh;
    TextView txtOutDHTt;
    ImageView imgDHTt;
    ImageView imgDHTh;
    ImageView imgALS;
    ImageView imgPM25;
    ImageView imgCH2O;
    ImageView imgCO2;
    ImageView imgTVOC;
    ImageView imgMenu;
    ImageView imgMessage;
    ImageView weatherIcon;
    RelativeLayout rlAdd;
    Sensorsdata sd;

    /**
     * 位置信息
     */
    public static String city = "";
    public static String country = "";
    public static double mLongitude = -80.5204;
    public static double mLatitude = 43.4643;
    // 高德定位
    private AMapLocationClient locationClient = null;

    protected ImmersionBar mImmersionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setNetEvent(new NetEvevt() {
            @Override
            public void onNetChange(int netMobile) {
                if (netMobile == 0) {
                    // 无网络
                    txtRefresh.setVisibility(View.VISIBLE);
                    try {
                        ToastUtil.showToast(getContext(), getString(R.string.net_error));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // 连接上网络
//                    txtRefresh.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glance, container, false);
        initComponents(view);
        initLocation();
        getBaseInfo();
        initHandler();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            initImmersionBar();
        }
    }

    // the meat of switching the above fragment
    private void switchFragment() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.toggle();
        }
    }

    private void onFabPressed() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.switchMessage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_message:
                onFabPressed();
                break;
            case R.id.home_menu:
                switchFragment();
                break;
            case R.id.imgALS:
                // txtUnit.setText(R.string.ALS);
                //resetBg();
                //imgALS.setImageDrawable(getResources().getDrawable(R.drawable.bg_ld));
                // txtSensor.setText(ALS.getText());
                break;
            case R.id.imgDHTt:
                // txtUnit.setText(R.string.DHTt);
                //resetBg();
                // imgDHTt.setImageDrawable(getResources().getDrawable(R.drawable.bg_wd));
                // txtSensor.setText(DHTt.getText());
                break;
            case R.id.imgDHTh:
                // txtUnit.setText(R.string.DHTh);
                //resetBg();
                // imgDHTh.setImageDrawable(getResources().getDrawable(R.drawable.bg_sd));
                // txtSensor.setText(DHTh.getText());
                break;
            case R.id.imgPM25:
                // txtUnit.setText(R.string.PM25);
                //resetBg();
                // imgPM25.setImageDrawable(getResources().getDrawable(R.drawable.bg_wm));
                // txtSensor.setText(PM25.getText());
                break;
            case R.id.imgCH2O:
                // txtUnit.setText(R.string.CH2O);
                // resetBg();
                // imgCH2O.setImageDrawable(getResources().getDrawable(R.drawable.bg_jq));
                // txtSensor.setText(CH2O.getText());
                break;
            case R.id.imgCO2:
                // txtUnit.setText(R.string.CO2);
                // resetBg();
                // imgCO2.setImageDrawable(getResources().getDrawable(R.drawable.bg_co2));
                // txtSensor.setText(CO2.getText());
                break;
            case R.id.imgTVOC:
                // txtUnit.setText(R.string.TVOC);
                // resetBg();
                // imgTVOC.setImageDrawable(getResources().getDrawable(R.drawable.bg_tovc));
                // txtSensor.setText(TVOC.getText());
                break;
            case R.id.rl_add:
                ActionSheet.createBuilder(getContext(), getFragmentManager())
                        .setCancelButtonTitle(getString(R.string.cancel))
                        .setOtherButtonTitles(getString(R.string.add_device_accesspoint))
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                Intent intent = new Intent(getContext(), BindDeviceConfirmActivity.class);
                                intent.putExtra("type", 1);
                                startActivityForResult(intent, 1);
                                actionSheet.dismiss();
                            }
                        }).show();
                break;
            case R.id.txtRefresh:
                // 重新加载
                getBaseInfo();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "main page start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "main page stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次Resume需要检测所有的连接对象是否完好
        for (xltDevice xltDevice : SlidingMenuMainActivity.xltDeviceMaps.values()) {
            // 判断每个连接对象
            if (xltDevice.getCurDevice().isConnected()) {
                Log.e(TAG, String.format("%s connection is %s", xltDevice.getControllerID(), xltDevice.getCurDevice().isConnected()));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2 && requestCode == 1) {
            Devicenodes ds = (Devicenodes) data.getSerializableExtra("devicenodes");
            for (Devicenodes d : devicenodes) {
                if (d.id == ds.id) {
                    d = ds;
                }
            }
            adapterLight.notifyDataSetChanged();
            Log.e(TAG, "back main=>" + ds.devicenodename);
        }
    }

    //设置接收消息的监听
    private void setHandlerMessage() {
        if (SlidingMenuMainActivity.m_mainDevice.getEnableEventSendMessage()) {
            updateUIHandler();
        } else {
            SlidingMenuMainActivity.m_mainDevice.setEnableEventSendMessage(true);
            updateUIHandler();
        }
    }

    private void setDeviceHandlerMessage(xltDevice xltDevice) {
        if (!xltDevice.getEnableEventSendMessage())
            xltDevice.setEnableEventSendMessage(true);
        //先清除
        xltDevice.clearDeviceEventHandlerList();
        xltDevice.clearSparkEventHandlerList();
        Log.e(TAG, "listen device status for =>" + xltDevice.getControllerID());
        xltDevice.addDeviceEventHandler(m_deviceHandler);
        xltDevice.addSparkEventHandler(m_sparkHandler);
    }

    private void updateUIHandler() {
        m_handlerGlance = new Handler(this.getContext().getMainLooper()) {
            public void handleMessage(Message msg) {
//                Log.e("XLight", "GlanceMainFragment_msg=" + msg.getData().toString());
                if (deviceList != null && deviceList.size() > 0 && msg.getData().getInt("nd", -1) == 130) {
                    int intValue = msg.getData().getInt("DHTt", -255);
                    if (intValue != -255) {
                        sd.DHTt = intValue;
                        DHTt.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("DHTh", -255);
                    if (intValue != -255) {
                        sd.DHTh = intValue;
                        DHTh.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("PM25", -255);
                    if (intValue != -255) {
                        sd.PM25 = intValue;
                        PM25.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("CH2O", -255);
                    if (intValue != -255) {
                        sd.CH2O = intValue;
                        CH2O.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("TVOC", -255);
                    if (intValue != -255) {
                        sd.TVOC = intValue;
                        TVOC.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("CO2", -255);
                    if (intValue != -255) {
                        sd.CO2 = intValue;
                        CO2.setText("" + intValue);
                    }
                    intValue = msg.getData().getInt("ALS", -255);
                    if (intValue != -255) {
                        sd.ALS = intValue;
                        ALS.setText("" + intValue);
                    }
                    intValue = SensorTool.getKPI(sd);
                    txtSensor.setText("" + intValue);
                    txtUnit.setText(getUnit(intValue));
                }
            }
        };
//        Log.e(TAG, "listen sensor status for =>" + SlidingMenuMainActivity.m_mainDevice.getControllerID());
        //先清除
        SlidingMenuMainActivity.m_mainDevice.clearDataEventHandlerList();
        SlidingMenuMainActivity.m_mainDevice.addDataEventHandler(m_handlerGlance);
    }

    public String getUnit(int value) {
        if (value > 80) {
            return getResources().getString(R.string.great);
        } else if (value > 60) {
            return getResources().getString(R.string.good);
        } else {
            return getResources().getString(R.string.bad);
        }
    }

    public void initHandler() {
        m_deviceHandler = new Handler(this.getContext().getMainLooper()) {
            public void handleMessage(Message msg) {
//                Log.e(TAG, "GlanceMainFragment_msg=" + msg.getData().toString());
                if (deviceList != null && deviceList.size() > 0) {
                    // 寻找设备
                    for (Devicenodes d : devicenodes) {
                        if (d.coreid.equals(msg.getData().getString("coreId")) && d.nodeno == msg.getData().getInt("nd", -1)) {
                            // 设置值
                            codeChange = true;
                            int intValue = msg.getData().getInt("State", -255);
                            if (intValue != -255) {
                                d.ison = intValue > 0 ? xltDevice.STATE_ON : xltDevice.STATE_OFF;
                            }
                            intValue = msg.getData().getInt("BR", -255);
                            if (intValue != -255) {
                                d.brightness = intValue;
                            }
                            intValue = msg.getData().getInt("CCT", -255);
                            if (intValue != -255) {
                                d.cct = intValue;
                            }
                            //颜色
                            int R = -255;
                            int G = -255;
                            int B = -255;
                            intValue = msg.getData().getInt("R", -255);
                            if (intValue != -255) {
                                R = intValue;
                            }
                            intValue = msg.getData().getInt("G", -255);
                            if (intValue != -255) {
                                G = intValue;
                            }
                            intValue = msg.getData().getInt("B", -255);
                            if (intValue != -255) {
                                B = intValue;
                            }
                            if (R != -255 && G != -255 && B != -255) {
                                int[] tmpColor = {R, G, B};
                                d.color = tmpColor;
//                                Log.e(TAG, "i change color:" + Arrays.toString(tmpColor));
                            }
//                            Log.d("XLight", "device event change");
                            codeChange = false;
                            if (adapterLight != null) {
                                adapterLight.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                }
            }
        };
        m_sparkHandler = new Handler(this.getContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                for (final Rows r : deviceList) {
                    if (msg.getData().getString("coreId").equals(r.coreid)) {
                        if (msg.getData().getString("data").equals("online")) {
                            addDeviceMapsSDK();
                            // 上线了，重新建立连接
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getContext(), String.format(getString(R.string.device_online), r.devicename));
                                }
                            });
                        } else {
                            // 离线了，需要将控制权移除，并提示
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(getContext(), String.format(getString(R.string.device_disconnected), r.devicename));
                                }
                            });
                        }
                    }
                }
            }
        };
    }

    public void getBaseInfo() {
        progressDialog = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
        progressDialog.show();
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            ToastUtil.showToast(getContext(), R.string.net_error);
            progressDialog.dismiss();
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
        }
        RequestFirstPageInfo.getInstance(getActivity()).getBaseInfo(new RequestFirstPageInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final DeviceInfoResult mDeviceInfoResult) {
                Log.e("XLight", "get first page data success");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        List<Rows> devices = mDeviceInfoResult.rows;
                        if (null != devices && devices.size() > 0) {
                            if (null != mDeviceInfoResult && null != mDeviceInfoResult.Energysaving) {
                                save_money.setText(getString(R.string.this_month_has_save_money_more) + "12 " + getString(R.string.this_month_has_save_money_more_two));
                            } else {
                                save_money.setText(getString(R.string.this_month_has_save_money_more) + "12 " + getString(R.string.this_month_has_save_money_more_two));
                            }
                        }
                        deviceList.clear();
                        deviceList.addAll(devices);
                        if (adapterLight != null) {
                            Log.d("XLight", "update device list at request after");
                            codeChange = true;
                            adapterLight.notifyDataSetChanged();
                            codeChange = false;
                        }
                        addDeviceMapsSDK();
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, String errMsg) {
                Log.d("XLight", "request first data error");
                final String err = errMsg;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //失败的处理
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtil.showToast(getContext(), err);
                    }
                });
            }
        });
    }

    private int initDeviceCount = 0;

    public void addDeviceMapsSDK() {
        if (deviceList != null && deviceList.size() == 0) {
            devicenodes.clear();
            default_text.setVisibility(View.VISIBLE);
            if (adapterLight != null) {
                adapterLight.notifyDataSetChanged();
            }
        } else if (null != deviceList && deviceList.size() > 0) {
            devicenodes.clear();
            initDeviceCount = 0;
//            Log.e("XLight", "device count:" + deviceList.size());
            default_text.setVisibility(View.GONE);
            SharedPreferencesUtils.putObject(getActivity(), SharedPreferencesUtils.KEY_DEVICE_LIST, deviceList);
//            if (SlidingMenuMainActivity.xltDeviceMaps != null) {
//                SlidingMenuMainActivity.xltDeviceMaps.clear();
//            }
            LoginResult lr = null;
            if (UserUtils.isLogin(getContext())) {
                lr = UserUtils.getUserInfo(getActivity());
            }
            for (int i = 0; i < deviceList.size(); i++) {
                // Initialize SmartDevice SDK
                final Rows device = deviceList.get(i);
                final xltDevice m_XltDevice = SlidingMenuMainActivity.xltDeviceMaps.get(device.coreid) != null ? SlidingMenuMainActivity.xltDeviceMaps.get(device.coreid) : new xltDevice();
                if (m_XltDevice != null && m_XltDevice.isCloudOK() && m_XltDevice.getCurDevice().isConnected()) {
                    //直接进行监听
                    if (device.maindevice == 1 && device.isShare == 0) {//主设备 TODO TODO  设置监听 广播回调
                        SlidingMenuMainActivity.m_mainDevice = m_XltDevice;
                        if (SlidingMenuMainActivity.m_mainDevice != null && getActivity() != null) {
                            //设置handler监听，获取数据室内温湿度
                            setHandlerMessage();
                        }
                    }
                    // 设置所有控制器的设备状态监听
                    setDeviceHandlerMessage(m_XltDevice);
                    if (device.devicenodes != null) {
                        for (int lv_idx = 0; lv_idx < device.devicenodes.size(); lv_idx++) {
                            m_XltDevice.addNodeToDeviceList(device.devicenodes.get(lv_idx).nodeno, xltDevice.DEFAULT_DEVICE_TYPE, device.devicenodes.get(lv_idx).devicenodename);
                            device.devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                            if (deviceList.get(i).sharedevice != null) {
                                device.devicenodes.get(lv_idx).isShare = 1;
                            }
                        }
                        devicenodes.addAll(device.devicenodes);
                    }
                    initDeviceCount++;
                    continue;
                } else {
                    if (lr != null)
                        m_XltDevice.Init(getActivity(), lr.username, lr.password);
                    else {
                        // 匿名登录
                        AnonymousParams ap = UserUtils.getAnonymousInfo(getContext());
                        m_XltDevice.Init(getActivity(), ap.uniqueId, ap.uniqueId);
                    }
                    if (device.devicenodes != null) {
                        for (int lv_idx = 0; lv_idx < device.devicenodes.size(); lv_idx++) {
                            m_XltDevice.addNodeToDeviceList(device.devicenodes.get(lv_idx).nodeno, xltDevice.DEFAULT_DEVICE_TYPE, device.devicenodes.get(lv_idx).devicenodename);
                            device.devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                            if (deviceList.get(i).sharedevice != null) {
                                device.devicenodes.get(lv_idx).isShare = 1;
                            }
                        }
                        devicenodes.addAll(device.devicenodes);
                    }
//                if (deviceList.get(i).sharedevice != null) {
//                    deviceList.get(i).isShare = 1;
//                }
                    if (!progressDialog.isShowing() && SlidingMenuMainActivity.xltDeviceMaps.size() == 0) {
                        progressDialog.show();
                    }
                    if (device.coreid != null) {
                        Log.e("XLight", "reconnect:" + device.coreid);
                        // Connect to Controller
                        m_XltDevice.Connect(device.coreid, new xltDevice.callbackConnect() {
                            @Override
                            public void onConnected(xltDevice.BridgeType bridge, boolean connected) {
                                Logger.e(TAG, String.format("coreID:%s,Bridge:%s,isControlConnect=%s", device.coreid, bridge, connected));
                                initDeviceCount++;
                                Log.e("XLight", "deviceSize:" + deviceList.size() + ",currentSize:" + initDeviceCount);
                                if (deviceList.size() == initDeviceCount && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                m_XltDevice.m_onConnected = null;
                                //判断是否连接成功
                                if (connected && bridge == xltDevice.BridgeType.Cloud) {
                                    SlidingMenuMainActivity.xltDeviceMaps.put(device.coreid, m_XltDevice);
                                    if (device.maindevice == 1 && device.isShare == 0) {//主设备 TODO TODO  设置监听 广播回调
                                        if (SlidingMenuMainActivity.m_mainDevice != null) {
                                            SlidingMenuMainActivity.m_mainDevice.Disconnect();
                                            SlidingMenuMainActivity.m_mainDevice = null;
                                        }
                                        SlidingMenuMainActivity.m_mainDevice = m_XltDevice;
                                        if (SlidingMenuMainActivity.m_mainDevice != null && getActivity() != null) {
                                            //设置handler监听，获取数据室内温湿度
                                            setHandlerMessage();
                                        }
                                    }
                                    // 设置所有控制器的设备状态监听
                                    setDeviceHandlerMessage(m_XltDevice);
                                    // 连接成功，直接查询下面所有子节点的状态
                                    for (Devicenodes node : devicenodes) {
                                        if (node.coreid.equals(device.coreid)) {
                                            m_XltDevice.QueryStatus(node.nodeno);
                                        }
                                    }
                                } else {
                                    // 提示连接失败信息
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToast(getContext(), String.format(getString(R.string.device_connect_failed), device.devicename));
                                            txtRefresh.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
            ArrayList<String> lstDevice = new ArrayList<String>();
            for (Rows r : deviceList) {
                lstDevice.add(r.coreid);
            }
            if (adapterLight == null) {
                changeGridView();
                adapterLight = new LightItemAdapter(getContext(), devicenodes, true);
                gvLight.setAdapter(adapterLight);
//                gvLight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        // 将此对象放到上面去
//                        Log.e("XLight", String.format("click %s -> id %s ", position, id));
//                        Devicenodes dn = devicenodes.get(position);
//                        SlidingMenuMainActivity.m_mainDevice = SlidingMenuMainActivity.xltDeviceMaps.get(dn.coreid);
//                        if (SlidingMenuMainActivity.m_mainDevice == null || !SlidingMenuMainActivity.m_mainDevice.isCloudOK() || !SlidingMenuMainActivity.m_mainDevice.getCurDevice().isConnected()) {
//                            ToastUtil.showToast(getContext(), R.string.device_disconnect);
//                            return;
//                        }
//                        // 点击事件 跳转到编辑设备页面
//                        Intent intent = new Intent(getActivity(), ControlDeviceActivity.class);
//                        intent.putExtra("info", dn);
//                        intent.putExtra("position", position);
//                        startActivityForResult(intent, 1);
//                        Log.e(TAG, position + "=>" + dn.toString());
//                    }
//                });
                // 直接控制事件
                adapterLight.setOnClickListener(new LightItemAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(LightItemAdapter.CLICK_TYPE type, int position) {
                        Devicenodes dn = devicenodes.get(position);
                        SlidingMenuMainActivity.m_mainDevice = SlidingMenuMainActivity.xltDeviceMaps.get(dn.coreid);
                        if (SlidingMenuMainActivity.m_mainDevice == null || !SlidingMenuMainActivity.m_mainDevice.getCurDevice().isConnected() || !SlidingMenuMainActivity.m_mainDevice.isCloudOK()) {
                            ToastUtil.showToast(getContext(), R.string.device_disconnect);
                            txtRefresh.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (type == LightItemAdapter.CLICK_TYPE.BOTTOM) {
                            dn.ison = dn.ison == 1 ? xltDevice.STATE_OFF : xltDevice.STATE_ON;
                            SlidingMenuMainActivity.m_mainDevice.PowerSwitch(dn.nodeno, dn.ison);
                            // 更新状态
                            adapterLight.notifyDataSetChanged();
                        } else {
                            // 点击事件 跳转到编辑设备页面
                            Intent intent = new Intent(getActivity(), ControlDeviceActivity.class);
                            intent.putExtra("info", dn);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, 1);
                            Log.e(TAG, position + "=>" + dn.toString());
                        }
                    }
                });
                codeChange = true;
                adapterLight.notifyDataSetChanged();
                codeChange = false;
                if (null != deviceList && deviceList.size() > 0) {
                    default_text.setVisibility(View.GONE);
                } else {
                    default_text.setVisibility(View.VISIBLE);
                }
            }
            getSensorAndStateInfo(lstDevice);
        }
    }


    public void getSensorAndStateInfo(final List<String> devices) {
        RequestSensorInfo.getInstance(getActivity()).getBaseInfo(devices, new RequestSensorInfo.OnRequestSensorInfoCallback() {
            @Override
            public void onRequestSensorInfoSuccess(final JSONObject mSensorsdata) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.e("XLight", mSensorsdata.toString());
                        final Gson gson = new Gson();
                        try {
                            // 获取各个设备的最新状态及传感器信息
                            for (String coreId : devices) {
                                if (!mSensorsdata.isNull(coreId)) {
                                    // 获取所有灯具的节点
                                    JSONObject device = mSensorsdata.getJSONObject(coreId);
                                    DeviceState ds = null;
                                    try {
                                        ds = gson.fromJson(device.toString(), DeviceState.class);
                                    } catch (Exception ex) {
                                        Log.e("XLight", device.toString());
                                        Log.e(TAG, ex.getMessage(), ex);
                                    }

                                    if (ds != null && ds.light != null && ds.light.size() > 0) {
                                        // List
                                        for (Light l : ds.light) {
                                            // 进行状态更新
                                            for (Devicenodes node : devicenodes) {
                                                if (node.coreid.equals(ds.coreid) && node.nodeno == l.nd) {
                                                    // 同一个灯，进行相应的状态赋值
                                                    node.ison = l.State;
                                                    node.brightness = l.BR;
                                                    node.cct = l.CCT;
                                                    node.filter = l.filter;
                                                    if (node.devicetype > 1) {
                                                        node.color = new int[]{l.R, l.G, l.B};
//                                                        Log.e(TAG, "R:" + l.R + "G" + l.G + "B" + l.B + "=>" + Arrays.toString(node.color));
                                                    }
//                                                    Log.e(TAG, node.coreid + "==" + ds.coreid + "，" + node.nodeno + "==" + l.nd + "，type=" + node.devicetype + "=>" + node.toString());
                                                    break;
                                                }
                                            }
                                        }
                                    }
//                                    ToastUtil.showToast(getContext(), "初始化状态完成" + devicenodes.get(0).ison);
                                    if (ds != null && ds.sensor != null && ds.sensor.size() > 0) {
                                        for (Sensorsdata s : ds.sensor) {
                                            sd = s;
                                            DHTh.setText("" + (int) s.DHTh);
                                            DHTt.setText("" + (int) s.DHTt);
                                            ALS.setText("" + s.ALS);
                                            PM25.setText("" + s.PM25);
                                            CO2.setText("" + s.CO2);
                                            CH2O.setText("" + (int) s.CH2O);
                                            TVOC.setText("" + (int) s.TVOC);
                                            int value = SensorTool.getKPI(s);
                                            txtSensor.setText("" + value);
                                            txtUnit.setText(getUnit(value));
                                        }
                                    }
                                }
                            }
                            if (adapterLight != null) {
                                adapterLight.notifyDataSetChanged();
                            }
                        } catch (Exception ex) {
                            Log.e("XLight", ex.getMessage(), ex);
                        }
                    }
                });
            }

            @Override
            public void onRequestSensorInfoFail(int code, String errMsg) {

            }
        });
    }

    /**
     * 将GridView改成单行横向布局
     */
    public void changeGridView() {
        // item宽度
//        int itemWidth = dip2px(95);
//        // item之间的间隔
//        int itemPaddingH = dip2px(15);
//        int size = devicenodes.size();
//        // 计算GridView宽度
//        int gvWidth = size * (itemWidth + itemPaddingH) + 80;
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, LinearLayout.LayoutParams.MATCH_PARENT);
//        gvLight.setLayoutParams(params);
//        gvLight.setColumnWidth(itemWidth);
//        gvLight.setHorizontalSpacing(itemPaddingH);
//        gvLight.setStretchMode(GridView.NO_STRETCH);
//        gvLight.setNumColumns(size);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp值
     * @return px值
     */
    public int dip2px(float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void initComponents(View view) {
        gvLight = (GridView) view.findViewById(R.id.gvLight);
        save_money = (TextView) view.findViewById(R.id.save_money);
        default_text = (TextView) view.findViewById(R.id.default_text);
        DHTt = (TextView) view.findViewById(R.id.txtDHTt);
        DHTh = (TextView) view.findViewById(R.id.txtDHTh);
        ALS = (TextView) view.findViewById(R.id.txtALS);
        PM25 = (TextView) view.findViewById(R.id.txtPM25);
        CO2 = (TextView) view.findViewById(R.id.txtCO2);
        CH2O = (TextView) view.findViewById(R.id.txtCH2O);
        TVOC = (TextView) view.findViewById(R.id.txtTVOC);
        txtSensor = (TextView) view.findViewById(R.id.txtSensor);
        txtUnit = (TextView) view.findViewById(R.id.txtUnit);
        txtCity = (TextView) view.findViewById(R.id.txtCity);
        txtWeather = (TextView) view.findViewById(R.id.txtWeather);
        txtRefresh = (TextView) view.findViewById(R.id.txtRefresh);
        txtOutDHTt = (TextView) view.findViewById(R.id.txtOutDHTt);

        imgMenu = (ImageView) view.findViewById(R.id.home_menu);
        weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
        imgMessage = (ImageView) view.findViewById(R.id.home_message);
        imgDHTt = (ImageView) view.findViewById(R.id.imgDHTt);
        imgDHTh = (ImageView) view.findViewById(R.id.imgDHTh);
        imgALS = (ImageView) view.findViewById(R.id.imgALS);
        imgCH2O = (ImageView) view.findViewById(R.id.imgCH2O);
        imgCO2 = (ImageView) view.findViewById(R.id.imgCO2);
        imgPM25 = (ImageView) view.findViewById(R.id.imgPM25);
        imgTVOC = (ImageView) view.findViewById(R.id.imgTVOC);
        rlAdd = (RelativeLayout) view.findViewById(R.id.rl_add);
        imgMenu.setOnClickListener(this);
        imgMessage.setOnClickListener(this);
        imgDHTt.setOnClickListener(this);
        imgDHTh.setOnClickListener(this);
        imgALS.setOnClickListener(this);
        imgCH2O.setOnClickListener(this);
        imgCO2.setOnClickListener(this);
        imgPM25.setOnClickListener(this);
        imgTVOC.setOnClickListener(this);
        rlAdd.setOnClickListener(this);
        txtRefresh.setOnClickListener(this);
    }

    public void resetBg() {
        imgALS.setImageDrawable(getResources().getDrawable(R.drawable.bgt__ld));
        imgDHTh.setImageDrawable(getResources().getDrawable(R.drawable.bgt__sd));
        imgDHTt.setImageDrawable(getResources().getDrawable(R.drawable.bgt__wd));
        imgPM25.setImageDrawable(getResources().getDrawable(R.drawable.bgt__wm));
        imgCO2.setImageDrawable(getResources().getDrawable(R.drawable.bgt__co2));
        imgCH2O.setImageDrawable(getResources().getDrawable(R.drawable.bgt__jq));
        imgTVOC.setImageDrawable(getResources().getDrawable(R.drawable.bgt_tovc));
    }

    public void initLocation() {
        locationClient = new AMapLocationClient(this.getActivity());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        AMapLocation location = locationClient.getLastKnownLocation();
        if (location != null) {
            Log.e(TAG, "getLastKnownLocation success");
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            city = location.getCity();
            country = location.getCountry();
            Log.e("XLight", String.format("long:%s,latitude:%s,ciry:%s,country:%s", mLongitude, mLatitude, city, country));
            //请求天气信息
            updateLocationInfo();
        } else {
            // 启动定位
            locationClient.startLocation();
        }
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc && loc.getErrorCode() == 0) {
                //解析定位结果
                mLongitude = loc.getLongitude();
                mLatitude = loc.getLatitude();
                city = loc.getCity();
                country = loc.getCountry();
                Log.i("XLight", String.format("long:%s,latitude:%s,city:%s,country:%s", mLongitude, mLatitude, city, country));
                //请求天气信息
                updateLocationInfo();
                locationClient.stopLocation();
            } else {
                // 定位失败，显示提示
                Log.e("XLight", "location Error, ErrCode:"
                        + loc.getErrorCode() + ", errInfo:"
                        + loc.getErrorInfo());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), R.string.open_gps);
                    }
                });
            }
        }
    };

    private void updateLocationInfo() {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(city)) {
                        txtCity.setText(city);
                    } else {
                        if (country == null) {
                            country = getString(R.string.share_list_unknown);
                        }
                        txtCity.setText("" + country);
                    }
                    getWeather();
                }
            });
    }

    /**
     * 获取title信息
     */
    private void getWeather() {

        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
        }

        String forecastUrl = "https://api.forecast.io/forecast/" + CloudAccount.DarkSky_apiKey + "/" + mLatitude + "," + mLongitude + "?" + (isZh() ? "lang=zh" : "lang=en");
        Log.e(TAG, forecastUrl);
        OkHttpClient client = new OkHttpClient();
        //build request
        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        //put request in call object to use for returning data
        Call call = client.newCall(request);
        Log.i("XLight", String.format("request weahter info->%s,%s", mLatitude, mLongitude));
        //make async call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    // 重新获取
                    getWeather();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.i("XLight", "request weahter success->" + jsonData);
                    if (response.isSuccessful()) {
                        mWeatherDetails = getWeatherDetails(jsonData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getActivity(), "There was an error retrieving weather data.");
                            }
                        });
                    }
                } catch (IOException | JSONException | NullPointerException e) {
                    Logger.i("Exception caught: " + e);
                }
            }
        });
    }

    private WeatherDetails getWeatherDetails(String jsonData) throws JSONException {
//        currently
//        {
//                time: 1535523409,
//                summary: "晴朗",
//                icon: "clear-day",
//                precipIntensity: 0,
//                precipProbability: 0,
//                temperature: 89.98,
//                apparentTemperature: 89.98,
//                dewPoint: 58.83,
//                humidity: 0.35,
//                pressure: 1008.72,
//                windSpeed: 5.31,
//                windGust: 9.96,
//                windBearing: 12,
//                cloudCover: 0,
//                uvIndex: 6,
//                visibility: 8.43,
//                ozone: 282.53
//        }
        WeatherDetails weatherDetails = new WeatherDetails();
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");
        weatherDetails.setIcon(currently.getString("icon"));
        weatherDetails.setSummary(currently.getString("summary"));
        weatherDetails.setTemp(currently.getDouble("apparentTemperature"));
        return weatherDetails;
    }

    private void updateDisplay() {
        weatherIcon.setImageDrawable(getWeatherIcon(mWeatherDetails.getIcon()));
        txtWeather.setText(mWeatherDetails.getSummary());
        txtOutDHTt.setText("" + mWeatherDetails.getTemp(""));
    }


    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setMockEnable(true);
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(10000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }


    public boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public Drawable getWeatherIcon(String summary) {
        if (summary.equals("clear-day") || summary.equals("clear-night")) {
            return getResources().getDrawable(R.drawable.qt);
        } else if (summary.equals("rain")) {
            return getResources().getDrawable(R.drawable.xy);
        } else if (summary.equals("snow")) {
            return getResources().getDrawable(R.drawable.xx);
        } else if (summary.equals("sleet")) {
            return getResources().getDrawable(R.drawable.yjx);
        } else if (summary.equals("cloudy")) {
            return getResources().getDrawable(R.drawable.dytq);
        } else if (summary.indexOf("cloudy") > -1) {
            return getResources().getDrawable(R.drawable.dy);
        } else if (summary.equals("wind")) {
            return getResources().getDrawable(R.drawable.wind);
        } else if (summary.equals("fog")) {
            return getResources().getDrawable(R.drawable.fog);
        } else {
            return getResources().getDrawable(R.drawable.dy);
        }
    }

    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(getActivity(), this);
        mImmersionBar.titleBar(R.id.ll_title).statusBarDarkFont(true).init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
        for (xltDevice _xltDevice : SlidingMenuMainActivity.xltDeviceMaps.values()) {
            _xltDevice.clearDataEventHandlerList();
            _xltDevice.clearDeviceEventHandlerList();
            _xltDevice.clearSparkEventHandlerList();
            _xltDevice.Disconnect();
            _xltDevice = null;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mImmersionBar != null)
            mImmersionBar.init();
    }
}