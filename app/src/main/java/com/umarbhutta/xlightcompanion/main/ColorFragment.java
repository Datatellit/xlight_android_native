package com.umarbhutta.xlightcompanion.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundRelativeLayout;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;
import com.umarbhutta.xlightcompanion.views.CircleImageView;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class ColorFragment extends Fragment implements View.OnClickListener {
    private TextView text;
    private ImageView imgSwitch;
    private RoundLinearLayout colorSelected;
    private ColorPickerView colorPickerView;
    private CircleImageView colorMore;
    private ArrayList<CircleImageView> lstFilter = new ArrayList<CircleImageView>();

    private TextView txtColor;
    private TextView txtFilter;
    private ImageView ivColor;
    private ImageView ivFilter;
    private LinearLayout llTabColor;
    private LinearLayout llTabFilter;
    private LinearLayout llColor;
    private LinearLayout llFilter;

    private ImageView imgAdd;
    private ImageView imgLess;
    private TextView txtBrightness;
    private BubbleSeekBar bubbleSeekBar;

    private ArrayList<RoundLinearLayout> lstColor = new ArrayList<RoundLinearLayout>();

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
            case R.id.ys_more:
                // 打开调色盘
                dialog.show();
                break;
            case R.id.tv_pink:
                changeBorder(v.getId());
                changeColor(ColorEnum.pink);
                break;
            case R.id.tv_purple:
                changeBorder(v.getId());
                changeColor(ColorEnum.purple);
                break;
            case R.id.tv_blue:
                changeBorder(v.getId());
                changeColor(ColorEnum.blue);
                break;
            case R.id.tv_green:
                changeBorder(v.getId());
                changeColor(ColorEnum.green);
                break;
            case R.id.tv_red:
                changeBorder(v.getId());
                changeColor(ColorEnum.red);
                break;
            case R.id.ll_tab_color:
                changeType(1);
                break;
            case R.id.ll_tab_filter:
                changeType(2);
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
            case R.id.iv_filter_5:
            case R.id.iv_filter_6:
                setEffect(0);
                break;
            case R.id.iv_add:
                changeBR(ControlDeviceActivity.devicenodes.brightness + 10);
                break;
            case R.id.iv_less:
                changeBR(ControlDeviceActivity.devicenodes.brightness - 10);
                break;
        }
    }

    public void changeBR(int br) {
        if (br > 100) {
            br = 100;
        } else if (br < 0) {
            br = 0;
        }
        ControlDeviceActivity.devicenodes.brightness = br;
        ControlDeviceActivity.mCurrentDevice.ChangeBrightness(ControlDeviceActivity.devicenodes.brightness);
        EventBus.getDefault().post(ControlDeviceActivity.devicenodes);
    }

    public void changeBorder(int resId) {
        if (resId == 0) {
            colorMore.setBorderWidth(DisplayUtils.dip2px(getContext(), 3));
        } else {
            colorMore.setBorderWidth(0);
        }
        for (RoundLinearLayout rll : lstColor) {
            if (rll.getId() == resId) {
                rll.getDelegate().setStrokeColor(getResources().getColor(R.color.dividerColor));
            } else {
                rll.getDelegate().setStrokeColor(rll.getDelegate().getBackgroundColor());
            }
        }
    }

    public void changeType(int type) {
        if (type == 1) {
            ivColor.setImageResource(R.drawable.qf_ysk);
            txtColor.setTextColor(getActivity().getResources().getColor(R.color.color_menu_text));
            ivFilter.setImageResource(R.drawable.qf_tx);
            txtFilter.setTextColor(getActivity().getResources().getColor(R.color.no_select));
            llColor.setVisibility(View.VISIBLE);
            llFilter.setVisibility(View.GONE);
            // 向左边移入
            llColor.setAnimation(AnimationUtils.makeInAnimation(getContext(), false));
            // 向左边移出
//            llFilter.setAnimation(AnimationUtils.makeOutAnimation(getContext(), false));
        } else {
            ivColor.setImageResource(R.drawable.qf_ys);
            txtColor.setTextColor(getActivity().getResources().getColor(R.color.no_select));
            ivFilter.setImageResource(R.drawable.qf_txk);
            txtFilter.setTextColor(getActivity().getResources().getColor(R.color.color_menu_text));
            llColor.setVisibility(View.GONE);
            llFilter.setVisibility(View.VISIBLE);
            // 向右边移出
//            llColor.setAnimation(AnimationUtils.makeOutAnimation(getContext(), true));
            // 向右边移入
            llFilter.setAnimation(AnimationUtils.makeInAnimation(getContext(), true));
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
        txtColor = (TextView) v.findViewById(R.id.txtColor);
        txtFilter = (TextView) v.findViewById(R.id.txtFilter);
        ivColor = (ImageView) v.findViewById(R.id.iv_color);
        ivFilter = (ImageView) v.findViewById(R.id.iv_filter);
        llTabColor = (LinearLayout) v.findViewById(R.id.ll_tab_color);
        llTabFilter = (LinearLayout) v.findViewById(R.id.ll_tab_filter);
        llColor = (LinearLayout) v.findViewById(R.id.ll_color);
        llFilter = (LinearLayout) v.findViewById(R.id.ll_filter);

        lstColor.add((RoundLinearLayout) v.findViewById(R.id.tv_pink));
        lstColor.add((RoundLinearLayout) v.findViewById(R.id.tv_blue));
        lstColor.add((RoundLinearLayout) v.findViewById(R.id.tv_red));
        lstColor.add((RoundLinearLayout) v.findViewById(R.id.tv_purple));
        lstColor.add((RoundLinearLayout) v.findViewById(R.id.tv_green));
        for (RoundLinearLayout rrl : lstColor) {
            rrl.setOnClickListener(this);
        }
        colorMore = (CircleImageView) v.findViewById(R.id.ys_more);
        colorMore.setOnClickListener(this);

        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_1));
        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_2));
        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_3));
        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_4));
        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_5));
        lstFilter.add((CircleImageView) v.findViewById(R.id.iv_filter_6));

        for (CircleImageView civ : lstFilter) {
            civ.setOnClickListener(this);
        }

        bubbleSeekBar = (BubbleSeekBar) v.findViewById(R.id.brightnessSeekBar);
        imgAdd = (ImageView) v.findViewById(R.id.iv_add);
        imgLess = (ImageView) v.findViewById(R.id.iv_less);
        txtBrightness = (TextView) v.findViewById(R.id.txtBrightness);
        imgAdd.setOnClickListener(this);
        imgLess.setOnClickListener(this);

        llTabFilter.setOnClickListener(this);
        llTabColor.setOnClickListener(this);
        builder = new AlertDialog.Builder(this.getContext());
        dialog = builder.create();
        initDialog();

        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.onProgressChanged(bubbleSeekBar, progress, progressFloat);
                txtBrightness.setText("" + progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat);
                changeBR(progress);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.getProgressOnFinally(bubbleSeekBar, progress, progressFloat);
            }
        });
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
                colorSelected.getDelegate().setBackgroundColor(color);

            }
            if (ControlDeviceActivity.devicenodes.filter != 0) {
                // 清除所有选中
                for (CircleImageView iv : lstFilter) {
                    iv.setBorderWidth(0);
                }
                // 设置选中的
                switch (ControlDeviceActivity.devicenodes.filter) {
                    case 1:
                        lstFilter.get(0).setBorderWidth(DisplayUtils.dip2px(getContext(), 3));
                        break;
                    case 2:
                        lstFilter.get(1).setBorderWidth(DisplayUtils.dip2px(getContext(), 3));
                        break;
                    case 3:
                        lstFilter.get(2).setBorderWidth(DisplayUtils.dip2px(getContext(), 3));
                        break;
                    case 4:
                        lstFilter.get(3).setBorderWidth(DisplayUtils.dip2px(getContext(), 3));
                        break;
                }
            }
            text.setText(ControlDeviceActivity.devicenodes.ison == 0 ? getString(R.string.off) : getString(R.string.on));
            imgSwitch.setImageResource(ControlDeviceActivity.devicenodes.ison == 0 ? R.drawable.close : R.drawable.kq);
            bubbleSeekBar.setProgress(ControlDeviceActivity.devicenodes.brightness);
            txtBrightness.setText("" + ControlDeviceActivity.devicenodes.brightness);
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
                    changeBorder(0);
                    changeColor(color_array);
                } else {
                    return;
                }
            }
        });
    }
}