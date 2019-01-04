package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
    private OnClickListener mOnClickListener;

    public enum CLICK_TYPE {
        SWITCH, LOW, NORMAL, HIGH, MORE, WARM, COOL
    }

    public interface OnClickListener {
        void onClickListener(CLICK_TYPE type, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public LightItemAdapter(Context context, List<Devicenodes> deviceList) {
        this.deviceList = deviceList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new LightItemAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.light_item_big, null);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.chkSwitch = (CheckBox) convertView.findViewById(R.id.chkSwitch);
            holder.ll_br_low = (LinearLayout) convertView.findViewById(R.id.ll_br_low);
            holder.ll_br_normal = (LinearLayout) convertView.findViewById(R.id.ll_br_normal);
            holder.ll_br_high = (LinearLayout) convertView.findViewById(R.id.ll_br_high);
            holder.ll_cct_cool = (LinearLayout) convertView.findViewById(R.id.ll_cct_cool);
            holder.ll_cct_warm = (LinearLayout) convertView.findViewById(R.id.ll_cct_warm);
            holder.ll_more = (LinearLayout) convertView.findViewById(R.id.ll_more);
            holder.iv_br_low = (ImageView) convertView.findViewById(R.id.iv_br_low);
            holder.iv_br_normal = (ImageView) convertView.findViewById(R.id.iv_br_normal);
            holder.iv_br_high = (ImageView) convertView.findViewById(R.id.iv_br_high);
            holder.iv_cct_cool = (ImageView) convertView.findViewById(R.id.iv_cct_cool);
            holder.iv_cct_warm = (ImageView) convertView.findViewById(R.id.iv_cct_warm);
            convertView.setTag(holder);
        } else {
            holder = (LightItemAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(device.devicenodename);
        holder.chkSwitch.setChecked(device.ison == 0 ? false : true);
        holder.chkSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClickListener(CLICK_TYPE.SWITCH, position);
                }
            }
        });
        holder.ll_br_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    holder.iv_br_low.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_low_checked));
                    holder.iv_br_high.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_high));
                    holder.iv_br_normal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_normal));
                    mOnClickListener.onClickListener(CLICK_TYPE.LOW, position);
                }
            }
        });
        holder.ll_br_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    holder.iv_br_low.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_low));
                    holder.iv_br_high.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_high));
                    holder.iv_br_normal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_normal_checked));
                    mOnClickListener.onClickListener(CLICK_TYPE.NORMAL, position);
                }
            }
        });
        holder.ll_br_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    holder.iv_br_low.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_low));
                    holder.iv_br_high.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_high_checked));
                    holder.iv_br_normal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.br_normal));
                    mOnClickListener.onClickListener(CLICK_TYPE.HIGH, position);
                }
            }
        });
        holder.ll_cct_warm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    holder.iv_cct_cool.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cool));
                    holder.iv_cct_warm.setImageDrawable(mContext.getResources().getDrawable(R.drawable.warm_checked));
                    mOnClickListener.onClickListener(CLICK_TYPE.WARM, position);
                }
            }
        });
        holder.ll_cct_cool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    holder.iv_cct_cool.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cool_checked));
                    holder.iv_cct_warm.setImageDrawable(mContext.getResources().getDrawable(R.drawable.warm));
                    mOnClickListener.onClickListener(CLICK_TYPE.COOL, position);
                }
            }
        });
        holder.ll_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClickListener(CLICK_TYPE.MORE, position);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {

        private TextView txtName;

        private CheckBox chkSwitch;

        private LinearLayout ll_br_low;
        private LinearLayout ll_br_normal;
        private LinearLayout ll_br_high;
        private LinearLayout ll_cct_warm;
        private LinearLayout ll_cct_cool;
        private LinearLayout ll_more;

        private ImageView iv_br_low;
        private ImageView iv_br_normal;
        private ImageView iv_br_high;

        private ImageView iv_cct_warm;
        private ImageView iv_cct_cool;

    }
}
