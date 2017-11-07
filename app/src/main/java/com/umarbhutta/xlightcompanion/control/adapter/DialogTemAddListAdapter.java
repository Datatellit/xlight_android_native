package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.control.bean.Ruleconditions;

/**
 * Created by Administrator on 2017/3/5.
 */

public class DialogTemAddListAdapter extends BaseAdapter {

    private Context mActivity;
    private String[] mRuleconditions;
    private LayoutInflater inflate;

    public DialogTemAddListAdapter(Context activity, String[] ruleconditions) {
        this.mActivity = activity;
        this.mRuleconditions = ruleconditions;
        inflate = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mRuleconditions.length;
    }

    @Override
    public Object getItem(int position) {
        return  mRuleconditions[position];
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
            convertView =inflate.inflate(R.layout.dialog_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_setting_name = (TextView) convertView.findViewById(R.id.tv_setting_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String tem = mRuleconditions[position].substring(1);
        holder.tv_setting_name.setText(tem.substring(0,tem.length()-1));
        return convertView;
    }

    class ViewHolder {
        private TextView tv_setting_name;
    }
}
