package com.umarbhutta.xlightcompanion.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.views.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


@SuppressLint("ValidFragment")
public class ColorFragment extends Fragment implements View.OnClickListener {
    private TextView text;
    private ImageView imgSwitch;
    private RoundLinearLayout imgSelected;
    private RoundLinearLayout colorSelected;
    private ColorPickerView colorPickerView;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;


    static class ColorEnum {
        private static int[] pink = {255, 111, 192};
        private static int[] purple = {205, 126, 255};
        private static int[] blue = {113, 129, 255};
        private static int[] sky = {123, 219, 255};
        private static int[] green = {122, 255, 196};
        private static int[] orange = {255, 200, 81};
        private static int[] red = {255, 67, 67};
        private static int[] yellow = {236, 248, 59};
    }

    public static ColorFragment getInstance() {
        ColorFragment sf = new ColorFragment();
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
            case R.id.tv_more:
                // 打开调色盘
                dialog.show();
                break;
            case R.id.tv_pink:
                changeColor(ColorEnum.pink);
                break;
            case R.id.tv_purple:
                changeColor(ColorEnum.purple);
                break;
            case R.id.tv_blue:
                changeColor(ColorEnum.blue);
                break;
            case R.id.tv_sky:
                changeColor(ColorEnum.sky);
                break;
            case R.id.tv_green:
                changeColor(ColorEnum.green);
                break;
            case R.id.tv_red:
                changeColor(ColorEnum.red);
                break;
            case R.id.tv_yellow:
                changeColor(ColorEnum.yellow);
                break;
            case R.id.tv_orange:
                changeColor(ColorEnum.orange);
                break;
        }
    }

    public void changeColor(int[] color) {
        ControlDeviceActivity.devicenodes.color = color;
        // 控制颜色变化，无需对外通知
        ControlDeviceActivity.mCurrentDevice.ChangeColor(xltDevice.RING_ID_ALL, true, ControlDeviceActivity.devicenodes.brightness, 0, ControlDeviceActivity.devicenodes.color[0], ControlDeviceActivity.devicenodes.color[1], ControlDeviceActivity.devicenodes.color[2]);
        // 更新颜色
        updateDevice();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_color, null);
        text = (TextView) v.findViewById(R.id.txtSwitch);
        imgSwitch = (ImageView) v.findViewById(R.id.imgSwitch);
        imgSwitch.setOnClickListener(this);
        imgSelected = (RoundLinearLayout) v.findViewById(R.id.tv_selected);
        ((RoundLinearLayout) v.findViewById(R.id.tv_pink)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_blue)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_sky)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_orange)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_red)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_yellow)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_purple)).setOnClickListener(this);
        ((RoundLinearLayout) v.findViewById(R.id.tv_green)).setOnClickListener(this);
        ((CircleImageView) v.findViewById(R.id.tv_more)).setOnClickListener(this);
        builder = new AlertDialog.Builder(this.getContext());
        dialog = builder.create();
        initDialog();
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
            if (ControlDeviceActivity.devicenodes.color != null) {
                // 设置颜色
                int color = Color.rgb(ControlDeviceActivity.devicenodes.color[0], ControlDeviceActivity.devicenodes.color[1], ControlDeviceActivity.devicenodes.color[2]);
                imgSelected.getDelegate().setBackgroundColor(color);
                colorSelected.getDelegate().setBackgroundColor(color);
            }
            text.setText(ControlDeviceActivity.devicenodes.ison == 0 ? getString(R.string.off) : getString(R.string.on));
            imgSwitch.setImageResource(ControlDeviceActivity.devicenodes.ison == 0 ? R.drawable.close : R.drawable.kq);
        }
    }

    // 判断是否为第一次打开
    private int first = 0;

    private void initDialog() {
        View view = View.inflate(this.getContext(), R.layout.activity_color_select, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
        colorSelected = (RoundLinearLayout) view.findViewById(R.id.tv_selected);
        ((ImageView) view.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭
                dialog.dismiss();
            }
        });
        ((RoundLinearLayout) view.findViewById(R.id.rll_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        colorPickerView = (ColorPickerView) view.findViewById(R.id.colorPickerView);
        // colorPickerView.setFlagView(new CustomFlag(this, R.layout.layout_flag));
        colorPickerView.setPreferenceName("MyColorPickerView");
        colorPickerView.setACTON_UP(true);
//        colorPickerView.clearSavedData();
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                if (first < 2) {
                    first++;
                    return;
                }
                int color = colorEnvelope.getColor();
                int[] color_array = new int[3];
                // 调用调色
                if (-1 != color) {
                    // 调色
                    color_array[0] = (color & 0xff0000) >> 16;
                    color_array[1] = (color & 0x00ff00) >> 8;
                    color_array[2] = (color & 0x0000ff);
                    changeColor(color_array);
                } else {
                    return;
                }
            }
        });
    }
}