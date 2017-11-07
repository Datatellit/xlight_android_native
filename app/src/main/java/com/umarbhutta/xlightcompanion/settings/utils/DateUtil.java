package com.umarbhutta.xlightcompanion.settings.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static Date getCurrentDate() {

        Calendar c = Calendar.getInstance();

        return new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * @param mills 毫秒值
     * @return yyyy-MM-dd
     */
    public static String getDateStr(String mills) {
        if (TextUtils.isEmpty(mills) || "null".equals(mills)) {
            mills = "" + System.currentTimeMillis();
        }
        Date data = new Date();
        data.setTime(Long.parseLong(mills));
        return getDate(data, false);
    }

    public static String getDate(Date date, boolean needShowTime) {

        if (date == null) {
            return "";
        }
        String format = "yyyy-MM-dd";
        if (needShowTime) {
            format = "yyyy-MM-dd hh:mm:ss";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String getDate(Date date) {

        if (date == null) {
            return "";
        }
        String format = "yyyyMMddhhmmss";
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String getDate(String time, boolean needShowTime) {
        long mill = 0;
        if (time == null) {
            return "";
        } else {
            try {
                mill = Long.parseLong(time);
            } catch (Exception e) {
            }
        }
        Date date = new Date(mill);
        String format = "yyyy-MM-dd";
        if (needShowTime) {
            format = "yyyy-MM-dd hh:mm:ss";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date getDate(String time) {
        if (time == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 用来全局控制 上一周，本周，下一周的周数变化
    private static int weeks = 0;
    private static int MaxDate;// 一月最大天数
    private static int MaxYear;// 一年最大天数

    /**
     * 定义一天的毫秒数
     */
    public static final long MILLSECOND_OF_DAY = 86400000;

    /**
     * 时间格式化字符串
     */
    public static final String time24Formatter = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {

        Calendar cd = Calendar.getInstance();
        System.out.println(cd.getTime());
        cd.roll(Calendar.SECOND, 1);
        System.out.println(cd.getTime());

    }

    /**
     * 获得比当前时间多一秒的时间
     */
    public static Date getMoreSecondTime() {
        Calendar cd = Calendar.getInstance();
        cd.roll(Calendar.SECOND, 1);
        return cd.getTime();
    }

    /**
     * 获取与今天相比,N天前的Date
     */
    public static Date findDaysAgo(int day) {
        Calendar cal = Calendar.getInstance(); // 当前时间
        cal.add(Calendar.DATE, -day); // - :前xxx天; +:后xxx天
        Date date = cal.getTime();
        return date;
    }

    /**
     * 获取与参数date相比,N天后的Date
     */
    public static Date findDaysAfter(Date date, int day) {
        Calendar cal = Calendar.getInstance(); // 当前时间
        cal.setTime(date);
        cal.add(Calendar.DATE, +day); // - :前xxx天; +:后xxx天
        Date date2 = cal.getTime();
        return date2;
    }

    /**
     * 获取当日N分钟前的Date
     */
    public static Date findTomcatAccessLogMinutesAgo(int minutes) {
        Calendar cal = Calendar.getInstance(); // 当前时间
        cal.add(Calendar.MINUTE, -minutes); // - :前xxx分钟; +:后xxx分钟
        Date date = cal.getTime();
        return date;
    }

    /**
     * 获取当日N分钟前的Date
     */
    public static Date getMinutesAgo(int minutes) {
        Calendar cal = Calendar.getInstance(); // 当前时间
        cal.add(Calendar.MINUTE, -minutes); // - :前xxx分钟; +:后xxx分钟
        Date date = cal.getTime();
        return date;
    }

    /**
     * 格式化日期
     *
     * @param strDate 符合格式的字符串
     * @return 格式后的日期 yyyy-MM-dd HH:mm:ss
     */
    public static Date parserToSecond(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化日期
     *
     * @return 格式后的日期 yyyy-MM-dd
     */
    public static Date parserToDay(Date testDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDate);
        calendar.clear(Calendar.MILLISECOND);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.HOUR_OF_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化日期精确到秒
     *
     * @return 格式后的日期 yyyy-MM-dd HH:mm:ss"
     */
    public static Date parserToSecond(Date testDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化日期
     *
     * @param strDate 符合格式的字符串
     * @return 格式后的日期 yyyy-MM-dd
     */
    public static Date parserToDay(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 得到当前月份的周期开始日期
     *
     * @param currentDate 当前日期
     * @return 当前月份的周期开始日期
     */
    public static Date getCurBeginCycleDate(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        String year = "" + calendar.get(Calendar.YEAR);
        String month = (calendar.get(Calendar.MONTH) + 1) + "";
        if (month.length() < 2) {
            month = "0" + month;
        }
        String dateStr = year + "-" + month + "-01 00:00:00";
        return DateUtil.parserToSecond(dateStr);
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得00:00 -> 01:00的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0001(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得00:00 -> 01:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0001(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得01:00 -> 02:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0102(Date currentDate) {
        currentDate.setHours(1);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得01:00 -> 02:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0102(Date currentDate) {
        currentDate.setHours(1);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得02:00 -> 03:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0203(Date currentDate) {
        currentDate.setHours(2);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得02:00 -> 03:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0203(Date currentDate) {
        currentDate.setHours(2);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得03:00 -> 04:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0304(Date currentDate) {
        currentDate.setHours(3);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得03:00 -> 04:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0304(Date currentDate) {
        currentDate.setHours(3);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得04:00 -> 05:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0405(Date currentDate) {
        currentDate.setHours(4);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得04:00 -> 05:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0405(Date currentDate) {
        currentDate.setHours(4);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得05:00 -> 06:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0506(Date currentDate) {
        currentDate.setHours(5);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得05:00 -> 06:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0506(Date currentDate) {
        currentDate.setHours(5);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得06:00 -> 07:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0607(Date currentDate) {
        currentDate.setHours(6);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得06:00 -> 07:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0607(Date currentDate) {
        currentDate.setHours(6);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得07:00 -> 08:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0708(Date currentDate) {
        currentDate.setHours(7);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得07:00 -> 08:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0708(Date currentDate) {
        currentDate.setHours(7);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得08:00 -> 09:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0809(Date currentDate) {
        currentDate.setHours(8);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得08:00 -> 09:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0809(Date currentDate) {
        currentDate.setHours(8);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得09:00 -> 10:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart0910(Date currentDate) {
        currentDate.setHours(9);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得09:00 -> 10:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd0910(Date currentDate) {
        currentDate.setHours(9);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得10:00 -> 11:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1011(Date currentDate) {
        currentDate.setHours(10);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得10:00 -> 11:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1011(Date currentDate) {
        currentDate.setHours(10);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得11:00 -> 12:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1112(Date currentDate) {
        currentDate.setHours(11);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得11:00 -> 12:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1112(Date currentDate) {
        currentDate.setHours(11);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得12:00 -> 13:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1213(Date currentDate) {
        currentDate.setHours(12);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得12:00 -> 13:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1213(Date currentDate) {
        currentDate.setHours(12);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得13:00 -> 14:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1314(Date currentDate) {
        currentDate.setHours(13);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得13:00 -> 14:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1314(Date currentDate) {
        currentDate.setHours(13);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得14:00 -> 15:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1415(Date currentDate) {
        currentDate.setHours(14);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得14:00 -> 15:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1415(Date currentDate) {
        currentDate.setHours(14);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得15:00 -> 16:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1516(Date currentDate) {
        currentDate.setHours(15);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得15:00 -> 16:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1516(Date currentDate) {
        currentDate.setHours(15);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得16:00 -> 17:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1617(Date currentDate) {
        currentDate.setHours(16);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得16:00 -> 17:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1617(Date currentDate) {
        currentDate.setHours(16);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得17:00 -> 18:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1718(Date currentDate) {
        currentDate.setHours(17);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得17:00 -> 18:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1718(Date currentDate) {
        currentDate.setHours(17);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得18:00 -> 19:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1819(Date currentDate) {
        currentDate.setHours(18);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得18:00 -> 19:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1819(Date currentDate) {
        currentDate.setHours(18);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得19:00 -> 20:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart1920(Date currentDate) {
        currentDate.setHours(19);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得19:00 -> 20:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd1920(Date currentDate) {
        currentDate.setHours(19);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得20:00 -> 21:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart2021(Date currentDate) {
        currentDate.setHours(20);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得20:00 -> 21:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd2021(Date currentDate) {
        currentDate.setHours(20);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得21:00 -> 22:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart2122(Date currentDate) {
        currentDate.setHours(21);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得21:00 -> 22:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd2122(Date currentDate) {
        currentDate.setHours(21);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得22:00 -> 23:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart2223(Date currentDate) {
        currentDate.setHours(22);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得22:00 -> 23:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd2223(Date currentDate) {
        currentDate.setHours(22);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得23:00 -> 24:00的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart2324(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得23:00 -> 24:00最后一秒
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd2324(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart01(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd01(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart02(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd02(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart03(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd03(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart04(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd04(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart05(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd05(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart06(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd06(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart07(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd07(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart08(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd08(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart09(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd09(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart10(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd10(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart11(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd11(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart12(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd12(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart13(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd13(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart14(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd14(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart15(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd15(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart16(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd16(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart17(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd17(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart18(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd18(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart19(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd19(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart20(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd20(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart21(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd21(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart22(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd22(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart23(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd23(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart24(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd24(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart25(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd25(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart26(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd26(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart27(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd27(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart28(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd28(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart29(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd29(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart30(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd30(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 取得当前日期的最前一秒 yyyy-MM-dd 00:00:00
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrStart31(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        return currentDate;
    }

    /**
     * 取得当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static Date getCurrEnd31(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
        return currentDate;
    }

    /**
     * 重置当前日期的最前一秒
     *
     * @param currentDate
     * @return
     */
    public static void resetStartDate(Date currentDate) {
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
    }

    /**
     * 重置当前日期的最后一秒 yyyy-MM-dd 23:59:59
     *
     * @param currentDate
     * @return
     */
    public static void resetEndDate(Date currentDate) {
        currentDate.setHours(23);
        currentDate.setMinutes(59);
        currentDate.setSeconds(59);
    }

    /**
     * 取得当前周期的周期结束日期
     *
     * @param currentDate 当前日期
     * @return 当前周期的周期结束日期
     */
    public static Date getCurEndCycleDate(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        String year = "" + calendar.get(Calendar.YEAR);
        String month = (calendar.get(Calendar.MONTH) + 2) + "";
        if (month.length() < 2) {
            month = "0" + month;
        }
        String dateStr = year + "-" + month + "-01 23:59:59";
        calendar.setTime(DateUtil.parserToSecond(dateStr));
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 得到下nextCycle周期的月份
     *
     * @param currentDate 当前日期
     * @param nextCycle   下nextCycle周期
     * @return 下nextCycle周期
     */
    public static Date getNextCycleDate(Date currentDate, long nextCycle) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        String year = "" + calendar.get(Calendar.YEAR);
        nextCycle++;
        String month = (calendar.get(Calendar.MONTH) + nextCycle) + "";
        if (month.length() < 2) {
            month = "0" + month;
        }
        String dateStr = year + "-" + month + "-01 00:00:00";
        return DateUtil.parserToSecond(dateStr);
    }

    /**
     * 获取开始和结束日期之间的间隔日期
     *
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @param roundingMode 舍入方式 见 BigDecimal的定义
     * @return 相隔的日期数
     */
    public static long getDaysBetweenDate(Date startDate, Date endDate,
                                          int roundingMode) {

        BigDecimal bStart = new BigDecimal(startDate.getTime());
        BigDecimal bEnd = new BigDecimal(endDate.getTime());
        BigDecimal bUnit = new BigDecimal(MILLSECOND_OF_DAY);
        return (bEnd.subtract(bStart)).divide(bUnit, roundingMode).longValue();
    }

    /**
     * 获取开始和结束日期之间的间隔日期
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 相隔的日期数
     */
    public static long getDaysBetweenDateWithoutTime(Date startDate,
                                                     Date endDate) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);

        cal1.clear(Calendar.MILLISECOND);
        cal1.clear(Calendar.SECOND);
        cal1.clear(Calendar.MINUTE);
        cal1.clear(Calendar.HOUR_OF_DAY);

        cal2.clear(Calendar.MILLISECOND);
        cal2.clear(Calendar.SECOND);
        cal2.clear(Calendar.MINUTE);
        cal2.clear(Calendar.HOUR_OF_DAY);

        return (cal2.getTime().getTime() - cal1.getTime().getTime())
                / (24 * 60 * 60 * 1000);
    }

    /**
     * @param date
     * @return
     */
    public static Date getTomorrowDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取两个日期之间相差的月份数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param flag      false 为全月舍
     * @return 返回的月份数
     */
    public static long getMonthsBetweenDate(Date startDate, Date endDate,
                                            boolean flag) {
        Calendar cal1 = Calendar.getInstance();

        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        if (endDate.before(startDate)) {
            cal1.setTime(endDate);
            cal2.setTime(startDate);
        }

        cal1.clear(Calendar.MILLISECOND);
        cal1.clear(Calendar.SECOND);
        cal1.clear(Calendar.MINUTE);
        cal1.clear(Calendar.HOUR_OF_DAY);

        cal2.clear(Calendar.MILLISECOND);
        cal2.clear(Calendar.SECOND);
        cal2.clear(Calendar.MINUTE);
        cal2.clear(Calendar.HOUR_OF_DAY);

        return getMonthsBetweenDate(cal1, cal2, flag);

    }

    /**
     * 获取两个日期之间相差的月份数
     *
     * @param cal1 开始日期
     * @param cal2 结束日期
     * @param flag false 为全月舍
     * @return 返回的月份数
     */
    public static long getMonthsBetweenDate(Calendar cal1, Calendar cal2,
                                            boolean flag) {
        long month = 0L;
        while (cal1.before(cal2)) {
            cal1.add(Calendar.MONTH, 1);
            month++;
            if (flag) {
                if ((cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
                        && (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                        && (cal1.get(Calendar.DAY_OF_MONTH) > cal2
                        .get(Calendar.DAY_OF_MONTH))) {
                    month--;
                    break;
                }
                if ((cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH))
                        && (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))

                        ) {
                    month--;
                    break;
                }
            }
        }
        return month;
    }

    /**
     * @param date
     * @param field
     * @return
     */
    public static long getDateField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (field == Calendar.MONTH)
            return cal.get(field) + 1;
        else
            return cal.get(field);

    }

    /**
     * @param date
     * @return
     */
    public static Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();

    }

    public static Date getAfterDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 当前的日期 date months月数
     *
     * @param date
     * @param months
     * @return
     */
    public static Date getAfterMonth(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 计算时间差
     *
     * @param endDate
     * @param beginDate
     * @return
     */
    public static int diffDate(Date endDate, Date beginDate) {
        return (int) ((endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000));
    }

    /**
     * 根据传入的日期，欲转换成的日期样式，返回转换后的日期字符串
     *
     * @param ldate   - 日期
     * @param pattern - 具体的值范围见java.text.SimpleDateFormat的说明
     * @return String 转换后的日期字符串
     * @see SimpleDateFormat
     */
    public static String format(Date ldate, String pattern) {

        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(ldate);
    }

    /**
     * date转String 默认格式
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatToSecend(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = sdf.format(date);
        return s;

    }

    /**
     * date转String 默认格式
     *
     * @return yyyyMMddHHmmss
     */
    public static String formatToSecendOnlyNum(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = sdf.format(date);
        return s;

    }

    /**
     * date转String 默认格式
     *
     * @return HH:mm:ss
     */
    public static String formatToHHmmss(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String s = sdf.format(date);
        return s;

    }

    /**
     * date转String 默认格式
     *
     * @return HH:mm
     */
    public static String formatToHHmm(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String s = sdf.format(date);
        return s;

    }

    /**
     * date转String 默认格式
     *
     * @return yyyy-MM-dd
     */
    public static String formatToDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(date);
        return s;

    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static long getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0L;
        }
        return day;
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = DateUtil.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    // 计算当月最后一天,返回字符串
    public static String getDefaultDay() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
        lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 上月第一天
    public static String getPreviousMonthFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号
        // lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天

        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获取当月第一天
    public static String getFirstDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获取Date所在月的第一天
    public static Date getFirstDayOfMonth(Date date) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        return lastDate.getTime();
    }

    // 获得本周星期日的日期
    public static String getCurrentWeekday() {
        weeks = 0;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获取当天时间
    public static String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
        String hehe = dateFormat.format(now);
        return hehe;
    }

    // 获得当前日期与本周日相差的天数
    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    // 获得本周一的日期
    public static String getMondayOFWeek() {
        weeks = 0;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得相应周的周六的日期
    public static String getSaturday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得上周星期日的日期
    public static String getPreviousWeekSunday() {
        weeks = 0;
        weeks--;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得上周星期一的日期
    public static String getPreviousWeekday() {
        weeks--;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得下周星期一的日期
    public static String getNextMonday() {
        weeks++;
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得下周星期日的日期
    public static String getNextSunday() {

        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    private int getMonthPlus() {
        Calendar cd = Calendar.getInstance();
        int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH);
        cd.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        cd.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        MaxDate = cd.get(Calendar.DATE);
        if (monthOfNumber == 1) {
            return -MaxDate;
        } else {
            return 1 - monthOfNumber;
        }
    }

    // 获得上月最后一天的日期
    public static String getPreviousMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, -1);// 减一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得下个月第一天的日期String
    public static String getNextMonthFirstStr() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);// 减一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得下个月第一天的日期Calendar
    public static Calendar getNextMonthFirstDate() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);// 减一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        // str = sdf.format(lastDate.getTime());
        return lastDate;
    }

    // 获得下个月最后一天的日期
    public static String getNextMonthEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);// 加一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得N个月最后一天的日期
    public static String getDurationMonthEnd(Integer duration) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, duration);// 加N个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得下个月第一天的日期,N个月后，当前日期前一天
    public static String getDurationMonthBeforeToDay(Integer duration) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar beforeToDayDate = getNextMonthFirstDate();
        beforeToDayDate.add(Calendar.MONTH, duration - 1);// 加N个月
        // beforeToDayDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        beforeToDayDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是当前日期前一天
        str = sdf.format(beforeToDayDate.getTime());
        return str;
    }

    // 获得明年最后一天的日期
    public static String getNextYearEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);// 加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        lastDate.roll(Calendar.DAY_OF_YEAR, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得参数日期是一个月的第几天
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    // 获得明年第一天的日期
    public static String getNextYearFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);// 加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        str = sdf.format(lastDate.getTime());
        return str;

    }

    // 获得本年有多少天
    private static int getMaxYear() {
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
        cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        return MaxYear;
    }

    private static int getYearPlus() {
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// 获得当天是一年中的第几天
        cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
        cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        if (yearOfNumber == 1) {
            return -MaxYear;
        } else {
            return 1 - yearOfNumber;
        }
    }

    // 获得本年第一天的日期
    public static String getCurrentYearFirst() {
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus);
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        return preYearDay;
    }

    // 获得本年最后一天的日期 *
    public static String getCurrentYearEnd() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
        String years = dateFormat.format(date);
        return years + "-12-31";
    }

    // 获得上年第一天的日期 *
    public static String getPreviousYearFirst() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value--;
        return years_value + "-1-1";
    }

    // 获得上年最后一天的日期
    public static String getPreviousYearEnd() {
        weeks--;
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus + MaxYear * weeks
                + (MaxYear - 1));
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        getThisSeasonTime(11);
        return preYearDay;
    }

    // 获得本季度
    public static String getThisSeasonTime(int month) {
        int array[][] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        int season = 1;
        if (month >= 1 && month <= 3) {
            season = 1;
        }
        if (month >= 4 && month <= 6) {
            season = 2;
        }
        if (month >= 7 && month <= 9) {
            season = 3;
        }
        if (month >= 10 && month <= 12) {
            season = 4;
        }
        int start_month = array[season - 1][0];
        int end_month = array[season - 1][2];

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// 可以方便地修改日期格式
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);

        int start_days = 1;// years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);
        int end_days = getLastDayOfMonth(years_value, end_month);
        String seasonDate = years_value + "-" + start_month + "-" + start_days
                + ";" + years_value + "-" + end_month + "-" + end_days;
        return seasonDate;

    }

    /**
     * 获取某年某月的最后一天
     *
     * @param year  年
     * @param month 月
     * @return 最后一天
     */
    private static int getLastDayOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        }
        return 0;
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static void main22(String[] args) {
        /*
         * Calendar cal1 = Calendar.getInstance(); Calendar cal2 =
		 * Calendar.getInstance(); cal1.set(2006, 11, 13, 10, 10);
		 * cal2.set(2006, 6, 18, 10, 30); //
		 * System.out.println(DateUtil.getMonthsBetweenDate(cal1,cal2,false));
		 * System.out.println(DateUtil.parser(cal1.getTime()));
		 * /*System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.YEAR)); System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.DATE)); System.out.println(DateUtil.getDateField(new Date(),
		 * Calendar.MONTH));
		 */
        // System.out.println(DateUtil.getTomorrowDate(new Date()));
        Calendar calendar = Calendar.getInstance();
        long start = calendar.getTimeInMillis();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis();

        System.out.print(((end - start) / 1000 / 60 / 60 / 24 / 30) + "月");
        System.out.print(((end - start) / 1000 / 60 / 60 / 24) + "日");
        System.out.print(((end - start) / 1000 / 60 / 60) + "小时");
        System.out.print(((end - start) / 1000 / 60) + "分钟");
        System.out.println(((end - start) / 1000) + "秒");

    }

    public static void main3(String[] args) throws ParseException {
        Date startDateTime = DateUtil.parserToSecond("2013-05-13 14:02:42");
        Date endDateTime = DateUtil.parserToSecond("2013-05-17 12:02:42");
        String startHHmmStr = DateUtil.formatToHHmm(startDateTime);
        String startDayStr = DateUtil.formatToDay(startDateTime);
        String endHHmmStr = DateUtil.formatToHHmm(endDateTime);
        String endDayStr = DateUtil.formatToDay(endDateTime);
        // 得到二个日期间的间隔天数
        long day = (endDateTime.getTime() - startDateTime.getTime())
                / (24 * 60 * 60 * 1000);
        // 开始那天
        String tStart = startDayStr + "==" + startHHmmStr + "-"
                + DateUtil.formatToHHmm(DateUtil.getCurrEnd(startDateTime));
        System.out.println(tStart);

        // 中间那些天
        for (int i = 0; i < day; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDateTime);
            calendar.roll(Calendar.DATE, i + 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(sdf.format(calendar.getTime()));
            String tMiddle = DateUtil.formatToDay(date) + "=="
                    + DateUtil.formatToHHmm(DateUtil.getCurrStart(date)) + "-"
                    + DateUtil.formatToHHmm(DateUtil.getCurrEnd(date));
            System.out.println(i + 1 + "--" + tMiddle);
        }

        // 结束那天
        String tEnd = endDayStr + "=="
                + DateUtil.formatToHHmm(DateUtil.getCurrStart(endDateTime))
                + "-" + endHHmmStr;
        System.out.println(tEnd);

    }
}
