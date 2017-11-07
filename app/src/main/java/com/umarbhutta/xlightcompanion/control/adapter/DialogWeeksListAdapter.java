package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.control.bean.SelectTime;
import com.umarbhutta.xlightcompanion.control.bean.SelectWeek;

import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 * 选择星期几。多选
 */

public class DialogWeeksListAdapter extends BaseAdapter {

    private Context mActivity;
    private List<SelectWeek> mSettingStr;
    private LayoutInflater inflate;

    public DialogWeeksListAdapter(Context activity, List<SelectWeek> settingStr) {
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
            convertView =inflate.inflate(R.layout.dialog_weeks_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_setting_name = (TextView) convertView.findViewById(R.id.tv_setting_name);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SelectWeek selectTime = mSettingStr.get(position);
        holder.tv_setting_name.setText(selectTime.name);
        if(selectTime.isSelect){
            holder.iv_select.setVisibility(View.VISIBLE);
        }else{
            holder.iv_select.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView tv_setting_name;
        private ImageView iv_select;
    }
}
