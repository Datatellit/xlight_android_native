package com.umarbhutta.xlightcompanion.glance;

import android.Manifest;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.bugly.crashreport.CrashReport;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.Cloud.ParticleAdapter;
import com.umarbhutta.xlightcompanion.SDK.CloudAccount;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.NoFastClickUtils;
import com.umarbhutta.xlightcompanion.Tools.SensorTool;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.adapter.LightItemAdapter;
import com.umarbhutta.xlightcompanion.bindDevice.BindDeviceConfirmActivity;
import com.umarbhutta.xlightcompanion.main.ControlDeviceActivity;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceState;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Light;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSceneListInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSensorInfo;
import com.umarbhutta.xlightcompanion.scenario.ScenarioListAdapter;
import com.umarbhutta.xlightcompanion.scenario.ScenarioMainFragment;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.BaseFragment;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

/**
 */
public class GlanceMainFragment extends BaseFragment implements ImageView.OnClickListener {

    private static final String TAG = "XLight";
    /**
     * 设备列表
     */
    private com.umarbhutta.xlightcompanion.adapter.LightItemAdapter adapterLight;
    private GridView gvLight;
    private GridView gvScene;

    public List<SceneResult> mSceneList = new ArrayList<SceneResult>();
    ScenarioListAdapter sceneListAdapter;
    private Handler m_deviceHandler;
    private Handler m_sparkHandler;
    public static List<Rows> deviceList = new ArrayList<Rows>();
    public static List<Devicenodes> devicenodes = new ArrayList<Devicenodes>();
    public boolean codeChange = false;
    ProgressDialog m_dialog;
    WeatherDetails mWeatherDetails;

    LinearLayout llNoDevices;
    LinearLayout llScene;
    HorizontalScrollView hsvSceneList;
    TextView txtCity;
    LinearLayout llGps;
    TextView txtWeather;
    LinearLayout txtRefresh;
    TextView txtOutDHTt;
    TextView txtMin;
    TextView txtMax;
    TextView txtSummary;
    TextView txtHumidity;
    ImageView imgMenu;
    ImageView weatherIcon;
    RelativeLayout rlAdd;

    /**
     * 位置信息
     */
    protected ImmersionBar mImmersionBar;
    private boolean isShowError = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParticleDeviceSetupLibrary.init(getActivity());
        this.setNetEvent(new NetEvevt() {
            @Override
            public void onNetChange(int netMobile) {
                if (netMobile == 0) {
                    // 无网络
                    txtRefresh.setVisibility(View.VISIBLE);
                    llGps.setVisibility(View.GONE);
                    try {
                        if (isShowError) {
                            ToastUtil.showToast(getContext(), getString(R.string.net_error));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // 连接上网络
                    if (txtRefresh != null) {
                        // txtRefresh.setVisibility(View.GONE);
                    }
                }
            }
        });
        m_dialog = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glance, container, false);
        initComponents(view);
        this.selfPermissionGranted(getContext(), new BaseActivity.PermissionCallback() {
            @Override
            public void hasPermission() {
                getLocation(getContext());
            }

            @Override
            public void noPermission() {
                ToastUtil.showToast(getContext(), R.string.defaule_first_message);
                getLocation(getContext());
            }
        }, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            initImmersionBar();
        }
        getBaseInfo();
        initHandler();
    }

