package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/23.
 * wifi列表页面
 */

public class WifiListAdapter extends BaseAdapter {

    List<ScanResult> mScanResultList = new ArrayList<ScanResult>();
    private Context context;
    private final LayoutInflater inflater;

    public WifiListAdapter(Context context, List<ScanResult> mScanResultList) {
        this.mScanResultList = mScanResultList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mScanResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScanResultList.get(position);
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
            convertView = inflater.inflate(R.layout.layout_wifi_list_item, null);

            holder.ssid = (TextView) convertView.findViewById(R.id.ssid);
            holder.lockIcon = (ImageView) convertView.findViewById(R.id.lockIcon);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult wifiInfo = mScanResultList.get(position);
        holder.ssid.setText(wifiInfo.SSID);
//        holder.lockIcon.setVisibility(0 == deviceInfo.maindevice ? View.GONE : View.VISIBLE);

        return convertView;
    }


    class ViewHolder {
        public TextView ssid;
        public ImageView lockIcon;
    }


}
