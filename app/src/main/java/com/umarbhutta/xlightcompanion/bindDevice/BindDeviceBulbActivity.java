package com.umarbhutta.xlightcompanion.bindDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.cardView.CardItem;
import com.umarbhutta.xlightcompanion.cardView.CardPagerAdapter;
import com.umarbhutta.xlightcompanion.cardView.ShadowTransformer;
import com.umarbhutta.xlightcompanion.help.DeviceInfo;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AddDeviceParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AddDeviceResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestAddDevice;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

/**
 * Created by 75932 on 2017/11/1.
 */

public class BindDeviceBulbActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private String coreID = null;
    private String deviceName = null;
    private int deviceType = 2;
    private ViewPager mViewPager;
    private int mainDevice = 1;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bind_device_select);

        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.select_blub_type);
        deviceName = getIntent().getStringExtra("deviceName");
        coreID = getIntent().getStringExtra("coreID");
        mainDevice = getIntent().getIntExtra("mainDevice", 1);
        Log.d("XLight", "coreID:" + coreID);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.drawable.rainbow));
        mCardAdapter.addCardItem(new CardItem(R.drawable.sunny));
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mCardShadowTransformer.enableScaling(true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 动态修改文案，动态修改图片
                if (position == 0) {
                    deviceType = 2;
                    ((TextView) findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.add_device_bulb_title_rainbow));
                    ((TextView) findViewById(R.id.txtDesc)).setText(getResources().getString(R.string.add_device_bulb_rainbow));
                } else if (position == 1) {
                    deviceType = 1;
                    ((TextView) findViewById(R.id.txtTitle)).setText(getResources().getString(R.string.add_device_bulb_title_sunny));
                    ((TextView) findViewById(R.id.txtDesc)).setText(getResources().getString(R.string.add_device_bulb_sunny));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void nextClick(View v) {
        ToastUtil.showLoading(this);
        AddDeviceParams device;
        // 收集信息
        if (UserUtils.isLogin(this)) {
            device = new AddDeviceParams(coreID, deviceName, UserUtils.getUserInfo(this).id, DeviceInfo.getSign(this), mainDevice, deviceType);
        } else {
            device = new AddDeviceParams(coreID, deviceName, DeviceInfo.getSign(this), mainDevice, deviceType);
        }
        Gson gson = new Gson();
        String paramStr = gson.toJson(device);
        RequestAddDevice.getInstance().addDevice(this, paramStr, new RequestAddDevice.OnAddDeviceCallBack() {
            @Override
            public void mOnAddDeviceCallBackFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(BindDeviceBulbActivity.this, R.string.add_device_bulb_bind);
                    }
                });
            }

            @Override
            public void mOnAddDeviceCallBackSuccess(final int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(BindDeviceBulbActivity.this, R.string.device_link_success);
                        Intent intent = new Intent(getApplicationContext(), SlidingMenuMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
