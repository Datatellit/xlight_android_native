package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;

import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class DeviceNameAdapter extends BaseAdapter {

    private List<Devicenodes> mDevicenodes;
    private Context mActivity;
    private LayoutInflater layoutInflater;

    public DeviceNameAdapter(Context mActivity, List<Devicenodes> devicenodes) {
        this.mDevicenodes = devicenodes;
        this.mActivity = mActivity;
        layoutInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mDevicenodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevicenodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.control_device_name, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_device_name.setText(mDevicenodes.get(position).devicenodename);
        return convertView;
    }

    class ViewHolder {
        private TextView tv_device_name;
    }
}