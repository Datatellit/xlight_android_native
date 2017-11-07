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

public class DialogListAdapter extends BaseAdapter {

    private Context mActivity;
    private Ruleconditions mRuleconditions;
    private LayoutInflater inflate;
    private int mType;

    public DialogListAdapter(Context activity, Ruleconditions ruleconditions,int type) {
        this.mActivity = activity;
        this.mRuleconditions = ruleconditions;
        this.mType = type;
        inflate = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        int size = 0;
        switch (mType){// 0是定时 1是亮度 2是活动，3是声音，4是温度 5是离家，6是回家，7是气体 8是大于，小于，等于 ，9是温度
            case 1:
                size = 100;
                break;
            case 2:
                size = mRuleconditions.data.get(0).getActivities().size();
                break;
            case 3:
                size = mRuleconditions.data.get(0).getVoice().size();
                break;
            case 5:
                size = mRuleconditions.data.get(0).getLeavehome().size();
                break;
            case 6:
                size = mRuleconditions.data.get(0).getGohome().size();
                break;
            case 7:
                size = mRuleconditions.data.get(0).getGas().size();
                break;
            case 8:
                size = 3;
                break;
            case 12:
                size = 3;
                break;
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        Object obj = null;
        switch (mType){// 0是定时 1是亮度 2是活动，3是声音，4是温度 5是离家，6是回家，7是气体 8是大于，小于，等于 ，9是温度
            case 1:
                obj = position+1;
                break;
            case 2:
                obj = mRuleconditions.data.get(0).activities.get(position);
                break;
            case 3:
                obj = mRuleconditions.data.get(0).voice.get(position);
                break;
            case 5:
                obj = mRuleconditions.data.get(0).leavehome.get(position);
                break;
            case 6:
                obj = mRuleconditions.data.get(0).gohome.get(position);
                break;
            case 7:
                obj = mRuleconditions.data.get(0).gas.get(position);
                break;
            case 8:
                if(position==0){
                    obj =  ">";
                }else if(position==1){
                    obj =  "=";
                }else if(position==2){
                    obj =  "<";
                }
                break;
            case 12:
                if(position==0){
                    obj =  ">";
                }else if(position==1){
                    obj =  "=";
                }else if(position==2){
                    obj =  "<";
                }
                break;
        }
        return  obj ;
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
        switch (mType){// 0是定时 1是亮度 2是活动，3是声音，4是温度 5是离家，6是回家，7是气体 8是大于，小于，等于 ，9是温度
            case 1:
                holder.tv_setting_name.setText((position+1)+"");
                break;
            case 2:
                holder.tv_setting_name.setText(mRuleconditions.data.get(0).activities.get(position).name);
                break;
            case 3:
                holder.tv_setting_name.setText(mRuleconditions.data.get(0).voice.get(position).name);
                break;
            case 5:
                holder.tv_setting_name.setText(mRuleconditions.data.get(0).leavehome.get(position).name);
                break;
            case 6:
                holder.tv_setting_name.setText(mRuleconditions.data.get(0).gohome.get(position).name);
                break;
            case 7:
                holder.tv_setting_name.setText(mRuleconditions.data.get(0).gas.get(position).name);
                break;
            case 8:
                if(position==0){
                    holder.tv_setting_name.setText(">");
                }else if(position==1){
                    holder.tv_setting_name.setText("=");
                }else if(position==2){
                    holder.tv_setting_name.setText("<");
                }
                break;
            case 12:
                if(position==0){
                    holder.tv_setting_name.setText(">");
                }else if(position==1){
                    holder.tv_setting_name.setText("=");
                }else if(position==2){
                    holder.tv_setting_name.setText("<");
                }
                break;
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tv_setting_name;
    }
}
