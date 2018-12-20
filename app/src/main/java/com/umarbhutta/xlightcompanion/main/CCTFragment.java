package com.umarbhutta.xlightcompanion.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.adapter.ScenarioAdapter;
import com.umarbhutta.xlightcompanion.glance.LightItemAdapter;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.ScenariosResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestScenarios;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;
import com.umarbhutta.xlightcompanion.views.ArcSeekBar;
import com.umarbhutta.xlightcompanion.views.BoxedVertical;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class CCTFragment extends Fragment implements View.OnClickListener {
    private ArcSeekBar arcSeekBar;
    private TextView text;
    private TextView txtCCT;
    private ImageView imgSwitch;
    private TextView txtRefresh;
    private ImageView imgAdd;
    private ImageView imgLess;
    private TextView txtBrightness;
    private BubbleSeekBar bubbleSeekBar;
    private RoundRelativeLayout rrlSlide;
    private GridView gvScenario;
    private ScenarioAdapter scenarioAdapter;
    private List<ScenariosResult> mScenarioList;
    public static Map<String, Bitmap> gridviewBitmapCaches = new HashMap<String, Bitmap>();

    public static CCTFragment getInstance(List<ScenariosResult> scenarioList) {
        CCTFragment sf = new CCTFragment();
        sf.mScenarioList = scenarioList;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onStart() {
        super.onStart();
        // 加载数据
        getScenarioList();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void getScenarioList() {
        if (mScenarioList != null && mScenarioList.size() > 0) {
            Log.e("CCTFragment", "Get ScenarioList:" + mScenarioList.size());
            changeGridView(mScenarioList.size());
            scenarioAdapter = new ScenarioAdapter(getContext(), mScenarioList);
            gvScenario.setAdapter(scenarioAdapter);
            scenarioAdapter.notifyDataSetChanged();
            txtRefresh.setVisibility(View.GONE);
        } else {
            txtRefresh.setVisibility(View.VISIBLE);
        }
    }

    public void refreshScenario(List<ScenariosResult> scenariosResults) {
        this.mScenarioList = scenariosResults;
        getScenarioList();
        Log.e("XLight", "again refreshScenario");
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
        } else if (v.getId() == R.id.iv_add) {
            changeBR(ControlDeviceActivity.devicenodes.brightness + 10);
        } else if (v.getId() == R.id.iv_less) {
            changeBR(ControlDeviceActivity.devicenodes.brightness - 10);
        } else if (v.getId() == R.id.txtRefresh) {
            getScenarioList();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cct, null);
        arcSeekBar = (ArcSeekBar) v.findViewById(R.id.arc_seek_bar);
        text = (TextView) v.findViewById(R.id.txtSwitch);
        txtCCT = (TextView) v.findViewById(R.id.txtCCT);
        imgSwitch = (ImageView) v.findViewById(R.id.imgSwitch);
        imgSwitch.setOnClickListener(this);
        // rrlSlide = (RoundRelativeLayout) v.findViewById(R.id.rrl_slide);
        bubbleSeekBar = (BubbleSeekBar) v.findViewById(R.id.brightnessSeekBar);
        imgAdd = (ImageView) v.findViewById(R.id.iv_add);
        imgLess = (ImageView) v.findViewById(R.id.iv_less);
        txtBrightness = (TextView) v.findViewById(R.id.txtBrightness);
        imgAdd.setOnClickListener(this);
        imgLess.setOnClickListener(this);
        gvScenario = (GridView) v.findViewById(R.id.gvScenario);
        txtRefresh = (TextView) v.findViewById(R.id.txtRefresh);
        txtRefresh.setOnClickListener(this);
        arcSeekBar.setOnProgressChangeListener(new ArcSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(ArcSeekBar seekBar, int progress, boolean isUser) {
                ControlDeviceActivity.devicenodes.cct = progress;
                txtCCT.setText("" + ControlDeviceActivity.devicenodes.cct);
            }

            @Override
            public void onStartTrackingTouch(ArcSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(ArcSeekBar seekBar) {
                ControlDeviceActivity.mCurrentDevice.ChangeCCT(seekBar.getProgress());
            }
        });

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
        gvScenario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScenariosResult sr = mScenarioList.get(position);
                // 切换模式
                changeMode(sr);
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
            arcSeekBar.setProgress(ControlDeviceActivity.devicenodes.cct);
            txtCCT.setText("" + ControlDeviceActivity.devicenodes.cct);
            text.setText(ControlDeviceActivity.devicenodes.ison == 0 ? getString(R.string.off) : getString(R.string.on));
            imgSwitch.setImageResource(ControlDeviceActivity.devicenodes.ison == 0 ? R.drawable.close : R.drawable.kq);
            bubbleSeekBar.setProgress(ControlDeviceActivity.devicenodes.brightness);
            txtBrightness.setText("" + ControlDeviceActivity.devicenodes.brightness);
        }
    }

    public void changeMode(ScenariosResult sr) {
        // 进行赋值操作
        if (sr.rgb != 0) {
            ControlDeviceActivity.devicenodes.color[0] = (sr.rgb & 0xff0000) >> 16;
            ControlDeviceActivity.devicenodes.color[1] = (sr.rgb & 0x00ff00) >> 8;
            ControlDeviceActivity.devicenodes.color[2] = (sr.rgb & 0x0000ff);
            ControlDeviceActivity.mCurrentDevice.ChangeColor(xltDevice.RING_ID_ALL, true, sr.brightness > 0 ? sr.brightness : 80, 0, ControlDeviceActivity.devicenodes.color[0], ControlDeviceActivity.devicenodes.color[1], ControlDeviceActivity.devicenodes.color[2]);
        } else {
            if (sr.brightness != 0 && sr.filter != 5) {
                changeBR(sr.brightness);
            }
            if (sr.cct != 0) {
                ControlDeviceActivity.devicenodes.cct = sr.cct;
                ControlDeviceActivity.mCurrentDevice.ChangeCCT(sr.cct);
            }
        }
        if (sr.filter != 0) {
            if (sr.filter == 5) {
                ControlDeviceActivity.mCurrentDevice.SetSpecialEffect(sr.filter, new int[]{900, sr.brightness, 0});
                ToastUtil.showToast(this.getContext(), String.format(getString(R.string.sleep_notify).toString(), "15"));
            } else {
                ControlDeviceActivity.devicenodes.filter = sr.filter;
                ControlDeviceActivity.mCurrentDevice.SetSpecialEffect(sr.filter);
            }
        }
        updateDevice();
        // 提示
//        ToastUtil.showToast(getContext(),R.string.success);
    }

    /**
     * 将GridView改成单行横向布局
     */
    public void changeGridView(int size) {
        if (getContext() != null) {
            int max = 340;
            // item宽度
            int itemWidth = DisplayUtils.dip2px(getContext(), 68);
            if (size < 5) {
                itemWidth = DisplayUtils.dip2px(getContext(), (int) (max / size));
            }
            // item之间的间隔
            int itemPaddingH = 0;
            // 计算GridView宽度
            int gvWidth = size * (itemWidth + itemPaddingH);

            Log.e("XLight", "GridView width is :" + gvWidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            gvScenario.setLayoutParams(params);
            gvScenario.setColumnWidth(itemWidth);
            gvScenario.setHorizontalSpacing(itemPaddingH);
            gvScenario.setStretchMode(GridView.NO_STRETCH);
            gvScenario.setNumColumns(size);
        }
    }
}