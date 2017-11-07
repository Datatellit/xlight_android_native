package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmdfield;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleActioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleactionnotify;
import com.umarbhutta.xlightcompanion.okHttp.model.Rulecondition;
import com.umarbhutta.xlightcompanion.okHttp.model.Ruleconditions;

import java.util.List;

/**
 * 规则适配器
 */
public class DeviceRulesListAdapter extends BaseAdapter {

    private LayoutInflater inflater;//这个一定要懂它的用法及作用
    private Context mActivity;
    private List<RuleInfo> mRuleInfoList;
    private Context context;

    public DeviceRulesListAdapter(Context activity, List<RuleInfo> mRuleInfoList) {
        this.context = activity;
        this.mActivity = activity;
        this.mRuleInfoList = mRuleInfoList;
        this.inflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mRuleInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRuleInfoList.get(position);
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
            convertView = inflater.inflate(R.layout.rules_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.mDeviceSwitch = (CheckBox) convertView.findViewById(R.id.deviceSwitch);
            holder.listViewTerm = (LinearLayout) convertView.findViewById(R.id.lv_term);
            holder.listViewResult = (LinearLayout) convertView.findViewById(R.id.lv_result);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mDeviceSwitch.setChecked((mRuleInfoList.get(position).status == 1) ? true : false);
        holder.mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mOnItemActionListener) {
                    mOnItemActionListener.onSwitchAction(position, isChecked);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnItemActionListener) {
                    mOnItemActionListener.onItemLongClick(position);
                }
                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemActionListener) {
                    mOnItemActionListener.OnItemClick(position);
                }
            }
        });

        conditionInfo(position, convertView, parent, holder);
        resultInfo(position, convertView, parent, holder);


        return convertView;
    }


    /**
     * 条件
     *
     * @param position
     * @param convertView
     * @param parent
     */
    private void conditionInfo(final int position, View convertView, ViewGroup parent, ViewHolder holder) {
        RuleInfo mRuleInfo = mRuleInfoList.get(position);
        holder.listViewTerm.removeAllViews();

        if (null != mRuleInfo && null != mRuleInfo.ruleconditions && mRuleInfo.ruleconditions.size() > 0) {
            for (Ruleconditions mRuleconditions : mRuleInfo.ruleconditions) {
                TextView textView = new TextView(mActivity);
//                textView.setSingleLine(true);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
//                textView.setText((getText(mRuleconditions.ruleconditionname) + getText(mRuleconditions.attribute) + getText(mRuleconditions.operator) + getText(mRuleconditions.rightValue)
//                        + getText(mRuleconditions.starttime) + getText(mRuleconditions.endtime) + getweekdays(mRuleconditions.weekdays) + getText(mRuleconditions.hour) + getText(mRuleconditions.minute)
//                        + getText(mRuleconditions.isrepeat)).trim());

                if (mRuleconditions.ruleconditiontype == 1) {//定时
                    textView.setText((getText(mRuleconditions.attribute) + getText(mRuleconditions.operator) + getText(mRuleconditions.rightValue)
                            + getText(mRuleconditions.starttime) + getText(mRuleconditions.endtime) + getweekdays(mRuleconditions.weekdays)
                            + (getText(mRuleconditions.hour).trim() + ":") + (getText(mRuleconditions.minute))
                    ).trim());
                } else if (mRuleconditions.ruleconditiontype == 2) {//亮度
                    textView.setText((getText(mRuleconditions.attribute)
                            + getText(mRuleconditions.operator)
                            + getText(mRuleconditions.rightValue)).trim());
                } else if (mRuleconditions.ruleconditiontype == 3) { //活动
                    textView.setText((getText(mRuleconditions.attribute)
                            + getText(mRuleconditions.operator)
                            + getText(mRuleconditions.rightValue)).trim());
                } else if (mRuleconditions.ruleconditiontype == 4) {//声音
                    textView.setText((getText(mRuleconditions.attribute)
                            + getText(mRuleconditions.operator)
                            + getText(mRuleconditions.rightValue)).trim());

                } else if (mRuleconditions.ruleconditiontype == 5) {//温度
                    textView.setText((getText(mRuleconditions.attribute)
                            + getText(mRuleconditions.operator)
                            + getText(mRuleconditions.rightValue)).trim());

                } else if (mRuleconditions.ruleconditiontype == 6) {//离家
                    textView.setText((getText(mRuleconditions.attribute)));

                } else if (mRuleconditions.ruleconditiontype == 7) {//回家
                    textView.setText((getText(mRuleconditions.attribute)));

                } else if (mRuleconditions.ruleconditiontype == 8) {//气体
                    textView.setText((getText(mRuleconditions.attribute) + getText(mRuleconditions.operator)
                            + getText(mRuleconditions.rightValue)));
                }

//                textView.setText((getText(mRuleconditions.attribute) + getText(mRuleconditions.operator) + getText(mRuleconditions.rightValue)
//                        + getText(mRuleconditions.starttime) + getText(mRuleconditions.endtime) + getweekdays(mRuleconditions.weekdays)
//                        + (getText(mRuleconditions.hour).trim() + ":") + (getText(mRuleconditions.minute))
//                        + getText(mRuleconditions.isrepeat)).trim());
                holder.listViewTerm.addView(textView);
            }
        }
    }

    /**
     * 执行
     *
     * @param position
     * @param convertView
     * @param parent
     */
    private void resultInfo(final int position, View convertView, ViewGroup parent, ViewHolder holder) {
        holder.listViewResult.removeAllViews();
        RuleInfo mRuleInfo = mRuleInfoList.get(position);

        if (null != mRuleInfo && null != mRuleInfo.ruleactionnotifies && mRuleInfo.ruleactionnotifies.size() > 0) {//通知
            for (Ruleactionnotify mRuleactionnotify : mRuleInfo.ruleactionnotifies) {
                TextView textView = new TextView(mActivity);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
//                textView.setSingleLine(true);

                if (1 == mRuleactionnotify.actiontype) { //emial通知
                    textView.setText(context.getString(R.string.email_notify) + "：" + (getText(mRuleactionnotify.subject)
                            + getText(mRuleactionnotify.content)
                            + getText(mRuleactionnotify.emailaddress)
                    ).trim());
                } else {  //APP通知
                    textView.setText("" + (getText(mRuleactionnotify.subject)
                            + getText(mRuleactionnotify.content)
                            + getText(mRuleactionnotify.emailaddress)
                    ).trim());
                }

                holder.listViewResult.addView(textView);
            }
        }

        if (null != mRuleInfo && null != mRuleInfo.ruleactioncmds && mRuleInfo.ruleactioncmds.size() > 0) {//灯或者场景
            for (RuleActioncmd action : mRuleInfo.ruleactioncmds) {

                TextView textView = new TextView(mActivity);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
//                textView.setSingleLine(true);

                if (null != action.actioncmdfields && action.actioncmdfields.size() > 0) {

                    if (1 == action.actiontype) { //灯
                        Actioncmdfield actioncmdfield = action.actioncmdfields.get(0);
                        textView.setText(context.getString(R.string.lamp_tool) + "：" + actioncmdfield.cmd + "  "
                                + (actioncmdfield.paralist.substring(1, actioncmdfield.paralist.trim().length() - 1)));
                    } else {//场景
                        Actioncmdfield actioncmdfield = action.actioncmdfields.get(0);
                        textView.setText(context.getString(R.string.scene) + "："
                                + (actioncmdfield.paralist.substring(1, actioncmdfield.paralist.trim().length() - 1)));
                    }
                }

                holder.listViewResult.addView(textView);

            }
        }


    }

    /**
     * [1,3,5]  返回 周一，周二
     *
     * @param weekdays
     * @return
     */
    private String getweekdays(String weekdays) {
        if (TextUtils.isEmpty(weekdays) || "null".equals(weekdays)) {
            return " ";
        }

        String mWeekDays = weekdays.replace("[", "").replace("]", "");
        String[] weekArrs = mWeekDays.split(",");
        StringBuilder builder = new StringBuilder();
        if (null != weekArrs && weekArrs.length > 0) {
            for (int i = 0; i < weekArrs.length; i++) {
                switch (weekArrs[i]) {
                    case "1":
                        builder.append(mActivity.getString(R.string.zhouyi) + ",");
                        break;
                    case "2":
                        builder.append(mActivity.getString(R.string.zhouer) + ",");
                        break;
                    case "3":
                        builder.append(mActivity.getString(R.string.zhousan) + ",");
                        break;
                    case "4":
                        builder.append(mActivity.getString(R.string.zhousi) + ",");
                        break;
                    case "5":
                        builder.append(mActivity.getString(R.string.zhouwu) + ",");
                        break;
                    case "6":
                        builder.append(mActivity.getString(R.string.zhouilu) + ",");
                        break;
                    default:
                        builder.append(mActivity.getString(R.string.zhouri) + ",");
                        break;
                }
            }
            return builder.toString().substring(0, builder.length() - 1) + " ";
        }

        return " ";

    }

    private String getText(String text) {
        if (TextUtils.isEmpty(text) || "null".equals(text)) {
            return " ";
        }

        return text + " ";
    }


    class ViewHolder {
        private CheckBox mDeviceSwitch;
        private LinearLayout listViewTerm, listViewResult;
    }

    private OnItemActionListener mOnItemActionListener;

    /**
     * 添加item事件回调
     *
     * @param mOnItemActionListener
     */
    public void addOnItemActionListener(OnItemActionListener mOnItemActionListener) {
        this.mOnItemActionListener = mOnItemActionListener;
    }

    public interface OnItemActionListener {
        /**
         * 规则开关执行
         *
         * @param isChecked
         */
        void onSwitchAction(int position, boolean isChecked);

        void onItemLongClick(int position);

        void OnItemClick(int position);
    }

}
