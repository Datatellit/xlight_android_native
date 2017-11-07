package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleconditions;

import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */

public class TermAdapter extends BaseAdapter {

    private Context mActivity;
    private List<Ruleconditions> ruleconditions;

    private LayoutInflater layoutInflater;

    public TermAdapter(Context activity, List<Ruleconditions> ruleconditions) {
        this.mActivity = activity;
        this.ruleconditions = ruleconditions;
        layoutInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return ruleconditions.size();
    }

    @Override
    public Object getItem(int position) {
        return ruleconditions.get(position);
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
            convertView = layoutInflater.inflate(R.layout.term_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_term = (TextView) convertView.findViewById(R.id.tv_term);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_term.setText(ruleconditions.get(position).weekdays);
        return convertView;
    }

    class ViewHolder {
        private TextView tv_term;
    }
}
