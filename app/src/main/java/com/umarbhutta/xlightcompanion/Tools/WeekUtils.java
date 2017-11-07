package com.umarbhutta.xlightcompanion.Tools;

import android.content.Context;
import android.text.TextUtils;

import com.umarbhutta.xlightcompanion.R;

/**
 * Created by alan on 2017/5/25.
 */

public class WeekUtils {
    /**
     * [1,3,5]  返回 周一，周二
     *
     * @param weekdays
     * @return
     */
    public static String getweekdays(String weekdays, Context context) {
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
                        builder.append(context.getString(R.string.zhouyi) + ",");
                        break;
                    case "2":
                        builder.append(context.getString(R.string.zhouer) + ",");
                        break;
                    case "3":
                        builder.append(context.getString(R.string.zhousan) + ",");
                        break;
                    case "4":
                        builder.append(context.getString(R.string.zhousi) + ",");
                        break;
                    case "5":
                        builder.append(context.getString(R.string.zhouwu) + ",");
                        break;
                    case "6":
                        builder.append(context.getString(R.string.zhouilu) + ",");
                        break;
                    default:
                        builder.append(context.getString(R.string.zhouri) + ",");
                        break;
                }
            }
            return builder.toString().substring(0, builder.length() - 1) + " ";
        }

        return " ";

    }
}
