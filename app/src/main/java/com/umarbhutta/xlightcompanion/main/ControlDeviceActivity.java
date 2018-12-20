package com.umarbhutta.xlightcompanion.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.ScenariosResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestScenarios;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 * 设置灯
 */

public class ControlDeviceActivity extends BaseActivity {

    SlidingTabLayout tabLayout;
    private MyPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private LinearLayout llBack;
    private ImageView btnSure;
    private TextView tvTitle;
    public static Devicenodes devicenodes;
    private int mPositon;
    public static xltDevice mCurrentDevice;
    private Handler m_handlerControl;
    private String TAG = "XLight";
    private List<ScenariosResult> mScenarioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScenarioList();
        setContentView(R.layout.activity_control);
        mTitles = new String[]{
                getString(R.string.tab_1), getString(R.string.tab_2), getString(R.string.tab_5)
        };
        devicenodes = (Devicenodes) getIntent().getSerializableExtra("info");
        mPositon = getIntent().getIntExtra("position", 0);
        mCurrentDevice = SlidingMenuMainActivity.xltDeviceMaps.get(devicenodes.coreid);
        if (mCurrentDevice == null)
            return;
        if (mCurrentDevice.getEnableEventSendMessage()) {
            upUIDateAddHandler();
        } else {
            //打开
            mCurrentDevice.setEnableEventSendMessage(true);
            upUIDateAddHandler();
        }
        // 设置并查询
        mCurrentDevice.setDeviceID(devicenodes.nodeno);
        mCurrentDevice.QueryStatus();//查询所有的状态，通过handler或者广播发送过来。
        // 加载所有的fragment页面
        int index = 0;
        for (String title : mTitles) {
            if (index == 0) {
                mFragments.add(CCTFragment.getInstance(mScenarioList));
            } else if (index == 1) {
                mFragments.add(ColorFragment.getInstance());
            } else if (index == 2) {
                mFragments.add(DelayFragment.getInstance());
            } else if (index == 3) {
                mFragments.add(TimingFragment.getInstance());
            }
//            else if (index == 4) {
//                mFragments.add(DelayFragment.getInstance());
//            } else if (index == 5) {
//                mFragments.add(TimingFragment.getInstance());
//            }
            index++;
        }
        ViewPager vp = (ViewPager) findViewById(R.id.vp);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        tabLayout.setViewPager(vp);
        initViews();
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(R.id.ll_top_edit).init();
    }

    public void getScenarioList() {
        Log.e("CCTFragment", "Get ScenarioList");
        RequestScenarios.getInstance().getScenarioInfo(this, new RequestScenarios.OnRequestScenarioInfoCallback() {
            @Override
            public void onRequestScenarioInfoSuccess(final List<ScenariosResult> mScenarioResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScenarioList = mScenarioResult;
                        Log.e("CCTFragment", "Get ScenarioList:" + mScenarioList.size());
                        CCTFragment cctFragment = (CCTFragment) mFragments.get(0);
                        cctFragment.refreshScenario(mScenarioList);
                    }
                });
            }

            @Override
            public void onRequestScenarioInfoFail(int code, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("CCTFragment", "Get ScenarioList Error");
                        ToastUtil.showToast(getBaseContext(), R.string.net_error);
                    }
                });
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // EventBus.getDefault().unregister(this);
    }

//
//    @Override
//    public void onClick(View v) {
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        mCurrentDevice.removeDeviceEventHandler(m_handlerControl);
    }

    public void initViews() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同步灯具名称回去
                Intent intent = new Intent();
                intent.putExtra("devicenodes", devicenodes);
                setResult(2, intent);
                finish();
            }
        });
        findViewById(R.id.tvEditSure).setVisibility(View.GONE);
        btnSure = (ImageView) findViewById(R.id.btn_more);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到新的active，进行灯具信息设置
                Intent intent = new Intent(getBaseContext(), EditDeviceActivity.class);
                intent.putExtra("info", devicenodes);
                startActivityForResult(intent, 1);
            }
        });
        btnSure.setVisibility(View.VISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(devicenodes.devicenodename);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            devicenodes.devicenodename = data.getStringExtra("name");
            // 更新头部
            tvTitle.setText(devicenodes.devicenodename);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    //获取监听
    private void upUIDateAddHandler() {
        m_handlerControl = new Handler() {
            public void handleMessage(Message msg) {
                Logger.e(TAG, "handler_msg=" + msg.getData().toString());
                if (devicenodes.coreid.equals(msg.getData().getString("coreId")) && devicenodes.nodeno == msg.getData().getInt("nd", -1)) {
                    int intValue = msg.getData().getInt("State", -255);
                    if (intValue != -255) {
                        devicenodes.ison = intValue > 0 ? xltDevice.STATE_ON : xltDevice.STATE_OFF;
                    }

                    intValue = msg.getData().getInt("BR", -255);
                    if (intValue != -255) {
                        devicenodes.brightness = intValue;
                    }
                    intValue = msg.getData().getInt("CCT", -255);
                    if (intValue != -255) {
                        devicenodes.cct = intValue;
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
                        devicenodes.color = tmpColor;
                        int color = Color.rgb(R, G, B);
                        Log.d("XLight", "update auto color return");
                    }
                    intValue = msg.getData().getInt("filter", -255);
                    if (intValue != -255) {
                        devicenodes.filter = intValue;
                    }
                    GlanceMainFragment.devicenodes.remove(mPositon);
                    GlanceMainFragment.devicenodes.add(mPositon, devicenodes);
                    // 发送事件通知
                    EventBus.getDefault().post(devicenodes);
                }
            }
        };
        mCurrentDevice.addDeviceEventHandler(m_handlerControl);
    }
}
