package com.umarbhutta.xlightcompanion.rule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.MessageResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleItem;

import java.util.List;


public class RuleListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RuleItem> ruleList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public RuleListAdapter(Context context, List<RuleItem> ruleList) {
        this.ruleList = ruleList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return ruleList.size();
    }

    @Override
    public Object getItem(int position) {
        return ruleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RuleItem rule = ruleList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_rule_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.chkState = (CheckBox) convertView.findViewById(R.id.ruleSwitch);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(rule.name);
        holder.chkState.setChecked(rule.state);
        holder.chkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("XLight", "rule state change");
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView txtName;
        private CheckBox chkState;
    }


    private OnLongClickCallBack mOnLongClickCallBack;

    public void setOnLongClickCallBack(OnLongClickCallBack mOnLongClickCallBack) {
        this.mOnLongClickCallBack = mOnLongClickCallBack;
    }

    public interface OnLongClickCallBack {
        void onLongClickCallBack(int position);
    }

    private OnClickCallBack mOnClickCallBack;

    public void setOnClickCallBack(OnClickCallBack mOnClickCallBack) {
        this.mOnClickCallBack = mOnClickCallBack;
    }

    public interface OnClickCallBack {
        void onClickCallBack(int position);
    }
}
