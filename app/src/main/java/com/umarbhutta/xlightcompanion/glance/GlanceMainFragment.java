package com.umarbhutta.xlightcompanion.glance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baoyz.actionsheet.ActionSheet;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.CloudAccount;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.DataReceiver;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.bindDevice.BindDeviceConfirmActivity;
import com.umarbhutta.xlightcompanion.control.adapter.DevicesMainListAdapter;
import com.umarbhutta.xlightcompanion.deviceList.DeviceListActivity;
import com.umarbhutta.xlightcompanion.main.EditDeviceActivity;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestUnBindDevice;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSensorInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequstCallback;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 */
public class GlanceMainFragment extends Fragment implements View.OnClickListener {
    private ImageButton fab;
    TextView txtLocation, outsideTemp, degreeSymbol, roomTemp, roomHumidity, outsideHumidity, apparentTemp;
    ImageView imgWeather;
    private RelativeLayout home_menu, home_setting;

    private static final String TAG = GlanceMainFragment.class.getSimpleName();
    private ListView devicesListView;
    WeatherDetails mWeatherDetails;

    private Handler m_handlerGlance;

    private Bitmap icoDefault, icoClearDay, icoClearNight, icoRain, icoSnow, icoSleet, icoWind, icoFog;
    private Bitmap icoCloudy, icoPartlyCloudyDay, icoPartlyCloudyNight;
    private static int ICON_WIDTH = 70;
    private static int ICON_HEIGHT = 75;
    private PtrFrameLayout ptrFrameLayout;

    /**
     * 设备列表
     */
    public static List<Rows> deviceList = new ArrayList<Rows>();
    public static List<Devicenodes> devicenodes = new ArrayList<Devicenodes>();
    private DevicesMainListAdapter devicesListAdapter;
    private TextView default_text;
    private TextView save_money;
    private TextView valRoomBrightness;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_setting:
                // 跳转到选择的主设备列表页面
                if (UserUtils.isLogin(getContext())) {
                    onFabPressed(DeviceListActivity.class);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                    getActivity().finish();
                }

                break;
            case R.id.home_menu:
                switchFragment();
                break;
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

