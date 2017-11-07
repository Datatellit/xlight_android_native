package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleActioncmd;

import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */

public class ResultAdapter extends BaseAdapter {

    private Context mActivity;
    private List<RuleActioncmd> ruleactioncmds;
    private LayoutInflater layoutInflater;

    public ResultAdapter(Context activity,List<RuleActioncmd> ruleactioncmds) {
        this.mActivity = activity;
        this.ruleactioncmds = ruleactioncmds;
        layoutInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return ruleactioncmds.size();
    }

    @Override
    public Object getItem(int position) {
        return ruleactioncmds.get(position);
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
            convertView = layoutInflater.inflate(R.layout.result_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_result.setText(ruleactioncmds.get(position).ruleId+"");
        return convertView;
    }

    class ViewHolder {
        private TextView tv_result;
    }
}
