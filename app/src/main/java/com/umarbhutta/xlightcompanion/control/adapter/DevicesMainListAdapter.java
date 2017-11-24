package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.main.EditDeviceActivity;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.settings.SettingListAdapter;

import java.util.List;

import static com.umarbhutta.xlightcompanion.R.id.devicePlan;

/**
 */
public class DevicesMainListAdapter extends BaseAdapter {

    private Context mActivity;
    //    private List<Rows> deviceList;
    private List<Devicenodes> mDevicenodes;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public DevicesMainListAdapter(Context activity, List<Devicenodes> devicenodes) {
        this.mDevicenodes = devicenodes;
        this.mActivity = activity;
        this.inflater = LayoutInflater.from(mActivity);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Devicenodes devicenodes = mDevicenodes.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.devices_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.mDeviceName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.main_device = (TextView) convertView.findViewById(R.id.main_device);
            holder.devicePlan = (TextView) convertView.findViewById(devicePlan);
            holder.mDeviceSwitch = (CheckBox) convertView.findViewById(R.id.deviceSwitch);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.ivShare = (ImageView) convertView.findViewById(R.id.ivShare);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                if (null != mOnClickListener) {
                    mOnClickListener.onClickListener(position);
                }
            }
        });
        holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 长按事件  长按删除设备
                if (null != mOnSwitchStateChangeListener) {
                    mOnSwitchStateChangeListener.onLongClick(position);
                }
                return true;
            }
        });
        holder.mDeviceSwitch.setOnCheckedChangeListener(null);
        holder.mDeviceSwitch.setChecked(devicenodes.ison == 0 ? false : true);
        holder.mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //点击事件
                if (null != mOnSwitchStateChangeListener) {
                    Log.d("XLight", "trigger onchange event");
                    mOnSwitchStateChangeListener.onSwitchChange(position, isChecked);
                }
            }
        });

        holder.mDeviceName.setText(TextUtils.isEmpty(devicenodes.devicenodename) ? mActivity.getString(R.string.lamp) : devicenodes.devicenodename);
        holder.devicePlan.setText(devicenodes.devicetype > 1 ? "Rainbow" : "Sunny");
        if (devicenodes.isShare == 1) {
            holder.ivShare.setVisibility(View.VISIBLE);
        } else {
            holder.ivShare.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        private TextView mDeviceName, main_device, devicePlan;
        private CheckBox mDeviceSwitch;
        private LinearLayout ll_item;
        private ImageView ivShare;
    }

    private OnSwitchStateChangeListener mOnSwitchStateChangeListener;
    private OnClickListener mOnClickListener;

    /**
     * 设置item开关通知
     *
     * @param mOnSwitchStateChangeListener
     */
    public void setOnSwitchStateChangeListener(OnSwitchStateChangeListener mOnSwitchStateChangeListener) {
        this.mOnSwitchStateChangeListener = mOnSwitchStateChangeListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public interface OnSwitchStateChangeListener {
        void onLongClick(int position);

        void onSwitchChange(int position, boolean checked);
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }
}
