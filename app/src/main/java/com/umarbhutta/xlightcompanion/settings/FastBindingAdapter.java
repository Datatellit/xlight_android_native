package com.umarbhutta.xlightcompanion.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;

import java.util.List;

import static com.umarbhutta.xlightcompanion.R.id.ll_item;

/**
 * Created by Administrator on 2017/3/5.
 */

public class FastBindingAdapter extends BaseAdapter {


    private List<String> mFaseBings;
    private Context mActivity;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public FastBindingAdapter(Context activity, List<String> faseBings) {
        this.mFaseBings = faseBings;
        this.mActivity = activity;
        this.inflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mFaseBings.size();
    }

    @Override
    public Object getItem(int position) {
        return mFaseBings.get(position);
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
            convertView = inflater.inflate(R.layout.fase_bing_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.tv_fast_bind = (TextView) convertView.findViewById(R.id.tv_fast_bind);
            holder.ll_item = (LinearLayout) convertView.findViewById(ll_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_fast_bind.setText(mFaseBings.get(position));
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
        private TextView tv_fast_bind;
        private LinearLayout ll_item;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private FastBindingAdapter.OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(FastBindingAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


}
