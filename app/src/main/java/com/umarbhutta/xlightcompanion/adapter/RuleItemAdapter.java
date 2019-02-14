package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.BitmapHelper;
import com.umarbhutta.xlightcompanion.main.CCTFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Condition;
import com.umarbhutta.xlightcompanion.okHttp.model.ScenariosResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/23.
 * 设备列表页面
 */

public class RuleItemAdapter extends BaseAdapter {

    List<Condition> conditionList = new ArrayList<Condition>();
    private Context context;
    private final LayoutInflater inflater;
    private int index = 0;

    public RuleItemAdapter(Context context, List<Condition> conditionList) {
        this.conditionList = conditionList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return conditionList.size();
    }

    @Override
    public Object getItem(int position) {
        return conditionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Condition condition = this.conditionList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.rule_list_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.ivLogo);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvRemark = (TextView) convertView.findViewById(R.id.tvRemark);
            holder.tvAfter = (TextView) convertView.findViewById(R.id.tvAfter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (condition.type.equals("time")) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ds_h));
        } else if (condition.type.equals("sensor")) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wd));
        } else if (condition.type.equals("light")) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wsd));
        }

        holder.tvName.setText(condition.name);
        holder.tvRemark.setText(condition.remark);
        if (condition.state != null && !condition.state.equals("")) {
            holder.tvAfter.setText(condition.state);
            holder.tvAfter.setVisibility(View.VISIBLE);
        } else {
            holder.tvAfter.setVisibility(View.GONE);
        }
        return convertView;
    }


    class ViewHolder {
        public TextView tvName;
        public TextView tvRemark;
        public ImageView icon;
        public TextView tvAfter;
    }
}