package com.umarbhutta.xlightcompanion.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class ShakeAdapter extends BaseAdapter{

    private LayoutInflater inflater;//这个一定要懂它的用法及作用
    private List<String> mShakes;
    private Context mActivity;
    public ShakeAdapter(Context activity, List<String> shake){
        this.mShakes = shake;
        this.mActivity = activity;
        this.inflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mShakes.size();
    }

    @Override
    public Object getItem(int position) {
        return mShakes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.shake_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
            holder.shakeSwitch = (Switch) convertView.findViewById(R.id.shake_switch);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch (position){
            case 0:
                holder.tv_name.setText(R.string.device_select_device);
                holder.tvMsg.setVisibility(View.VISIBLE);
                holder.shakeSwitch.setVisibility(View.GONE);
                break;
            case 1:
                holder.tv_name.setText(R.string.kaiguan_switch);
                holder.tvMsg.setVisibility(View.GONE);
                holder.shakeSwitch.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.tv_name.setText(R.string.scene_switch);
                holder.tvMsg.setVisibility(View.GONE);
                holder.shakeSwitch.setVisibility(View.VISIBLE);
                break;
        }
        //如果设置了回调，就设置点击事件
        if (mOnItemClickListener != null) {
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tv_name,tvMsg;
        private Switch shakeSwitch;
        private LinearLayout ll_item;
    }

    /** * ItemClick的回调接口 */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