    // the meat of switching the above fragment
    private void switchFragment(android.support.v4.app.Fragment activity) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.switchContent(activity, "scene");
        }
    }

    public void switchFragment(Class activity) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.onActivityPressed(activity);
        }
    }


    private void switchFragment() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.toggle();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_scene_add:
                if (UserUtils.isLogin(getContext())) {
                    switchFragment(new ScenarioMainFragment());
                } else {
                    // 跳转到登录页
                    switchFragment(LoginActivity.class);
                }
                break;
            case R.id.home_menu:
                switchFragment();
                break;
            case R.id.fly_rl_add:
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
//                                Intent intent = new Intent(getContext(), BindDeviceConfirmActivity.class);
//                                intent.putExtra("type", 1);
//                                startActivityForResult(intent, 1);
                                isShowError = false;
                                if (!ParticleAdapter.isAuthenticated()) {
                                    // 尝试进行登录操作
                                    if (UserUtils.isLogin(getContext())) {
                                        LoginResult lr = UserUtils.getUserInfo(getActivity());
                                        if (lr == null) {
                                            AnonymousParams ap = UserUtils.getAnonymousInfo(getContext());
                                            ParticleAdapter.authenticate(ap.uniqueId, ap.uniqueId);
                                        } else {
                                            ParticleAdapter.authenticate(lr.username, lr.password);
                                        }
                                    } else {
                                        AnonymousParams ap = UserUtils.getAnonymousInfo(getContext());
                                        ParticleAdapter.authenticate(ap.uniqueId, ap.uniqueId);
                                    }
                                }
                                ParticleDeviceSetupLibrary.startDeviceSetup(getActivity(), SlidingMenuMainActivity.class);
                                actionSheet.dismiss();
                            }
                        }).show();
                break;
            case R.id.txtRefresh:
                // 重新加载
                getBaseInfo();
                getLocation(getContext());
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
        isShowError = true;
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
                    d.devicenodename = ds.devicenodename;
                    d.ison = ds.ison;
                    d.cct = ds.cct;
                    d.brightness = ds.brightness;
                    d.color = ds.color;
                    d.filter = ds.filter;
                    Log.e("XLight", "id equals " + d.id);
                }
            }
            adapterLight.notifyDataSetChanged();
            Log.e(TAG, "back main=>" + ds.devicenodename);
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

    public void initHandler() {
        m_deviceHandler = new Handler(this.getContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                Log.e(TAG, "GlanceMainFragment_msg=" + msg.getData().toString());
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

    private void getSceneList() {
        RequestSceneListInfo.getInstance().getSceneListInfo(getActivity(), new RequestSceneListInfo.OnRequestSceneInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final List<SceneResult> sceneInfoResult) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != sceneInfoResult && sceneInfoResult.size() > 0) {
                            mSceneList.clear();
                            llScene.setVisibility(View.GONE);
                            hsvSceneList.setVisibility(View.VISIBLE);
                            mSceneList.addAll(sceneInfoResult);
                            sceneListAdapter = new ScenarioListAdapter(getContext(), mSceneList, true);
                            sceneListAdapter.setOnClickCallBack(new ScenarioListAdapter.OnClickCallBack() {
                                @Override
                                public void onClickCallBack(int position) {
                                    if (NoFastClickUtils.isFastClick()) {
                                        return;
                                    }
                                    for (SceneResult s : mSceneList) {
                                        s.checked = false;
                                    }
                                    mSceneList.get(position).checked = true;
                                    resolveScene(mSceneList.get(position));
                                    sceneListAdapter.notifyDataSetChanged();
                                }
                            });
                            gvScene.setAdapter(sceneListAdapter);
                            changeGridView(gvScene, mSceneList.size());
                        }
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, final String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "" + errMsg);
                    }
                });
            }
        });
    }

    public void resolveScene(final SceneResult scene) {
        try {
            HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_CHANGE_SCENE, scene.id, UserUtils.getAccessToken(getContext())), "", null, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_success), scene.name));
                        }
                    });
                }

                @Override
                public void onHttpRequestFail(int code, String errMsg) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_failed), scene.name));
                        }
                    });
                }
            });
        } catch (Exception e) {
            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_failed), scene.name));
        }
    }

    public void getBaseInfo() {
        if (!m_dialog.isShowing())
            m_dialog.show();
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            llGps.setVisibility(View.GONE);
            ToastUtil.showToast(getContext(), R.string.net_error);
            if (m_dialog != null && m_dialog.isShowing()) {
                m_dialog.dismiss();
            }
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
            llGps.setVisibility(View.VISIBLE);
        }
        try {
            getSceneList();
            RequestFirstPageInfo.getInstance(getActivity()).getBaseInfo(new RequestFirstPageInfo.OnRequestFirstPageInfoCallback() {
                @Override
                public void onRequestFirstPageInfoSuccess(final DeviceInfoResult mDeviceInfoResult) {
                    Log.e("XLight", "get first page data success");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (m_dialog != null && m_dialog.isShowing()) {
                                m_dialog.dismiss();
                            }
                            List<Rows> devices = mDeviceInfoResult.rows;
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
                            if (m_dialog != null && m_dialog.isShowing()) {
                                m_dialog.dismiss();
                            }
                            ToastUtil.showToast(getContext(), err);
                        }
                    });
                }
            });
        } catch (Exception ex) {

        }
    }

    private int initDeviceCount = 0;

    public void addDeviceMapsSDK() {
        if (deviceList != null && deviceList.size() == 0) {
            devicenodes.clear();
            llNoDevices.setVisibility(View.VISIBLE);
            if (adapterLight != null) {
                adapterLight.notifyDataSetChanged();
            }
        } else if (null != deviceList && deviceList.size() > 0) {
            devicenodes.clear();
            initDeviceCount = 0;
//            Log.e("XLight", "device count:" + deviceList.size());
            llNoDevices.setVisibility(View.GONE);
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
                    if (m_dialog != null && !m_dialog.isShowing()) {
                        m_dialog.show();
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
                                if (deviceList.size() == initDeviceCount) {
                                    if (m_dialog != null && m_dialog.isShowing()) {
                                        m_dialog.dismiss();
                                    }
                                }
                                m_XltDevice.m_onConnected = null;
                                //判断是否连接成功
                                if (connected && bridge == xltDevice.BridgeType.Cloud) {
                                    SlidingMenuMainActivity.xltDeviceMaps.put(device.coreid, m_XltDevice);
                                    if (device.maindevice == 1 && device.isShare == 0) {//主设备 TODO TODO  设置监听 广播回调
//                                        if (SlidingMenuMainActivity.m_mainDevice != null) {
//                                            SlidingMenuMainActivity.m_mainDevice.Disconnect();
//                                            SlidingMenuMainActivity.m_mainDevice = null;
//                                        }
                                        SlidingMenuMainActivity.m_mainDevice = m_XltDevice;
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
                                            llGps.setVisibility(View.GONE);
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
                adapterLight = new com.umarbhutta.xlightcompanion.adapter.LightItemAdapter(getContext(), devicenodes);
                gvLight.setAdapter(adapterLight);
                // 直接控制事件
                adapterLight.setOnClickListener(new com.umarbhutta.xlightcompanion.adapter.LightItemAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(com.umarbhutta.xlightcompanion.adapter.LightItemAdapter.CLICK_TYPE type, int position) {
                        Devicenodes dn = devicenodes.get(position);
                        SlidingMenuMainActivity.m_mainDevice = SlidingMenuMainActivity.xltDeviceMaps.get(dn.coreid);
                        if (SlidingMenuMainActivity.m_mainDevice == null || !SlidingMenuMainActivity.m_mainDevice.isCloudOK()) {
                            ToastUtil.showToast(getContext(), R.string.device_disconnect);
                            txtRefresh.setVisibility(View.VISIBLE);
                            llGps.setVisibility(View.GONE);
                            return;
                        }
                        if (type == LightItemAdapter.CLICK_TYPE.SWITCH) {
                            dn.ison = dn.ison == 1 ? xltDevice.STATE_OFF : xltDevice.STATE_ON;
                            SlidingMenuMainActivity.m_mainDevice.PowerSwitch(dn.nodeno, dn.ison);
                            // 更新状态
                            adapterLight.notifyDataSetChanged();
                        } else if (type == LightItemAdapter.CLICK_TYPE.LOW) {
                            dn.brightness = 30;
                            SlidingMenuMainActivity.m_mainDevice.ChangeBrightness(dn.nodeno, dn.brightness);
                        } else if (type == LightItemAdapter.CLICK_TYPE.NORMAL) {
                            dn.brightness = 50;
                            SlidingMenuMainActivity.m_mainDevice.ChangeBrightness(dn.nodeno, dn.brightness);
                        } else if (type == LightItemAdapter.CLICK_TYPE.HIGH) {
                            dn.brightness = 90;
                            SlidingMenuMainActivity.m_mainDevice.ChangeBrightness(dn.nodeno, dn.brightness);
                        } else if (type == LightItemAdapter.CLICK_TYPE.COOL) {
                            dn.cct = 6500;
                            SlidingMenuMainActivity.m_mainDevice.ChangeCCT(dn.nodeno, dn.cct);
                        } else if (type == LightItemAdapter.CLICK_TYPE.WARM) {
                            dn.cct = 2700;
                            SlidingMenuMainActivity.m_mainDevice.ChangeCCT(dn.nodeno, dn.cct);
                        } else if (type == LightItemAdapter.CLICK_TYPE.MORE) {
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
                    llNoDevices.setVisibility(View.GONE);
                } else {
                    llNoDevices.setVisibility(View.VISIBLE);
                }
            }
            // getSensorAndStateInfo(lstDevice);
        }
    }

    public void initComponents(View view) {
        gvLight = (GridView) view.findViewById(R.id.gvLight);
        gvScene = (GridView) view.findViewById(R.id.gvScenario);
        llNoDevices = (LinearLayout) view.findViewById(R.id.llNoDevices);
        hsvSceneList = (HorizontalScrollView) view.findViewById(R.id.hsvSceneList);
        llScene = (LinearLayout) view.findViewById(R.id.llScene);
        txtCity = (TextView) view.findViewById(R.id.txtCity);
        llGps = (LinearLayout) view.findViewById(R.id.llGps);
        txtWeather = (TextView) view.findViewById(R.id.txtWeather);
        txtRefresh = (LinearLayout) view.findViewById(R.id.txtRefresh);
        txtOutDHTt = (TextView) view.findViewById(R.id.txtOutDHTt);
        txtMin = (TextView) view.findViewById(R.id.txtMin);
        txtMax = (TextView) view.findViewById(R.id.txtMax);
        txtSummary = (TextView) view.findViewById(R.id.txtSummary);
        txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
        imgMenu = (ImageView) view.findViewById(R.id.home_menu);
        weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
        rlAdd = (RelativeLayout) view.findViewById(R.id.rl_add);
        imgMenu.setOnClickListener(this);


        ((LinearLayout) view.findViewById(R.id.fly_rl_add)).setOnClickListener(this);
        ((RelativeLayout) view.findViewById(R.id.rl_scene_add)).setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((LinearLayout) view.findViewById(R.id.root)).setBackgroundColor(getResources().getColor(R.color.white));
        }
        rlAdd.setOnClickListener(this);
        txtRefresh.setOnClickListener(this);
    }

    /**
     * 判断GPS是否开启,GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位,定位级别可以精确到街(通过24颗卫星定位,在室外和空旷的地方定位准确、速度快)
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置(也称作AGPS,辅助GPS定位。主要用于在室内或遮盖物(建筑群或茂密的深林等)密集的地方定位)
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 强制打开GPS
     *
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private LocationManager locationManager;
    private String locationProvider;

    private void getLocation(Context context) {
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            return;
        }
        //3.获取上次的位置，一般第一次运行，此值为null
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            // 获取城市
            updateLocationInfo(location);
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 2000, 0, mListener);
        }
    }

    public void getCity(Location location) {
        List<Address> addList = null;
        Geocoder ge = new Geocoder(getContext());
        try {
            addList = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);
                txtCity.setText(ad.getLocality());
                Log.e("XLight", String.format("long:%s,latitude:%s,city:%s", location.getLongitude(), location.getLatitude(), ad.getLocality()));
            }
        } else {
            getCityByAmapAPI(location);
        }
//        if (TextUtils.isEmpty(txtCity.getText().toString())) {
//            txtCity.setText(getString(R.string.share_list_unknown));
//        }
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            updateLocationInfo(location);
            locationManager.removeUpdates(mListener);
        }
    };

    private void updateLocationInfo(Location location) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getCity(location);
                    getWeather(location);
                }
            });
    }

    int requestTotal = 0;

    /**
     * 获取title信息
     */
    private void getWeather(Location location) {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            txtCity.setVisibility(View.GONE);
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
            txtCity.setVisibility(View.VISIBLE);
        }

        String forecastUrl = NetConfig.WEATHER_API + NetConfig.DarkSKY_Key + "/" + location.getLatitude() + "," + location.getLongitude() + "?" + (isZh() ? "lang=zh" : "lang=en");
        Log.e(TAG, forecastUrl);
        OkHttpClient client = new OkHttpClient();
        //build request
        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        //put request in call object to use for returning data
        Call call = client.newCall(request);
        Log.i("XLight", String.format("request weahter info->%s,%s", location.getLatitude(), location.getLongitude()));
        //make async call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    // 重新获取
                    if (requestTotal < 3) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWeather(location);
                            }
                        });
                    }
                    requestTotal++;
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

    private void getCityByGoogleAPI(Location location) {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            txtCity.setVisibility(View.GONE);
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
            txtCity.setVisibility(View.VISIBLE);
        }

        String getCity = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&sensor=false&key=" + NetConfig.GOOGLE_KEY;
        Log.e(TAG, getCity);
        OkHttpClient client = new OkHttpClient();
        //build request
        Request request = new Request.Builder()
                .url(getCity)
                .build();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        //put request in call object to use for returning data
        Call call = client.newCall(request);
        Log.i("XLight", String.format("request city info->%s,%s", location.getLatitude(), location.getLongitude()));
        //make async call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    // 重新获取
                    if (requestTotal < 3) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCity(location);
                            }
                        });
                    }
                    requestTotal++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.i("XLight", "request city success->" + jsonData);
                    if (response.isSuccessful()) {
                        mWeatherDetails = getWeatherDetails(jsonData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                } catch (IOException | JSONException | NullPointerException e) {
                    Logger.i("Exception caught: " + e);
                }
            }
        });
    }

    private void getCityByAmapAPI(Location location) {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            txtCity.setVisibility(View.GONE);
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
            txtCity.setVisibility(View.VISIBLE);
        }

        String getCity = NetConfig.AMAP_API + "?location=" + location.getLongitude() + "," + location.getLatitude() + "&output=json&key=" + NetConfig.AMAP_KEY;
        Log.e(TAG, getCity);
        OkHttpClient client = new OkHttpClient();
        //build request
        Request request = new Request.Builder()
                .url(getCity)
                .build();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        //put request in call object to use for returning data
        Call call = client.newCall(request);
        Log.i("XLight", String.format("amap request city info->%s,%s", location.getLatitude(), location.getLongitude()));
        //make async call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    // 重新获取
                    if (requestTotal < 3) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCityByAmapAPI(location);
                            }
                        });
                    }
                    requestTotal++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.i("XLight", "amap request city success->" + jsonData);
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jb = new JSONObject(jsonData);
                                    if (jb.getInt("status") == 1 && !jb.isNull("regeocode")) {
                                        // 获取，如果是国外调用另一个
                                        JSONObject address = jb.getJSONObject("regeocode").getJSONObject("addressComponent");
                                        if (address.getString("country").equals("中国")) {
                                            // 开始赋值
                                            if (address.getString("city").equals("[]")) {
                                                txtCity.setText(address.getString("province"));
                                            } else if (!address.isNull("city")) {
                                                txtCity.setText(address.getString("city"));
                                            } else {
                                                txtCity.setText(getString(R.string.share_list_unknown));
                                            }
                                        } else {
                                            getCityByLocationiqAPI(location);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (requestTotal < 3) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getCityByAmapAPI(location);
                                        }
                                    });
                                }
                                requestTotal++;
                            }
                        });
                    }
                } catch (Exception e) {
                    Logger.i("Exception caught: " + e);
                }
            }
        });
    }

    private void getCityByLocationiqAPI(Location location) {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            txtRefresh.setVisibility(View.VISIBLE);
            txtCity.setVisibility(View.GONE);
            return;
        } else {
            txtRefresh.setVisibility(View.GONE);
            txtCity.setVisibility(View.VISIBLE);
        }

        String getCity = NetConfig.Locationiq_API + "?key=" + NetConfig.Locationiq_KEY + "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&format=json&zoom=10";
        Log.e(TAG, getCity);
        OkHttpClient client = new OkHttpClient();
        //build request
        Request request = new Request.Builder()
                .url(getCity)
                .build();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        //put request in call object to use for returning data
        Call call = client.newCall(request);
        Log.i("XLight", String.format("locationiq request city info->%s,%s", location.getLatitude(), location.getLongitude()));
        //make async call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    // 重新获取
                    if (requestTotal < 3) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCityByLocationiqAPI(location);
                            }
                        });
                    }
                    requestTotal++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.i("XLight", "locationiq request city success->" + jsonData);
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jb = new JSONObject(jsonData);
                                    if (!jb.isNull("address")) {
                                        JSONObject address = jb.getJSONObject("address");
                                        if (!address.isNull("city")) {
                                            txtCity.setText(address.getString("city"));
                                        } else if (!address.isNull("county")) {
                                            txtCity.setText(address.getString("county"));
                                        } else if (!address.isNull("state")) {
                                            txtCity.setText(address.getString("state"));
                                        } else {
                                            txtCity.setText(getString(R.string.share_list_unknown));
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 重新获取
                                if (requestTotal < 3) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getCityByLocationiqAPI(location);
                                        }
                                    });
                                }
                                requestTotal++;
                            }
                        });
                    }
                } catch (Exception e) {
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
        weatherDetails.setHumidity(currently.getDouble("humidity"));
        JSONObject hourly = forecast.getJSONObject("hourly");
        weatherDetails.setSummaryHour(hourly.getString("summary"));
        JSONObject daily = forecast.getJSONObject("daily").getJSONArray("data").getJSONObject(0);
        weatherDetails.setMin(daily.getDouble("temperatureLow"));
        weatherDetails.setMax(daily.getDouble("temperatureHigh"));
        return weatherDetails;
    }

    private void updateDisplay() {
        weatherIcon.setImageDrawable(getWeatherIcon(mWeatherDetails.getIcon()));
        txtWeather.setText(mWeatherDetails.getSummary());
        txtOutDHTt.setText("" + mWeatherDetails.getTemp(""));
        txtSummary.setText("" + mWeatherDetails.getSummaryHour());
        txtMin.setText("" + mWeatherDetails.getMin(""));
        txtMax.setText("" + mWeatherDetails.getMax(""));
        txtHumidity.setText("" + mWeatherDetails.getHumidity());
    }

    public void changeGridView(GridView gv, int size) {
        // item宽度
        int itemWidth = dip2px(155);
        // item之间的间隔
        int itemPaddingH = dip2px(0);
        // 计算GridView宽度
        int gvWidth = size * (itemWidth + itemPaddingH) + 80;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gv.setLayoutParams(params);
        gv.setColumnWidth(itemWidth);
        gv.setHorizontalSpacing(itemPaddingH);
        gv.setStretchMode(GridView.NO_STRETCH);
        gv.setNumColumns(size);
    }

    public int dip2px(float dpValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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

    public boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:" + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}