package com.umarbhutta.xlightcompanion.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.views.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.particle.android.sdk.utils.ui.Toaster;

@SuppressLint("ValidFragment")
public class EffectsFragment extends Fragment implements View.OnClickListener {
    private TextView text;
    private ImageView imgSwitch;
    private ArrayList<CircleImageView> lstFilter = new ArrayList<CircleImageView>();

    public static EffectsFragment getInstance() {
        EffectsFragment sf = new EffectsFragment();
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgSwitch:
                // 进行开关操作，反向操作
                if (text.getText().toString().equals(getString(R.string.on))) { // 如果是开的
                    // 进行关闭操作
                    ControlDeviceActivity.mCurrentDevice.PowerSwitch(xltDevice.STATE_OFF);
                    ControlDeviceActivity.devicenodes.ison = xltDevice.STATE_OFF;
                } else {
                    ControlDeviceActivity.mCurrentDevice.PowerSwitch(xltDevice.STATE_ON);
                    ControlDeviceActivity.devicenodes.ison = xltDevice.STATE_ON;
                }
                // 同步状态至其他页面
                EventBus.getDefault().post(ControlDeviceActivity.devicenodes);
                break;
            case R.id.iv_filter_1:
                setEffect(1);
                break;
            case R.id.iv_filter_2:
                setEffect(2);
                break;
            case R.id.iv_filter_3:
                setEffect(3);
                break;
            case R.id.iv_filter_4:
                setEffect(4);
                break;
            default:
                setEffect(0);
        }
    }

    public void setEffect(int filter) {
        if (filter > 0) {
            ControlDeviceActivity.devicenodes.filter = filter;
            ControlDeviceActivity.mCurrentDevice.SetSpecialEffect(ControlDeviceActivity.devicenodes.filter);
            updateDevice();
        } else {
            // 暂未开放
            ToastUtil.showToast(getActivity(), getString(R.string.more_filter));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_effects, null);
        text = (TextView) v.findViewById(R.id.txtSwitch);
        imgSwitch = (ImageView) v.findViewById(R.id.imgSwitch);
        imgSwitch.setOnClickListener(this);
        CircleImageView filter1 = ((CircleImageView) v.findViewById(R.id.iv_filter_1));
        CircleImageView filter2 = ((CircleImageView) v.findViewById(R.id.iv_filter_2));
        CircleImageView filter3 = ((CircleImageView) v.findViewById(R.id.iv_filter_3));
        CircleImageView filter4 = ((CircleImageView) v.findViewById(R.id.iv_filter_4));
        CircleImageView filter5 = ((CircleImageView) v.findViewById(R.id.iv_filter_5));
        CircleImageView filter6 = ((CircleImageView) v.findViewById(R.id.iv_filter_6));
        filter1.setOnClickListener(this);
        filter2.setOnClickListener(this);
        filter5.setOnClickListener(this);
        filter4.setOnClickListener(this);
        filter3.setOnClickListener(this);
        filter6.setOnClickListener(this);
        lstFilter.add(filter1);
        lstFilter.add(filter2);
        lstFilter.add(filter3);
        lstFilter.add(filter4);
        lstFilter.add(filter5);
        lstFilter.add(filter6);
        // 进行状态更新
        updateDevice();
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Devicenodes devicenodes) {
        updateDevice();
    }

    public void updateDevice() {
        if (ControlDeviceActivity.devicenodes != null) {
            if (ControlDeviceActivity.devicenodes.filter != 0) {
                // 清除所有选中
                for (CircleImageView iv : lstFilter) {
                    iv.setBorderWidth(0);
                }
                // 设置选中的
                switch (ControlDeviceActivity.devicenodes.filter) {
                    case 1:
                        lstFilter.get(0).setBorderWidth(8);
                        break;
                    case 2:
                        lstFilter.get(1).setBorderWidth(8);
                        break;
                    case 3:
                        lstFilter.get(2).setBorderWidth(8);
                        break;
                    case 4:
                        lstFilter.get(3).setBorderWidth(8);
                        break;
                }
            }
            text.setText(ControlDeviceActivity.devicenodes.ison == 0 ? getString(R.string.off) : getString(R.string.on));
            imgSwitch.setImageResource(ControlDeviceActivity.devicenodes.ison == 0 ? R.drawable.close : R.drawable.kq);
        }
    }
}