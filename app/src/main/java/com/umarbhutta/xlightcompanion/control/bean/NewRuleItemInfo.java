package com.umarbhutta.xlightcompanion.control.bean;

import android.content.Context;
import android.text.TextUtils;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.StringUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.Tools.WeekUtils;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actionnotify;
import com.umarbhutta.xlightcompanion.okHttp.model.Condition;
import com.umarbhutta.xlightcompanion.okHttp.model.Schedule;

/**
 * Created by luomengxin on 2017/4/23.
 * 规则添加执行条件和结果
 */

public class NewRuleItemInfo {
    //条件
    private Schedule mSchedule;
    private Condition mCondition;

    //结果
    private Actioncmd mActioncmd;
    private Actionnotify mActionnotify;

//    private ControlRuleDevice mControlRuleDevice;//控制的结果

    /**
     * 用于显示添加规则页面的item
     */
    public String showText;

    public String getShowText() {
        return showText;
    }

    private void setShowText(String showText) {
        this.showText = showText;
    }

    public Schedule getmSchedule() {
        return mSchedule;
    }

    public void setmSchedule(Schedule mSchedule, Context context) {
        this.mSchedule = mSchedule;
        setShowText(WeekUtils.getweekdays(mSchedule.weekdays, context) + " " + mSchedule.hour + ":" + mSchedule.minute);
    }

    public Condition getmCondition() {
        return mCondition;
    }

    public void setmCondition(Condition condition) {
        this.mCondition = condition;

//       ruleconditiontype  亮度2、活动3、声音4、温度5、离家6、回家7、气体8


        if (6 == mCondition.ruleconditiontype || 7 == mCondition.ruleconditiontype) {
            setShowText(mCondition.attribute);
        } else if (2 == mCondition.ruleconditiontype || 5 == mCondition.ruleconditiontype
                || 3 == mCondition.ruleconditiontype || 4 == mCondition.ruleconditiontype
                || 8 == mCondition.ruleconditiontype) {
            setShowText(mCondition.attribute + "   " + mCondition.operator + "   " + mCondition.rightValue);
        } else {
            setShowText(mCondition.attribute + "   " + mCondition.operator + "   " + mCondition.rightValue);
        }
//        if (mCondition.conditionType == 5 || mCondition.conditionType == 6 || mCondition.conditionType == 7
//                || mCondition.conditionType == 2 || mCondition.conditionType == 3) {
//            setShowText(mCondition.ruleconditionname);
//        } else if (mCondition.conditionType == 8) {
////            setShowText(mCondition.attribute + "   " + mCondition.ruleconditionname + "   " + mCondition.temAbove + "   " + mCondition.rightValue);
//            setShowText(mCondition.attribute + "   " + mCondition.temAbove + "   " + mCondition.rightValue);
//        } else if (mCondition.conditionType == 9) {
//            setShowText(mCondition.attribute + "   " + mCondition.temAbove + "   " + mCondition.rightValue);
//        } else {
//            setShowText(mCondition.ruleconditionname + "   " + mCondition.operator + "   " + mCondition.rightValue);
//        }
    }

    public Actioncmd getmActioncmd() {
        return mActioncmd;
    }

    public void setmActioncmd(Actioncmd mActioncmd) {
        this.mActioncmd = mActioncmd;
        if (mActioncmd.actioncmdType == 1) {
            setShowText(App.Inst.getString(R.string.lamp_tool) + "：" + mActioncmd.actioncmdfield.get(0).cmd + " " + mActioncmd.actioncmdfield.get(0).paralist.replace("{", "").replace("}", ""));
        } else {
            if (null != mActioncmd.actioncmdfield && mActioncmd.actioncmdfield.size() > 0)
                setShowText(App.Inst.getString(R.string.scene) + "：" + mActioncmd.actioncmdfield.get(0).paralist.replace("{", "").replace("}", ""));
        }
    }

    public Actionnotify getmActionnotify() {
        return mActionnotify;
    }

    public void setmActionnotify(Actionnotify mActionnotify) {
        this.mActionnotify = mActionnotify;

        if (mActionnotify.actiontype == 1) {//邮箱通知
            setShowText(App.Inst.getString(R.string.email_notify) + "：" + mActionnotify.subject + " " + mActionnotify.content + " " + mActionnotify.emailaddress);
        } else if (mActionnotify.actiontype == 2) { //App通知
            setShowText(mActionnotify.subject + " " + mActionnotify.content);
        } else {
            if (StringUtil.isNotEmpty(mActionnotify.msisdn, true)) {
                setShowText(mActionnotify.msisdn + " " + mActionnotify.content);
            } else {
                if (TextUtils.isEmpty(mActionnotify.emailaddress)) {

                    setShowText(mActionnotify.content);
                } else {

                    setShowText(mActionnotify.emailaddress + " " + mActionnotify.content);
                }
            }
        }
    }

//    public ControlRuleDevice getmControlRuleDevice() {
//        return mControlRuleDevice;
//    }

//    public void setmControlRuleDevice(ControlRuleDevice mControlRuleDevice) {
//        this.mControlRuleDevice = mControlRuleDevice;
//        setShowText(mControlRuleDevice.roomName + " " + mControlRuleDevice.statues + " 亮度：" + mControlRuleDevice.brightness + " 色温：" + mControlRuleDevice.cct);
//    }

    /**
     * 获取类型
     *
     * @return 0 Schedule，1 Condition， 2 Actioncmd， 3 Actionnotify
     */
    public int getType() {
        if (null != mSchedule) {
            return 0;
        }

        if (null != mCondition) {
            return 1;
        }

        if (null != mActioncmd) {
            return 2;
        }
        return 3;
    }

    @Override
    public String toString() {
        return "NewRuleItemInfo{" +
                "mSchedule=" + mSchedule +
                ", mCondition=" + mCondition +
                ", mActioncmd=" + mActioncmd +
                ", mActionnotify=" + mActionnotify +
                ", showText='" + showText + '\'' +
                '}';
    }
}
