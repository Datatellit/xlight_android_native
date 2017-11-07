package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/23.
 * 设备列表页面
 */

public class DeviceListAdapter extends BaseAdapter {

    List<Rows> deviceList = new ArrayList<Rows>();
    private Context context;
    private final LayoutInflater inflater;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.main_devices_list_item, null);

            holder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.main_device = (TextView) convertView.findViewById(R.id.main_device);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rows deviceInfo = deviceList.get(position);
        holder.deviceName.setText(TextUtils.isEmpty(deviceInfo.devicename) ? context.getString(R.string.lamp) : deviceInfo.devicename);
        holder.main_device.setVisibility(0 == deviceInfo.maindevice ? View.GONE : View.VISIBLE);

        return convertView;
    }


    class ViewHolder {
        public TextView deviceName;
        public TextView main_device;
    }


}
