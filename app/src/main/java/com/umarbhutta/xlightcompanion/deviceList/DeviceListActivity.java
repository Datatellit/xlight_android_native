package com.umarbhutta.xlightcompanion.deviceList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.adapter.DeviceListAdapter;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSettingMainDevice;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestUnBindDevice;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;
import com.umarbhutta.xlightcompanion.share.ShareDeviceActivity;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;

/**
 * Created by Administrator on 2017/3/4.
 * 选择主设备列表
 */

public class DeviceListActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;
    private ListView listView;
    public static int selectPosition = 0;
    private DeviceListAdapter adapter;

    private TextView tv_select_main_device;
    private TextView tv_no_device;
    private View view_top_line, view_bottom_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
//        getSupportActionBar().hide();
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setVisibility(View.INVISIBLE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.select_main_device);
        listView = (ListView) findViewById(R.id.lv_devices);
        tv_select_main_device = (TextView) findViewById(R.id.tv_select_main_device);
        tv_no_device = (TextView) findViewById(R.id.tv_no_device);
        view_top_line = findViewById(R.id.view_top_line);
        view_bottom_line = findViewById(R.id.view_bottom_line);
        if (GlanceMainFragment.devicenodes != null && GlanceMainFragment.devicenodes.size() > 0) {
            tv_no_device.setVisibility(View.GONE);
            tv_select_main_device.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            adapter = new DeviceListAdapter(this, GlanceMainFragment.deviceList);
            listView.setAdapter(adapter);
            adapter.setOnClickCallBack(new DeviceListAdapter.OnClickCallBack() {
                @Override
                public void onClickCallBack(int position) {
                    selectPosition = position;
                    setMainDevice(position);
                }
            });
            adapter.setOnShareClickCallBack(new DeviceListAdapter.OnShareClickCallBack() {
                @Override
                public void OnShareClickCallBack(int position) {
                    //分享功能
                    if (UserUtils.isLogin(getApplicationContext())) {
                        //打开分享页面
                        Intent intent = new Intent(getApplicationContext(), ShareDeviceActivity.class);
                        intent.putExtra("deviceId", GlanceMainFragment.deviceList.get(position).id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
            });
            listView.setOnItemLongClickListener(this);
            view_top_line.setVisibility(View.VISIBLE);
            view_bottom_line.setVisibility(View.VISIBLE);
        } else {
            tv_no_device.setVisibility(View.VISIBLE);
            tv_select_main_device.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            view_top_line.setVisibility(View.GONE);
            view_bottom_line.setVisibility(View.GONE);
        }


        if (null == GlanceMainFragment.deviceList || GlanceMainFragment.deviceList.size() <= 0) {
//            ToastUtil.showToast(this, R.string.you_have_no_device_and_add);
        }

        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(R.id.ll_top_edit).init();
    }

    //设置主设备
    private void setMainDevice(int position) {
        if (GlanceMainFragment.deviceList.get(position).isShare == 1) {
            ToastUtil.showToast(this, R.string.main_device_share);
            return;
        }
        if (GlanceMainFragment.deviceList.get(position).maindevice == 1) {
            return;
        }
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }
        showProgressDialog(getString(R.string.setting));

        int deviceId = GlanceMainFragment.deviceList.get(position).id;
        RequestSettingMainDevice.getInstance().settingDevice(this, 1, deviceId, new CommentRequestCallback() {
            @Override
            public void onCommentRequestCallbackSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDeviceListInfo();
                        cancelProgressDialog();
                        ToastUtil.showToast(DeviceListActivity.this, getString(R.string.setting_success));
                    }
                });
            }

            @Override
            public void onCommentRequestCallbackFail(int code, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgressDialog();
                        ToastUtil.showToast(DeviceListActivity.this, errMsg);
                    }
                });
            }
        });
    }

    private void updateDeviceListInfo() {
        for (int i = 0; i < GlanceMainFragment.deviceList.size(); i++) {
            GlanceMainFragment.deviceList.get(i).maindevice = 0;
        }
        GlanceMainFragment.deviceList.get(selectPosition).maindevice = 1;
        adapter.notifyDataSetChanged();
    }


    /**
     * 弹出解绑设备确认框
     */
    private void showDeleteSceneDialog(final int position) {
        AlertDialog mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.unbind_device_tap))
                .setMessage(getString(R.string.sure_unbind_device))
                .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unBindDevice(position);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        Button btn1 = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    /**
     * 解绑设备
     *
     * @param position
     */
    private void unBindDevice(final int position) {
        if (!NetworkUtils.isNetworkAvaliable(this)) {
            ToastUtil.showToast(this, R.string.net_error);
            return;
        }

        showProgressDialog(getString(R.string.setting));

        RequestUnBindDevice.getInstance().unBindController(this, "" + GlanceMainFragment.deviceList.get(position).id,
                new CommentRequestCallback() {
                    @Override
                    public void onCommentRequestCallbackSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelProgressDialog();
                                ToastUtil.showToast(DeviceListActivity.this, getString(R.string.unbind_sucess));
                                Rows r = GlanceMainFragment.deviceList.get(position);
                                for (int i = 0; i < GlanceMainFragment.devicenodes.size(); ) {
                                    if (GlanceMainFragment.devicenodes.get(i).coreid == r.coreid) {
                                        GlanceMainFragment.devicenodes.remove(i);
                                    } else {
                                        i++;
                                    }
                                }
                                GlanceMainFragment.deviceList.remove(position);
                                //移除对应的node
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onCommentRequestCallbackFail(int code, final String errMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelProgressDialog();
                                ToastUtil.showToast(DeviceListActivity.this, getString(R.string.unbind_fail));
                            }
                        });
                    }
                });
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (GlanceMainFragment.deviceList.get(position).isShare == 0)
            showDeleteSceneDialog(position);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
