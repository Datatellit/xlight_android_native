package com.umarbhutta.xlightcompanion.room;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastCompat;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.LightItemAdapter;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RoomMainFragment extends Fragment implements View.OnClickListener {


    LightItemAdapter adapterLight;
    GridView gvLight;
    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private ProgressDialog progressDialog;
    public static List<Rows> deviceList = new ArrayList<Rows>();
    public static List<Devicenodes> devicenodes = new ArrayList<Devicenodes>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.room);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.VISIBLE);
//        btn_add.setBackground(getActivity().getDrawable(R.drawable.control_add));
//        btn_add.setBackgroundResource(R.drawable.control_add);
        btn_add.setOnClickListener(this);

        //setup recycler view
        gvLight = (GridView) view.findViewById(R.id.gvLight);
        getBaseInfo();
        return view;
    }

    public void getBaseInfo() {
        progressDialog = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
        progressDialog.show();
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

                        deviceList.clear();
                        deviceList.addAll(devices);
                        if (adapterLight != null) {
                            Log.d("XLight", "update device list at request after");
                            adapterLight.notifyDataSetChanged();
                        }
                        addDeviceMapsSDK(deviceList);
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

    public void addDeviceMapsSDK(final List<Rows> deviceList) {
        if (deviceList != null && deviceList.size() == 0) {
            devicenodes.clear();
            if (adapterLight != null) {
                adapterLight.notifyDataSetChanged();
            }
        } else if (null != deviceList && deviceList.size() > 0) {
            devicenodes.clear();
//            Log.e("XLight", "device count:" + deviceList.size());
            SharedPreferencesUtils.putObject(getActivity(), SharedPreferencesUtils.KEY_DEVICE_LIST, deviceList);
            if (SlidingMenuMainActivity.xltDeviceMaps != null) {
                SlidingMenuMainActivity.xltDeviceMaps.clear();
            }
            for (int i = 0; i < deviceList.size(); i++) {
                // Initialize SmartDevice SDK
                final Rows device = deviceList.get(i);
                if (device.devicenodes != null) {
                    for (int lv_idx = 0; lv_idx < device.devicenodes.size(); lv_idx++) {
                        device.devicenodes.get(lv_idx).coreid = deviceList.get(i).coreid;
                    }
                    devicenodes.addAll(device.devicenodes);
                }
                ArrayList<String> lstDevice = new ArrayList<String>();
                for (Rows r : deviceList) {
                    lstDevice.add(r.coreid);
                }
                if (adapterLight == null) {
                    adapterLight = new LightItemAdapter(getContext(), devicenodes, true);
                    gvLight.setAdapter(adapterLight);
                    adapterLight.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            ImmersionBar.with(this).titleBar(R.id.ll_main_top).statusBarDarkFont(true).init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.btn_add:
                // 跳转到添加场景页面
                ToastUtil.showToast(this.getContext(), getString(R.string.feature_wait));
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
