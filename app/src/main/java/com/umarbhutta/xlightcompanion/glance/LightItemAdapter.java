package com.umarbhutta.xlightcompanion.glance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;

import java.util.List;

/**
 * Created by 75932 on 2017/11/20.
 */

public class LightItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<Devicenodes> deviceList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用
    private boolean support = false;
    private OnClickListener mOnClickListener;

    public enum CLICK_TYPE {
        TOP, BOTTOM
    }

    public interface OnClickListener {
        void onClickListener(CLICK_TYPE type, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public LightItemAdapter(Context context, List<Devicenodes> deviceList, boolean supportEvent) {
        this.deviceList = deviceList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.support = supportEvent;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Devicenodes device = deviceList.get(position);
        LightItemAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new LightItemAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.light_item, null);
            //通过上面layout得到的view来获取里面的具体控件
//            holder.deviceIcon = (ImageView) convertView.findViewById(R.id.imgLight);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.ll_top = (LinearLayout) convertView.findViewById(R.id.ll_top);
            holder.deviceName = (TextView) convertView.findViewById(R.id.txtName);
            holder.brightness = (TextView) convertView.findViewById(R.id.txtBrightness);
            holder.state = (TextView) convertView.findViewById(R.id.txtSwitch);
            holder.ll_switch = (RoundLinearLayout) convertView.findViewById(R.id.ll_switch);
            convertView.setTag(holder);
        } else {
            holder = (LightItemAdapter.ViewHolder) convertView.getTag();
        }

        holder.deviceName.setText(device.devicenodename);
//        holder.deviceIcon.setImageResource(device.devicetype > 1 ? R.drawable.rainbow : R.drawable.sunny);
        holder.brightness.setText(device.brightness + "%");
        if (support) {
            if (device.ison == 0) {
                holder.brightness.setVisibility(View.GONE);
                holder.ll_item.setBackgroundResource(R.drawable.layout_raiuds_close);
                holder.state.setText(R.string.off);
                holder.state.setVisibility(View.VISIBLE);
                holder.ll_switch.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.color_light_close));
            } else {
                holder.brightness.setVisibility(View.VISIBLE);
                holder.ll_item.setBackgroundResource(R.drawable.shadow_radius);
                holder.state.setVisibility(View.GONE);
                holder.ll_switch.getDelegate().setBackgroundColor(mContext.getResources().getColor(R.color.color_light_open));
            }
        }
        holder.ll_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClickListener(CLICK_TYPE.BOTTOM, position);
                }
            }
        });
        holder.ll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClickListener(CLICK_TYPE.TOP, position);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        private ImageView deviceIcon;

        private TextView deviceName;

        private LinearLayout ll_item;

        private LinearLayout ll_top;

        private TextView brightness;

        private TextView state;

        private RoundLinearLayout ll_switch;
    }
}
