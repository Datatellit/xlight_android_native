package com.umarbhutta.xlightcompanion.rule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.MessageResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleItem;
import com.umarbhutta.xlightcompanion.okHttp.model.Rules;

import java.util.List;


public class RuleListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RuleInfo> ruleList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public RuleListAdapter(Context context, List<RuleInfo> ruleList) {
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
        final RuleInfo rule = ruleList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_rule_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.chkState = (CheckBox) convertView.findViewById(R.id.ruleSwitch);
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(rule.rulename);
        holder.chkState.setChecked(rule.status == 1 ? true : false);
        holder.chkState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mOnClickCallBack.onCheckedCallBack(position, isChecked);
                Log.e("XLight", "rule state change");
            }
        });
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickCallBack != null) {
                    mOnClickCallBack.onClickCallBack(position);
                }
            }
        });

        holder.llItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickCallBack != null) {
                    mOnLongClickCallBack.onLongClickCallBack(position);
                }
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView txtName;
        private CheckBox chkState;
        private LinearLayout llItem;
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

        void onCheckedCallBack(int position, boolean checked);
    }
}
