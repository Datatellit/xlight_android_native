package com.umarbhutta.xlightcompanion.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;
import com.umarbhutta.xlightcompanion.views.BoxedVertical;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("ValidFragment")
public class CCTFragment extends Fragment implements View.OnClickListener {
    private BoxedVertical boxedVertical;
    private TextView text;
    private TextView txtCCT;
    private ImageView imgSwitch;
    private ImageView imgReading;
    private ImageView imgSleep;
    private ImageView imgGame;
    private ImageView imgAdd;
    private ImageView imgLess;
    private TextView txtBrightness;
    private BubbleSeekBar bubbleSeekBar;
    private RoundRelativeLayout rrlSlide;

    public static CCTFragment getInstance() {
        CCTFragment sf = new CCTFragment();
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
        if (v.getId() == R.id.imgSwitch) {
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cct, null);
        boxedVertical = (BoxedVertical) v.findViewById(R.id.boxed_vertical);
        text = (TextView) v.findViewById(R.id.txtSwitch);
        txtCCT = (TextView) v.findViewById(R.id.txtCCT);
        imgSwitch = (ImageView) v.findViewById(R.id.imgSwitch);
        imgSwitch.setOnClickListener(this);
        rrlSlide = (RoundRelativeLayout) v.findViewById(R.id.rrl_slide);
        boxedVertical.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                ControlDeviceActivity.devicenodes.cct = points;
                txtCCT.setText("" + ControlDeviceActivity.devicenodes.cct);
                //
                float rate = (points - 2700) / (float) 3800;
                int top = (int) (180 - 180 * rate);
                Log.e("XLight", points + " > " + rate + " > " + top + "");
                RoundRelativeLayout.LayoutParams params = (RoundRelativeLayout.LayoutParams) rrlSlide.getLayoutParams();
                params.setMargins(DisplayUtils.dip2px(getContext(), 40), DisplayUtils.dip2px(getContext(), top), 0, 0);
                rrlSlide.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                Log.e("XLight", "" + boxedPoints.getValue());
                ControlDeviceActivity.mCurrentDevice.ChangeCCT(boxedPoints.getValue());
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
            boxedVertical.setValue(ControlDeviceActivity.devicenodes.cct);
            txtCCT.setText("" + ControlDeviceActivity.devicenodes.cct);
            text.setText(ControlDeviceActivity.devicenodes.ison == 0 ? getString(R.string.off) : getString(R.string.on));
            imgSwitch.setImageResource(ControlDeviceActivity.devicenodes.ison == 0 ? R.drawable.close : R.drawable.kq);
        }
    }
}