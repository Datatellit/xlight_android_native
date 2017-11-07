package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleInfo;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;

/**
 * Created by luomengxin on 2017/4/23.
 */

public class NewRuleAdapter extends BaseAdapter {
    private Context context;
    private final LayoutInflater inflater;
    private NewRuleInfo mNewRuleInfo;
    /**
     * `
     * 0既没有执行条件，也没有执行结果，
     * 1有执行条件，没有执行结果
     * 2没有执行条件，有执行结果
     * 3有执行条件，也有执行结果
     */
    private int type;

    public NewRuleAdapter(Context context, NewRuleInfo mNewRuleInfo) {
        this.context = context;
        this.mNewRuleInfo = mNewRuleInfo;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int conditionSize = mNewRuleInfo.mNewRuleConditionInfoList.size();  //条件size
        int resultSize = mNewRuleInfo.mNewRuleResultInfoList.size();    //结果size


        if (resultSize > 0 && conditionSize > 0) {  //有执行条件，也有执行结果
            type = 3;
            return conditionSize + resultSize;
        } else if (conditionSize > 0 && resultSize <= 0) {  //有执行条件，没有执行结果
            type = 1;
            return conditionSize + 1;
        } else if (conditionSize <= 0 && resultSize > 0) {   //有执行结果，没有执行条件
            type = 2;
            return resultSize + 1;
        } else {//既没有执行条件，又没有执行结果
            type = 0;
            return 2;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_new_rule_item, null);

            holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.titleLayout);
            holder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
            holder.condidtion_tv = (TextView) convertView.findViewById(R.id.condidtion_tv);
            holder.ib_add_term = (ImageView) convertView.findViewById(R.id.ib_add_term);
            holder.no_condidtion = (TextView) convertView.findViewById(R.id.no_condidtion);
            holder.item_left_tv = (TextView) convertView.findViewById(R.id.item_left_tv);
            holder.ib_minus = (ImageButton) convertView.findViewById(R.id.ib_minus);
            holder.topLine = convertView.findViewById(R.id.topLine);
            holder.bottomLine = convertView.findViewById(R.id.bottomLine);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == getCount() - 1) {
            holder.bottomLine.setVisibility(View.VISIBLE);
        } else {
            holder.bottomLine.setVisibility(View.GONE);
        }

        NewRuleItemInfo mNewRuleItemInfo = null;
        if (type == 1) {

            if (position == 0) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.itemLayout.setVisibility(View.VISIBLE);
                holder.no_condidtion.setVisibility(View.GONE);
                holder.condidtion_tv.setText(R.string.manzu_tiaojian);
                mNewRuleItemInfo = mNewRuleInfo.mNewRuleConditionInfoList.get(0);
                holder.item_left_tv.setText(mNewRuleItemInfo.getShowText());
                holder.topLine.setVisibility(View.GONE);

            } else if (position == mNewRuleInfo.mNewRuleConditionInfoList.size()) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.itemLayout.setVisibility(View.GONE);
                holder.no_condidtion.setVisibility(View.VISIBLE);
                holder.condidtion_tv.setText(R.string.jiang_zhixing_jieguo);
                holder.no_condidtion.setText(R.string.wei_tianjia_jieguo);
                holder.topLine.setVisibility(View.VISIBLE);
            } else {

                holder.titleLayout.setVisibility(View.GONE);
                holder.itemLayout.setVisibility(View.VISIBLE);
                holder.no_condidtion.setVisibility(View.GONE);
                holder.condidtion_tv.setText(R.string.jiang_zhixing_jieguo);
                holder.no_condidtion.setText(R.string.wei_tianjia_jieguo);
                holder.topLine.setVisibility(View.GONE);

                mNewRuleItemInfo = mNewRuleInfo.mNewRuleConditionInfoList.get(position);
                holder.item_left_tv.setText(mNewRuleItemInfo.getShowText());
            }


        } else if (type == 2) {

            if (position == 0) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.itemLayout.setVisibility(View.GONE);
                holder.no_condidtion.setVisibility(View.VISIBLE);
                holder.condidtion_tv.setText(R.string.manzu_tiaojian);
                holder.no_condidtion.setText(R.string.wei_tianjia_tiaojian);
                holder.topLine.setVisibility(View.GONE);
            } else if (position == 1) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.itemLayout.setVisibility(View.VISIBLE);
                holder.no_condidtion.setVisibility(View.GONE);
                holder.condidtion_tv.setText(R.string.jiang_zhixing_jieguo);

                mNewRuleItemInfo = mNewRuleInfo.mNewRuleResultInfoList.get(0);

                holder.item_left_tv.setText(mNewRuleItemInfo.getShowText());
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.titleLayout.setVisibility(View.GONE);
                holder.itemLayout.setVisibility(View.VISIBLE);
                holder.no_condidtion.setVisibility(View.GONE);

                mNewRuleItemInfo = mNewRuleInfo.mNewRuleResultInfoList.get(position - 1);

                holder.item_left_tv.setText(mNewRuleItemInfo.getShowText());
                holder.topLine.setVisibility(View.GONE);

            }


        } else if (type == 3) {

            holder.no_condidtion.setVisibility(View.GONE);
            holder.itemLayout.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.condidtion_tv.setText(R.string.manzu_tiaojian);
                holder.topLine.setVisibility(View.GONE);
            } else if (position == mNewRuleInfo.mNewRuleConditionInfoList.size()) {
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.condidtion_tv.setText(R.string.jiang_zhixing_jieguo);
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.titleLayout.setVisibility(View.GONE);
                holder.topLine.setVisibility(View.GONE);
            }

            if (position < mNewRuleInfo.mNewRuleConditionInfoList.size()) {
                mNewRuleItemInfo = mNewRuleInfo.mNewRuleConditionInfoList.get(position);
            } else {
                mNewRuleItemInfo = mNewRuleInfo.mNewRuleResultInfoList.get(position - mNewRuleInfo.mNewRuleConditionInfoList.size());
            }

            holder.item_left_tv.setText(mNewRuleItemInfo.getShowText());

        } else {  //type == 0  执行条件和执行结果都为空
            holder.titleLayout.setVisibility(View.VISIBLE);
            holder.itemLayout.setVisibility(View.GONE);
            holder.no_condidtion.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.condidtion_tv.setText(R.string.manzu_tiaojian);
                holder.no_condidtion.setText(R.string.wei_tianjia_tiaojian);
                holder.topLine.setVisibility(View.GONE);
            } else {
                holder.condidtion_tv.setText(R.string.jiang_zhixing_jieguo);
                holder.no_condidtion.setText(R.string.wei_tianjia_jieguo);
                holder.topLine.setVisibility(View.VISIBLE);
            }
        }

        holder.ib_add_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickAddIconListener) {
                    if (position == 0) {
                        mOnClickAddIconListener.clickAddCondition();
                    } else {
                        mOnClickAddIconListener.clickAddResult();
                    }
                }
            }
        });

        holder.ib_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickAddIconListener) {

                    Logger.i("index", "position = " + position + ", size = " + mNewRuleInfo.mNewRuleConditionInfoList.size());


                    if (mNewRuleInfo.mNewRuleConditionInfoList.size() <= 0) {
                        mOnClickAddIconListener.clickDeleteIcon(false, position - mNewRuleInfo.mNewRuleConditionInfoList.size() - 1);
                        return;
                    }

                    if (position < mNewRuleInfo.mNewRuleConditionInfoList.size()) {
                        mOnClickAddIconListener.clickDeleteIcon(true, position);
                    } else {
                        mOnClickAddIconListener.clickDeleteIcon(false, position - mNewRuleInfo.mNewRuleConditionInfoList.size());
                    }
                }
            }
        });

        return convertView;
    }

    class ViewHolder {

        public View topLine;
        public View bottomLine;

        //title
        public LinearLayout titleLayout;
        //item
        public RelativeLayout itemLayout;

        //title满足条件时或者将执行结果
        public TextView condidtion_tv;

        //添加按钮
        public ImageView ib_add_term;

        //未添加条件或者未添加结果
        public TextView no_condidtion;


        //左侧的条件或者执行结果
        public TextView item_left_tv;
        //右侧的减号
        public ImageButton ib_minus;
    }

    public OnClickAddIconListener mOnClickAddIconListener;

    public void setOnClickAddIconListener(OnClickAddIconListener mOnClickAddIconListener) {
        this.mOnClickAddIconListener = mOnClickAddIconListener;
    }

    public interface OnClickAddIconListener {
        void clickAddCondition();

        void clickAddResult();

        /**
         * @param isCondition true为条件，false为结果
         * @param position
         */
        void clickDeleteIcon(boolean isCondition, int position);
    }

}