    private void onFabPressed(Class activity) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.onActivityPressed(activity);
        }
    }

    @Override
    public void onDestroyView() {
        devicesListView.setAdapter(null);
        super.onDestroyView();
    }

    private Dialog mSelectDialog = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_glance, container, false);
        locationClient = new AMapLocationClient(this.getActivity());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        //        hide nav bar
        fab = (ImageButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到绑定设备页面
                if (UserUtils.isLogin(getContext())) {
                    ActionSheet.createBuilder(getContext(), getFragmentManager())
                            .setCancelButtonTitle(getString(R.string.cancel))
                            .setOtherButtonTitles(getString(R.string.add_device_bluetooth), getString(R.string.add_device_accesspoint))
                            .setCancelableOnTouchOutside(true)
                            .setListener(new ActionSheet.ActionSheetListener() {
                                @Override
                                public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                                }

                                @Override
                                public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                    Intent intent = new Intent(getContext(), BindDeviceConfirmActivity.class);
                                    intent.putExtra("type", index);
                                    startActivityForResult(intent, 1);
                                    actionSheet.dismiss();
                                }
                            }).show();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                    getActivity().finish();
                }

            }
        });

        default_text = (TextView) view.findViewById(R.id.default_text);
        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        txtLocation = (TextView) view.findViewById(R.id.location);
        outsideTemp = (TextView) view.findViewById(R.id.outsideTemp);
        degreeSymbol = (TextView) view.findViewById(R.id.degreeSymbol);
        outsideHumidity = (TextView) view.findViewById(R.id.valLocalHumidity);
        apparentTemp = (TextView) view.findViewById(R.id.valApparentTemp);
        roomTemp = (TextView) view.findViewById(R.id.valRoomTemp);
        roomHumidity = (TextView) view.findViewById(R.id.valRoomHumidity);
        imgWeather = (ImageView) view.findViewById(R.id.weatherIcon);
        home_menu = (RelativeLayout) view.findViewById(R.id.home_menu);
        home_menu.setOnClickListener(this);
        home_setting = (RelativeLayout) view.findViewById(R.id.home_setting);
        home_setting.setOnClickListener(this);
        save_money = (TextView) view.findViewById(R.id.save_money);

        valRoomBrightness = (TextView) view.findViewById(R.id.valRoomBrightness);

        Resources res = getResources();
        Bitmap weatherIcons = decodeResource(res, R.drawable.weather_icons_1, 420, 600);
        icoDefault = Bitmap.createBitmap(weatherIcons, 0, 0, ICON_WIDTH, ICON_HEIGHT);
        icoClearDay = Bitmap.createBitmap(weatherIcons, ICON_WIDTH, 0, ICON_WIDTH, ICON_HEIGHT);
        icoClearNight = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 2, 0, ICON_WIDTH, ICON_HEIGHT);
        icoRain = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 5, ICON_HEIGHT * 2, ICON_WIDTH, ICON_HEIGHT);
        icoSnow = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 4, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoSleet = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 5, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoWind = Bitmap.createBitmap(weatherIcons, 0, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoFog = Bitmap.createBitmap(weatherIcons, 0, ICON_HEIGHT * 2, ICON_WIDTH, ICON_HEIGHT);
        icoCloudy = Bitmap.createBitmap(weatherIcons, ICON_WIDTH, ICON_HEIGHT * 5, ICON_WIDTH, ICON_HEIGHT);
        icoPartlyCloudyDay = Bitmap.createBitmap(weatherIcons, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
        icoPartlyCloudyNight = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 2, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);

        //setup recycler view
        devicesListView = (ListView) view.findViewById(R.id.devicesListView);

        //getTitleInfo();
        initLocation();
        getBaseInfo();
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //加载数据
                Log.d("XLight", "RefreshBegin");
                initLocation();
                refreshDeviceInfo(frame);
            }
        });
        return view;
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

    private void updateUIHandler() {
        m_handlerGlance = new Handler(this.getContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                Log.e(TAG, "GlanceMainFragment_msg=" + msg.getData().toString());
                if (deviceList != null && deviceList.size() > 0 && msg.getData().getInt("nd", -1) == 130) {
                    int intValue = msg.getData().getInt("DHTt", -255);
                    if (intValue != -255) {
                        roomTemp.setText(intValue + "\u00B0");
                    }
                    intValue = msg.getData().getInt("DHTh", -255);
                    if (intValue != -255) {
                        roomHumidity.setText(intValue + "\u0025");
                    }
                    intValue = msg.getData().getInt("PM25", -255);
                    if (intValue != -255) {
                        valRoomBrightness.setText(intValue + "μg/m³");
                    }
                }
            }
        };
        //先清除
        SlidingMenuMainActivity.m_mainDevice.clearDataEventHandlerList();
        SlidingMenuMainActivity.m_mainDevice.addDataEventHandler(m_handlerGlance);
    }

    /**
     * 获取title信息
     */
    private void getTitleInfo() {

        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            return;
        }

        String forecastUrl = "https://api.forecast.io/forecast/" + CloudAccount.DarkSky_apiKey + "/" + mLatitude + "," + mLongitude;

        if (NetworkUtils.isNetworkAvaliable(getActivity())) {
            OkHttpClient client = new OkHttpClient();
            //build request
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            //put request in call object to use for returning data
            Call call = client.newCall(request);
            Log.i("XLight", String.format("request weahter info->%s,%s", mLatitude, mLongitude));
            //make async call
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
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
//                                    ToastUtil.showToast(getActivity(), "There was an error retrieving weather data.");
                                }
                            });
                        }
                    } catch (IOException | JSONException | NullPointerException e) {
                        Logger.i("Exception caught: " + e);
                    }
                }
            });
        } else {
        }
    }

    private View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_choose_add_wifi_device: // 扫描设备添加
                    Toast.makeText(getActivity(), getString(R.string.scan_device_add), Toast.LENGTH_SHORT).show();
                    break;
                //扫描二维码添加
                case R.id.btn_choose_scan_add_device:
                    Toast.makeText(getActivity(), getString(R.string.scan_qr_add), Toast.LENGTH_SHORT).show();
                    break;
                // 输入口令添加
                case R.id.btn_choose_add_line_device:
                    Toast.makeText(getActivity(), getString(R.string.input_order_add), Toast.LENGTH_SHORT).show();
                    break;
                // 取消
                case R.id.btn_cancel:
                    if (mSelectDialog != null) {
                        mSelectDialog.dismiss();
                    }
                    break;
            }
        }
    };

    private void updateDisplay() {
        imgWeather.setVisibility(View.VISIBLE);
        imgWeather.setImageResource(R.drawable.cloud);
//        txtLocation.setText(mWeatherDetails.getLocation());
//        imgWeather.setImageBitmap(getWeatherIcon(mWeatherDetails.getIcon()));
        outsideTemp.setText(" " + mWeatherDetails.getTemp("celsius"));
        degreeSymbol.setText("℃");
        outsideHumidity.setText(mWeatherDetails.getmHumidity() + "\u0025");
        apparentTemp.setText(mWeatherDetails.getApparentTemp("celsius") + "℃");

//        roomTemp.setText(SlidingMenuMainActivity.m_mainDevice.m_Data.m_RoomTemp + "℃");
//        roomHumidity.setText(SlidingMenuMainActivity.m_mainDevice.m_Data.m_RoomHumidity + "\u0025");
    }

    private WeatherDetails getWeatherDetails(String jsonData) throws JSONException {
        WeatherDetails weatherDetails = new WeatherDetails();

        //make JSONObject for all JSON
        JSONObject forecast = new JSONObject(jsonData);

        //JSONObject for nested JSONObject inside 'forecast' for current weather details
        JSONObject currently = forecast.getJSONObject("currently");

        weatherDetails.setTemp(currently.getDouble("temperature"));
        weatherDetails.setIcon(currently.getString("icon"));
        weatherDetails.setApparentTemp(currently.getDouble("apparentTemperature"));
        weatherDetails.setHumidity((int) (currently.getDouble("humidity") * 100 + 0.5));

        return weatherDetails;
    }

    private Bitmap decodeResource(Resources resources, final int id, final int newWidth, final int newHeight) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        Bitmap loadBmp = BitmapFactory.decodeResource(resources, id, opts);

        int width = loadBmp.getWidth();
        int height = loadBmp.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newBmp = Bitmap.createBitmap(loadBmp, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    public Bitmap getWeatherIcon(final String iconName) {
        if (iconName.equalsIgnoreCase("clear-day")) {
            return icoClearDay;
        } else if (iconName.equalsIgnoreCase("clear-night")) {
            return icoClearNight;
        } else if (iconName.equalsIgnoreCase("rain")) {
            return icoRain;
        } else if (iconName.equalsIgnoreCase("snow")) {
            return icoSnow;
        } else if (iconName.equalsIgnoreCase("sleet")) {
            return icoSleet;
        } else if (iconName.equalsIgnoreCase("wind")) {
            return icoWind;
        } else if (iconName.equalsIgnoreCase("fog")) {
            return icoFog;
        } else if (iconName.equalsIgnoreCase("cloudy")) {
            return icoCloudy;
        } else if (iconName.equalsIgnoreCase("partly-cloudy-day")) {
            return icoPartlyCloudyDay;
        } else if (iconName.equalsIgnoreCase("partly-cloudy-night")) {
            return icoPartlyCloudyNight;
        } else {
            return icoDefault;
        }
    }

    /**
     * 获取设备信息
     */
    public void getBaseInfo() {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            ToastUtil.showToast(getActivity(), R.string.net_error);
            //TODO
            List<Rows> devices = (List<Rows>) SharedPreferencesUtils.getObject(getActivity(), SharedPreferencesUtils.KEY_DEVICE_LIST, null);
            if (null != devices && devices.size() > 0) {
                deviceList.clear();
                deviceList.addAll(devices);
            }
            if (devicesListAdapter != null) {
                //更新数据
                Log.d("XLight", "update device list");
                codeChange = true;
                devicesListAdapter.notifyDataSetChanged();
                codeChange = false;
            }
            addDeviceMapsSDK(deviceList);
            return;
        }
        if (!UserUtils.isLogin(getActivity())) {
            return;
        }
        refreshDeviceInfo(null);
    }

    public void refreshDeviceInfo(final PtrFrameLayout ptrFrameLayout) {
        final ProgressDialog progressDialog = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
        if (ptrFrameLayout == null) {
            if (progressDialog != null) {
                progressDialog.show();
            }
        }
        RequestFirstPageInfo.getInstance(getActivity()).getBaseInfo(new RequestFirstPageInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final DeviceInfoResult mDeviceInfoResult) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && ptrFrameLayout == null) {
                            progressDialog.dismiss();
                        } else if (ptrFrameLayout != null) {
                            ptrFrameLayout.refreshComplete();
                        }
                        List<Rows> devices = mDeviceInfoResult.rows;
                        if (null != devices && devices.size() > 0) {
                            if (null != mDeviceInfoResult && null != mDeviceInfoResult.Energysaving) {
                                save_money.setText(getString(R.string.this_month_has_save_money_more) + mDeviceInfoResult.Energysaving.value + " " + getString(R.string.this_month_has_save_money_more_two));
                            }
                            ArrayList<String> lstDevice = new ArrayList<String>();
                            for (int i = 0; i < devices.size(); i++) {
                                lstDevice.add(devices.get(i).coreid);
                            }
                            getSensorInfo(lstDevice);
                        }
                        deviceList.clear();
                        deviceList.addAll(devices);
                        if (devicesListAdapter != null) {
                            Log.d("XLight", "update device list at request after");
                            codeChange = true;
                            devicesListAdapter.notifyDataSetChanged();
                            codeChange = false;
                        }
                        addDeviceMapsSDK(deviceList);
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, String errMsg) {
                //失败的处理
                if (progressDialog != null && ptrFrameLayout == null) {
                    progressDialog.dismiss();
                } else if (ptrFrameLayout != null) {
                    ptrFrameLayout.refreshComplete();
                }
            }
        });
    }

    public void getSensorInfo(List<String> devices) {
        RequestSensorInfo.getInstance(getActivity()).getBaseInfo(new RequestSensorInfo.OnRequestSensorInfoCallback() {
            @Override
            public void onRequestSensorInfoSuccess(final List<Sensorsdata> mSensorsdata) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 打印List
                        if (mSensorsdata.size() > 0) {
                            Sensorsdata sd = mSensorsdata.get(0);
                            valRoomBrightness.setText("" + (int) sd.PM25 + "μg/m³");
                            roomHumidity.setText("" + (int) sd.DHTh + "\u0025");
                            roomTemp.setText("" + (int) sd.DHTt + "℃");
                        }
                    }
                });
            }

            @Override
            public void onRequestSensorInfoFail(int code, String errMsg) {

            }
        }, devices);
    }

    @Override
    public void onResume() {
        super.onResume();
        //刷新列表
        if (devicesListAdapter != null)
            devicesListAdapter.notifyDataSetChanged();
        Log.d("XLight", "frame main resume");
    }

    private boolean codeChange = false;
    private List<String> connectFailed = new ArrayList<String>();
    private int deviceSDKResCount = 0;

    public void addDeviceMapsSDK(final List<Rows> deviceList) {
        if (null != deviceList && deviceList.size() > 0) {
            devicenodes.clear();
            Log.d("XLight", "device count:" + deviceList.size());
            default_text.setVisibility(View.GONE);
            SharedPreferencesUtils.putObject(getActivity(), SharedPreferencesUtils.KEY_DEVICE_LIST, deviceList);
            if (SlidingMenuMainActivity.xltDeviceMaps != null) {
                SlidingMenuMainActivity.xltDeviceMaps.clear();
            }
            LoginResult lr = UserUtils.getUserInfo(getActivity());
            connectFailed.clear();
            deviceSDKResCount = 0;
            for (int i = 0; i < deviceList.size(); i++) {
                // Initialize SmartDevice SDK
                final xltDevice m_XltDevice = new xltDevice();
                final Rows device = deviceList.get(i);
                m_XltDevice.Init(getActivity(), lr.username, lr.password);
                if (device.devicenodes != null) {
                    for (int lv_idx = 0; lv_idx < device.devicenodes.size(); lv_idx++) {
                        m_XltDevice.addNodeToDeviceList(device.devicenodes.get(lv_idx).nodeno, xltDevice.DEFAULT_DEVICE_TYPE, device.devicenodes.get(lv_idx).devicenodename);
                        device.devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                    }
                    devicenodes.addAll(device.devicenodes);
                }
                if (device.coreid != null) {
                    // Connect to Controller
                    Log.d("XLight", "current index:" + i);
                    m_XltDevice.Connect(device.coreid, new xltDevice.callbackConnect() {
                        @Override
                        public void onConnected(xltDevice.BridgeType bridge, boolean connected) {
                            Logger.d(TAG, String.format("coreID:%s,Bridge:%s,isControlConnect=%s", device.coreid, bridge, connected));
                            deviceSDKResCount++;
                            m_XltDevice.m_onConnected = null;
                            //判断是否连接成功
                            if (!connected && bridge == xltDevice.BridgeType.Cloud) {
                                //记录，用于后面的提示
                                connectFailed.add(device.devicename);
                            } else if (connected && bridge == xltDevice.BridgeType.Cloud) {
                                SlidingMenuMainActivity.xltDeviceMaps.put(device.coreid, m_XltDevice);
                                if (device.maindevice == 1) {//主设备 TODO TODO  设置监听 广播回调
                                    if (SlidingMenuMainActivity.m_mainDevice != null) {
                                        SlidingMenuMainActivity.m_mainDevice.Disconnect();
                                        SlidingMenuMainActivity.m_mainDevice = null;
                                    }
                                    SlidingMenuMainActivity.m_mainDevice = m_XltDevice;
                                    if (SlidingMenuMainActivity.m_mainDevice != null) {
                                        //设置handler监听，获取数据室内温湿度
                                        setHandlerMessage();
                                    }
                                }
                            }
                            if (deviceList.size() == deviceSDKResCount && connectFailed.size() > 0) {
                                //提示连接失败的设备
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(getActivity(), String.format(getString(R.string.device_connect_failed), StringUtils.join(connectFailed.toArray(), ",")));
                                    }
                                });
                            }
                        }
                    });
                }
            }
            if (devicesListAdapter == null) {
                devicesListAdapter = new DevicesMainListAdapter(getContext(), devicenodes);
                devicesListView.setAdapter(devicesListAdapter);
                devicesListAdapter.setOnSwitchStateChangeListener(new DevicesMainListAdapter.OnSwitchStateChangeListener() {
                    @Override
                    public void onLongClick(int position) {
                        //showDeleteSceneDialog(position);
                    }

                    @Override
                    public void onSwitchChange(int position, boolean checked) {
                        if (codeChange)
                            return;
                        SlidingMenuMainActivity.m_mainDevice = SlidingMenuMainActivity.xltDeviceMaps.get(devicenodes.get(position).coreid);
                        SlidingMenuMainActivity.m_mainDevice.setDeviceID(devicenodes.get(position).nodeno);
                        Log.d("XLight", "main power switch:" + checked);
                        SlidingMenuMainActivity.m_mainDevice.PowerSwitch(checked ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
                        devicenodes.get(position).ison = checked ? xltDevice.STATE_ON : xltDevice.STATE_OFF;
                    }
                });
                devicesListAdapter.setOnClickListener(new DevicesMainListAdapter.OnClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        // 点击事件 跳转到编辑设备页面
                        Intent intent = new Intent(getActivity(), EditDeviceActivity.class);
                        intent.putExtra("info", devicenodes.get(position));
                        intent.putExtra("position", position);
                        getActivity().startActivity(intent);
                    }
                });
                Log.d("XLight", "update device list first");
            }
            codeChange = true;
            devicesListAdapter.notifyDataSetChanged();
            codeChange = false;
            if (null != deviceList && deviceList.size() > 0) {
                default_text.setVisibility(View.GONE);
            } else {
                default_text.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 弹出解绑设备确认框
     */
    private void showDeleteSceneDialog(final int position) {
        AlertDialog mAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.unbind_device_tap))
                .setMessage(getString(R.string.sure_unbind_device))
                .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unBindDevice(position);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        Button btn1 = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    /**
     * 解绑设备
     *
     * @param position
     */
    private void unBindDevice(final int position) {
        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            ToastUtil.showToast(getActivity(), R.string.net_error);
            return;
        }

        ((SlidingMenuMainActivity) getActivity()).showProgressDialog(getString(R.string.setting));

        RequestUnBindDevice.getInstance().unBindDevice(getActivity(), "" + devicenodes.get(position).id, new CommentRequstCallback() {
            @Override
            public void onCommentRequstCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((SlidingMenuMainActivity) getActivity()).cancelProgressDialog();
                        ToastUtil.showToast(getActivity(), getString(R.string.unbind_sucess));
                        updateUnbindList(position);
                    }
                });
            }

            @Override
            public void onCommentRequstCallbackFail(int code, final String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((SlidingMenuMainActivity) getActivity()).cancelProgressDialog();
                        ToastUtil.showToast(getActivity(), getString(R.string.unbind_fail) + errMsg);
                    }
                });
            }
        });
    }

    /**
     * 解绑后更新页面
     *
     * @param position
     */
    private void updateUnbindList(int position) {
        devicenodes.remove(position);
        devicesListAdapter.notifyDataSetChanged();
    }

    /**
     * 位置信息
     */
    public static String city = "";
    public static String country = "";
    public static double mLongitude = -80.5204;
    public static double mLatitude = 43.4643;
    // 高德定位
    private AMapLocationClient locationClient = null;

    /**
     * 初始化百度地图
     */
    private void initLocation() {
        AMapLocation location = locationClient.getLastKnownLocation();
        if (location != null) {
            Log.d(TAG, "getLastKnownLocation success");
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            city = location.getCity();
            country = location.getCountry();
            Log.d("XLight", String.format("long:%s,latitude:%s,ciry:%s,country:%s", mLongitude, mLatitude, city, country));
            //请求天气信息
            updateLocationInfo();
        } else {
            // 启动定位
            locationClient.startLocation();
        }
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
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
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

    /**
     * 定位监听
     */
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
                        txtLocation.setText(city);
                    } else {
                        if (country == null) {
                            country = "";
                        }
                        txtLocation.setText("" + country);
                    }
                    getTitleInfo();
                }
            });
    }

}
