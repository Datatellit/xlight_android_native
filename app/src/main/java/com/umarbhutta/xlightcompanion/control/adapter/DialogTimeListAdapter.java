package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class DialogTimeListAdapter extends BaseAdapter {

    private Context mActivity;
    private List<String> mSettingStr;
    private LayoutInflater inflate;

    public DialogTimeListAdapter(Context activity, List<String> settingStr) {
        this.mActivity = activity;
        this.mSettingStr = settingStr;
        inflate = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mSettingStr.size();
    }

    @Override
    public Object getItem(int position) {
        return mSettingStr.get(position);
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
            convertView =inflate.inflate(R.layout.dialog_time_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_setting_name = (TextView) convertView.findViewById(R.id.tv_setting_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_setting_name.setText(mSettingStr.get(position));
        return convertView;
    }

    class ViewHolder {
        private TextView tv_setting_name;
    }
}
