package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.scenario.ScenarioListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guangbinw on 2017/3/23.
 * 设备列表页面
 */

public class DeviceListAdapter extends BaseAdapter {

    List<Rows> deviceList = new ArrayList<Rows>();
    private Context context;
    private final LayoutInflater inflater;
    private int index = 0;

    public DeviceListAdapter(Context context, List<Rows> deviceList) {
        this.deviceList = deviceList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class CustTask extends TimerTask {
        @Override
        public void run() {
            lvh.put(index, true);
        }
    }

    Timer timer;
    TimerTask tt;
    Map<Integer, Boolean> lvh = new HashMap<>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.main_devices_list_item, null);

            holder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.main_device = (TextView) convertView.findViewById(R.id.main_device);
            holder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.slItem);
            holder.ivShare = (ImageView) convertView.findViewById(R.id.ivShare);
            holder.ivIsShare = (ImageView) convertView.findViewById(R.id.ivIsShare);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rows deviceInfo = deviceList.get(position);
        holder.deviceName.setText(TextUtils.isEmpty(deviceInfo.devicename) ? context.getString(R.string.lamp) : deviceInfo.devicename);
        holder.main_device.setVisibility(0 == deviceInfo.maindevice || deviceInfo.isShare == 1 ? View.GONE : View.VISIBLE);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, deviceInfo.isShare == 1 ? null : convertView.findViewById(R.id.bottom_wrapper));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, null);
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                lvh.put(position, false);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
                index = position;
                if (timer != null && tt != null) {
                    tt.cancel();
                    timer.cancel();
                }
                timer = new Timer();
                tt = new CustTask();
                timer.schedule(tt, 100);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
        final SwipeLayout swipeLayout = holder.swipeLayout;
        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入分享页面
                if (mOnShareClickCallBack != null) {
                    swipeLayout.close();
                    mOnShareClickCallBack.OnShareClickCallBack(position);
                }
            }
        });
        holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发外部Click事件，添加冗余时间200ms
                if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Close && (lvh.get(index) == null || (Boolean) lvh.get(index))) {
                    if (mOnClickCallBack != null) {
                        mOnClickCallBack.onClickCallBack(position);
                    }
                }
            }
        });
        if (deviceInfo.isShare == 1) {
            holder.ivIsShare.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsShare.setVisibility(View.GONE);
        }
        return convertView;
    }

    private DeviceListAdapter.OnClickCallBack mOnClickCallBack;

    public void setOnClickCallBack(DeviceListAdapter.OnClickCallBack mOnClickCallBack) {
        this.mOnClickCallBack = mOnClickCallBack;
    }

    public interface OnClickCallBack {
        void onClickCallBack(int position);
    }

    private DeviceListAdapter.OnShareClickCallBack mOnShareClickCallBack;

    public void setOnShareClickCallBack(DeviceListAdapter.OnShareClickCallBack mOnShareClickCallBack) {
        this.mOnShareClickCallBack = mOnShareClickCallBack;
    }

    public interface OnShareClickCallBack {
        void OnShareClickCallBack(int position);
    }

    class ViewHolder {
        public TextView deviceName;
        public TextView main_device;
        public SwipeLayout swipeLayout;
        public ImageView ivShare;
        public ImageView ivIsShare;
    }
}
