package com.umarbhutta.xlightcompanion.scenario;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.NoFastClickUtils;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.bindDevice.BindDeviceSearchActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 75932 on 2017/11/1.
 */

public class AddSceneActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private GridView gvOk;
    private GridView gvSelect;
    private TextView txtSetting;
    private EditText etName;
    private EditText etDesc;
    public static List<Rows> deviceList = new ArrayList<Rows>();
    public static List<Devicenodes> devicenodes = new ArrayList<Devicenodes>();
    public static List<Devicenodes> deviceSures = new ArrayList<Devicenodes>();
    public ProgressDialog progressDialog;
    private List<Map<String, Object>> dataIcon = new ArrayList<Map<String, Object>>();
    private ScenarioItemAdapter adapterOk;
    private ScenarioItemAdapter adapterSelect;
    private DialogPlus dialogPlus;
    private DialogPlus dialogIcon;
    private View mPopupHeadViewy;
    private View mIconView;
    private int curIndex;
    private ColorSeekBar colorSeekBar;
    private LinearLayout llColor;
    private LinearLayout llCCT;
    private LinearLayout llChange;
    private GridView gvIcon;
    private String selectIcon;
    private SceneResult sceneResult;
    private ImageButton imageButton;
    int[] image = {R.drawable.scene, R.drawable.scene_wakeup, R.drawable.scene_goout, R.drawable.scene_xx, R.drawable.scene_eat, R.drawable.scene_book, R.drawable.scene_sleep, R.drawable.scene_hj, R.drawable.scene_tv, R.drawable.scene_yx, R.drawable.scene_yl, R.drawable.dy};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_add_scene);

        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }
        if (getIntent().getStringExtra("from") != null) {
            //处理这个场景
            sceneResult = (SceneResult) getIntent().getSerializableExtra("scene");
            Log.e("XLight", "edit scene:" + sceneResult.id);
        }
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存场景
                saveScene();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.add_scene);

        gvOk = (GridView) findViewById(R.id.gvOk);
        gvSelect = (GridView) findViewById(R.id.gvSelect);
        txtSetting = (TextView) findViewById(R.id.txtSetting);
        etName = (EditText) findViewById(R.id.etName);
        etDesc = (EditText) findViewById(R.id.etDesc);
        mPopupHeadViewy = View.inflate(this, R.layout.activity_add_scene_setting, null);
        mIconView = View.inflate(this, R.layout.activity_add_scene_icon, null);
        gvIcon = (GridView) mIconView.findViewById(R.id.gvIcon);
        imageButton = (ImageButton) findViewById(R.id.imgButton);
        initIcon();
        final CheckBox chkSwitch = (CheckBox) mPopupHeadViewy.findViewById(R.id.chkSwitch);
        final SeekBar cct = (SeekBar) mPopupHeadViewy.findViewById(R.id.cctSeekBar);
        final BubbleSeekBar brightness = (BubbleSeekBar) mPopupHeadViewy.findViewById(R.id.brightnessSeekBar);
        final TextView txtChange = (TextView) mPopupHeadViewy.findViewById(R.id.txtChange);
        final TextView txtSwitch = (TextView) mPopupHeadViewy.findViewById(R.id.txtSwitch);
        colorSeekBar = (ColorSeekBar) mPopupHeadViewy.findViewById(R.id.colorSlider);
        llColor = (LinearLayout) mPopupHeadViewy.findViewById(R.id.ll_color);
        llCCT = (LinearLayout) mPopupHeadViewy.findViewById(R.id.ll_cct);
        llChange = (LinearLayout) mPopupHeadViewy.findViewById(R.id.llChange);
        //txtColor = (TextView) mPopupHeadViewy.findViewById(R.id.txtColor);
        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(mPopupHeadViewy))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogIcon = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(mIconView))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        gvSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 将此对象放到上面去
                addGvOk(position);
            }
        });
        gvOk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeGvOk(position);
            }
        });
        gvIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 设置Icon
                imageButton.setBackgroundResource(image[position]);
                //记录选择的图标
                selectIcon = getResources().getResourceName(image[position]);
                dialogIcon.dismiss();
            }
        });
        gvOk.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                curIndex = position;
                //设置默认值
                chkSwitch.setChecked(deviceSures.get(position).ison == 1 ? true : false);
                brightness.setProgress(deviceSures.get(position).brightness);
                cct.setProgress(deviceSures.get(position).cct - 2700);
                if (deviceSures.get(position).devicetype > 1) { // 彩灯控制
                    llChange.setVisibility(View.VISIBLE);
                    llColor.setVisibility(View.VISIBLE);
                    int[] color = deviceSures.get(position).color;
                    colorSeekBar.setColor(Color.rgb(color[0], color[1], color[2]));
                    llCCT.setVisibility(View.GONE);
                } else {
                    llColor.setVisibility(View.GONE);
                    llCCT.setVisibility(View.VISIBLE);
                    llChange.setVisibility(View.GONE);
                }
                // 弹出设置按钮
                dialogPlus.show();
                return true;
            }
        });
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("XLight", "click change cct or color");
                // 改变
                if (txtChange.getText().toString().equals(getString(R.string.txt_cct))) {
                    // 变为颜色
                    txtChange.setText(getString(R.string.txt_color));
                    // 将色温显示，颜色隐藏
                    llCCT.setVisibility(View.VISIBLE);
                    llColor.setVisibility(View.GONE);
                    deviceSures.get(curIndex).scenarioId = "CCT";
                } else {
                    txtChange.setText(getString(R.string.txt_cct));
                    // 将色温显示，颜色隐藏
                    llCCT.setVisibility(View.GONE);
                    llColor.setVisibility(View.VISIBLE);
                    deviceSures.get(curIndex).scenarioId = null;
                }
            }
        });
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                Log.d("XLight", i + "," + i1 + "," + i2);
                int red = (i2 & 0xff0000) >> 16;
                int green = (i2 & 0x00ff00) >> 8;
                int blue = (i2 & 0x0000ff);
                int color[] = {red, green, blue};
                deviceSures.get(curIndex).color = color;
            }
        });

        chkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deviceSures.get(curIndex).ison = isChecked ? 1 : 0;
                adapterOk.notifyDataSetChanged();
            }
        });

        cct.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                deviceSures.get(curIndex).cct = 2700 + progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        brightness.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                deviceSures.get(curIndex).brightness = progress;
                adapterOk.notifyDataSetChanged();
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
        selectIcon = getResources().getResourceName(R.drawable.scene);
        progressDialog = ProgressDialogUtils.showProgressDialog(this, getString(R.string.loading));
        initDevice();
        ImmersionBar.with(this).titleBar(R.id.ll_top_edit).statusBarDarkFont(true).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }


    public void hideSoftInput(IBinder token) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    /**
     * 根据图片的名称获取对应的资源id
     *
     * @param resourceName
     * @return
     */
    public static int getDrawResourceID(String resourceName, Context context) {
        Resources res = context.getResources();
        int picId = res.getIdentifier(resourceName, "drawable", context.getPackageName());
        return picId;
    }

    @Override
    public void onClick(View v) {

    }

    public void changeIcon(View view) {
        // 弹出选择
        dialogIcon.show();
    }

    public void removeGvOk(int p) {
        devicenodes.add(deviceSures.get(p));
        adapterOk.notifyDataSetChanged();
        deviceSures.remove(p);
        adapterSelect.notifyDataSetChanged();
        if (deviceSures.size() == 0) {
            txtSetting.setVisibility(View.GONE);
            gvOk.setVisibility(View.GONE);
        }
    }

    public void addGvOk(int p) {
        deviceSures.add(devicenodes.get(p));
        adapterOk.notifyDataSetChanged();
        devicenodes.remove(p);
        adapterSelect.notifyDataSetChanged();
        if (deviceSures.size() > 0) {
            txtSetting.setVisibility(View.VISIBLE);
            gvOk.setVisibility(View.VISIBLE);
        }
    }

    public void initDevice() {
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            //TODO
            List<Rows> devices = (List<Rows>) SharedPreferencesUtils.getObject(this, SharedPreferencesUtils.KEY_DEVICE_LIST, null);
            if (null != devices && devices.size() > 0) {
                deviceList.clear();
                devicenodes.clear();
                deviceSures.clear();
                deviceList.addAll(devices);
            }
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i).devicenodes != null) {
                    for (int lv_idx = 0; lv_idx < deviceList.get(i).devicenodes.size(); lv_idx++) {
                        deviceList.get(i).devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                    }
                    devicenodes.addAll(deviceList.get(i).devicenodes);
                }
            }
            if (sceneResult != null)
                editScene();
            settingData();
            return;
        }
        getDevice();
    }

    public void initIcon() {
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", image[i]);
            map.put("index", image[i]);
            dataIcon.add(map);
        }
        SimpleAdapter adapterIcon = new SimpleAdapter(this, dataIcon, R.layout.activity_add_scene_icon_item,
                new String[]{"img"}, new int[]{R.id.imgIcon});
        gvIcon.setAdapter(adapterIcon);
    }

    public void settingData() {
        int[] color = {255, 255, 255};
        // 将设备添加到适配器数据中
        for (int i = 0; i < devicenodes.size(); i++) {
            if (devicenodes.get(i).devicetype > 1) {
                if (devicenodes.get(i).color == null)
                    devicenodes.get(i).color = color;
            }
        }
        //进行设备转移
        if (sceneResult != null) {
            Collections.sort(remove);
            for (Integer i : remove) {
                deviceSures.add(devicenodes.get(i));
            }
            //清除现有的
            int index = 0;
            for (Integer i : remove) {
                devicenodes.remove(i - index);
                index++;
            }
        }
        // 初始化gv
        adapterSelect = new ScenarioItemAdapter(this, devicenodes, false);
        gvSelect.setAdapter(adapterSelect);

        adapterOk = new ScenarioItemAdapter(this, deviceSures, true);
        if (deviceSures.size() > 0) {
            gvOk.setVisibility(View.VISIBLE);
        }
        gvOk.setAdapter(adapterOk);
    }

    public void getDevice() {
        Log.d("XLight", "getDeviceInfo");

        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        RequestFirstPageInfo.getInstance(this).getBaseInfo(new RequestFirstPageInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final DeviceInfoResult mDeviceInfoResult) {
                Log.d("XLight", "get scene page data success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        deviceList.clear();
                        devicenodes.clear();
                        deviceSures.clear();
                        deviceList.addAll(mDeviceInfoResult.rows);
                        for (int i = 0; i < deviceList.size(); i++) {
                            if (deviceList.get(i).devicenodes != null) {
                                for (int lv_idx = 0; lv_idx < deviceList.get(i).devicenodes.size(); lv_idx++) {
                                    deviceList.get(i).devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                                }
                                devicenodes.addAll(deviceList.get(i).devicenodes);
                            }
                        }
                        if (sceneResult != null)
                            editScene();
                        settingData();
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, String errMsg) {
                Log.d("XLight", "request scene data error");
                final String err = errMsg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //失败的处理
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtil.showToast(getApplicationContext(), err);
                    }
                });
            }
        });
    }

    ArrayList<Integer> remove = new ArrayList<Integer>();

    public void editScene() {
        //欢迎选项
        etName.setText(sceneResult.name);
        etDesc.setText(sceneResult.remark);
        imageButton.setBackgroundResource(getDrawResourceID(sceneResult.icon, this));
        try {
            JSONArray js = new JSONArray(sceneResult.cmd);
            for (int i = 0; i < js.length(); i++) {
                int p = setDevice(js.getJSONObject(i));
                if (!remove.contains(p) && p != -1) {
                    Log.d("XLight", "edit device:" + p);
                    remove.add(p);
                }
            }
        } catch (Exception e) {
            Log.e("XLight", e.getMessage(), e);
        }
    }

    public int setDevice(JSONObject cmd) {
        try {
            int curIndex = -1;
            for (int j = 0; j < devicenodes.size(); j++) {
                if (devicenodes.get(j).coreid.equals(cmd.getString("deviceId")) && devicenodes.get(j).nodeno == cmd.getInt("nd")) {
                    curIndex = j;
                    switch (cmd.getInt("cmd")) {
                        case 1:
                            devicenodes.get(j).ison = cmd.getInt("state");
                            break;
                        case 2:
                            JSONArray ring = cmd.getJSONArray("ring");
                            devicenodes.get(j).ison = ring.getInt(1);
                            devicenodes.get(j).brightness = ring.getInt(2);
                            devicenodes.get(j).color = new int[]{ring.getInt(5), ring.getInt(6), ring.getInt(7)};
                            devicenodes.get(j).cct = (ring.getInt(3) << 8) + ring.getInt(4);
                            Log.e("XLight", "edit scene->" + ring.getInt(3) + "，" + ring.getInt(4));
                            Log.e("XLight", "edit scene->" + devicenodes.get(j).toString());
                            break;
                        case 3:
                            devicenodes.get(j).brightness = cmd.getInt("value");
                            break;
                        case 5:
                            devicenodes.get(j).cct = cmd.getInt("value");
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
            return curIndex;
        } catch (Exception e) {
            Log.e("XLight", e.getMessage(), e);
            return curIndex;
        }
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

    public void saveScene() {
        if (NoFastClickUtils.isFastClick()) {
            return;
        }
        //保存场景
        if (etName.getText().toString().equals("")) {
            ToastUtil.showToast(this, R.string.please_input_scene_name);
            return;
        }
        if (deviceSures.size() == 0) {
            ToastUtil.showToast(this, R.string.scene_wait_last_select);
            return;
        }
        //转换场景数据
        try {
            ToastUtil.showLoading(this);
            JSONArray js = new JSONArray();
            List<String> lst = new ArrayList<String>();
            for (Devicenodes d : deviceSures) {
                lst.addAll(getCommand(d));
            }
            //解析并转化成js
            for (String s : lst) {
                js.put(new JSONObject(s));
            }
            if (UserUtils.isLogin(this)) {
                JSONObject jb = new JSONObject();
                jb.put("name", etName.getText().toString());
                jb.put("remark", etDesc.getText().toString());
                jb.put("icon", selectIcon);
                jb.put("cmd", js.toString());
                jb.put("userId", UserUtils.getUserInfo(this).id);
                jb.put("type", 2);
                //提交请求
                if (sceneResult == null)
                    HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ADD_SCENE + UserUtils.getAccessToken(this), jb.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
                        @Override
                        public void onHttpRequestSuccess(Object result) {
                            //成功提示
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.dismissLoading();
                                    ToastUtil.showToast(getApplicationContext(), R.string.add_scene_success);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onHttpRequestFail(int code, String errMsg) {
                            //成功提示
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.dismissLoading();
                                    ToastUtil.showToast(getApplicationContext(), R.string.net_error);
                                }
                            });
                        }
                    });
                else {
                    HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_EDIT_SCENE, sceneResult.id, UserUtils.getAccessToken(this)), jb.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
                        @Override
                        public void onHttpRequestSuccess(Object result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.dismissLoading();
                                    ToastUtil.showToast(getApplicationContext(), R.string.edit_scene_success);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onHttpRequestFail(int code, String errMsg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.dismissLoading();
                                    ToastUtil.showToast(getApplicationContext(), R.string.net_error);
                                }
                            });
                        }
                    });
                }
            } else {
                //跳转到登录页
            }
        } catch (Exception e) {
            ToastUtil.dismissLoading();
        }
    }

    public List<String> getCommand(Devicenodes device) {
        try {
            List<String> ljb = new ArrayList<String>();
            JSONObject jb = new JSONObject();
            jb.put("deviceId", device.coreid);
            jb.put("cmd", 1);
            jb.put("nd", device.nodeno);
            jb.put("state", device.ison);
            ljb.add(jb.toString());
            if (device.ison == 0) {
                return ljb;
            } else {
                if (device.devicetype > 1) {
                    ljb.remove(0);
                    // 添加颜色设置
                    jb = new JSONObject();
                    jb.put("deviceId", device.coreid);
                    jb.put("cmd", 2);
                    jb.put("nd", device.nodeno);
                    // 判断使用颜色还是色温
                    int[] color = device.color;
                    JSONArray j_array = new JSONArray();
                    j_array.put(0);
                    j_array.put(1);
                    j_array.put(device.brightness);
                    if (device.scenarioId == null || device.scenarioId.equals("")) {
                        // 使用颜色
                        j_array.put(0);
                        j_array.put(0);
                        j_array.put(color[0]);
                        j_array.put(color[1]);
                        j_array.put(color[2]);
                    } else {
                        // 对色温进行运算
                        j_array.put(device.cct >> 8);
                        j_array.put(device.cct & 0xff);
                        j_array.put(0);
                        j_array.put(0);
                        j_array.put(0);
                    }
                    jb.putOpt("ring", j_array);
                    ljb.add(jb.toString());
                    return ljb;
                } else {
                    // 添加其他属性
                    jb = new JSONObject();
                    jb.put("deviceId", device.coreid);
                    jb.put("cmd", 3);
                    jb.put("nd", device.nodeno);
                    jb.put("value", device.brightness);
                    ljb.add(jb.toString());
                    jb = new JSONObject();
                    jb.put("deviceId", device.coreid);
                    jb.put("cmd", 5);
                    jb.put("nd", (int) device.nodeno);
                    jb.put("value", (int) device.cct);
                    ljb.add(jb.toString());
                    return ljb;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
}
