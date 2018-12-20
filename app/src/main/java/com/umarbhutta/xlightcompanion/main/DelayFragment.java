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
import com.umarbhutta.xlightcompanion.views.CircleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

@SuppressLint("ValidFragment")
public class DelayFragment extends Fragment implements View.OnClickListener {

    private CircleSeekBar seekBar;
    private TextView tvMinute;
    private TextView txtSwitch;
    private ImageView imgSwitch;

    public static DelayFragment getInstance() {
        DelayFragment sf = new DelayFragment();
        return sf;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgSwitch) {
            if (seekBar.getCurProcess() < 1) {
                ToastUtil.showToast(this.getContext(), R.string.delay_0);
                return;
            }
            imgSwitch.setImageResource(R.drawable.kq);
            txtSwitch.setText(R.string.enabled);
            ControlDeviceActivity.mCurrentDevice.SetSpecialEffect(5, new int[]{seekBar.getCurProcess() * 60, ControlDeviceActivity.devicenodes.brightness, 0});
            ToastUtil.showToast(this.getContext(), String.format(getString(R.string.sleep_notify).toString(), tvMinute.getText().toString()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delay, null);
        seekBar = (CircleSeekBar) v.findViewById(R.id.seekbar);
        tvMinute = (TextView) v.findViewById(R.id.tvMinute);
        imgSwitch = (ImageView) v.findViewById(R.id.imgSwitch);
        txtSwitch = (TextView) v.findViewById(R.id.txtSwitch);
        imgSwitch.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                tvMinute.setText(curValue + "");
            }
        });
        return v;
    }
}