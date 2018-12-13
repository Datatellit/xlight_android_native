package com.umarbhutta.xlightcompanion.Tools;

import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class SensorTool {
    static class Importance {
        double DHTt = 0.15;
        double DHTh = 0.1;
        double ALS = 0.05;
        double PM25 = 0.25;
        double CH2O = 0.25;
        double CO2 = 0.2;
    }

    static class GreatValue {
        int a = -1;
        int b = -1;
        int c = -1;
        int d = -1;

        public GreatValue(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    private static GreatValue DHTt = new GreatValue(5, 19, 24, 32);
    private static GreatValue DHTh = new GreatValue(10, 30, 60, 100);
    private static GreatValue ALS = new GreatValue(30, 70, 90, 100);
    private static GreatValue PM25 = new GreatValue(-1, 0, 50, 200);
    private static GreatValue CH2O = new GreatValue(-1, 0, 80, 160);
    private static GreatValue CO2 = new GreatValue(-1, 0, 450, 2000);

    public static int getKPI(Sensorsdata sensor) {
        if (!exception(sensor)) {
            Importance score = new Importance();
            String[] prop = new String[]{"DHTt", "DHTh", "PM25", "CH2O", "CO2", "ALS"};
            for (String key : prop) {
                int value;
                if (key.equals("DHTt")) {
                    value = (int) sensor.DHTt;
                    score.DHTt = getScore(DHTt, score.DHTt, value);
                } else if (key.equals("DHTh")) {
                    value = (int) sensor.DHTh;
                    score.DHTh = getScore(DHTh, score.DHTh, value);
                } else if (key.equals("ALS")) {
                    value = (int) sensor.ALS;
                    score.ALS = getScore(ALS, score.ALS, value);
                } else if (key.equals("PM25")) {
                    value = (int) sensor.PM25;
                    score.PM25 = getScore(PM25, score.PM25, value);
                } else if (key.equals("CO2")) {
                    value = (int) sensor.CO2;
                    score.CO2 = getScore(CO2, score.CO2, value);
                } else if (key.equals("CH2O")) {
                    value = (int) sensor.CH2O;
                    score.CH2O = getScore(CH2O, score.CH2O, value);
                }
            }
            double total = score.DHTt + score.ALS + score.CH2O + score.CO2 + score.DHTh + score.PM25;
            DecimalFormat df = new DecimalFormat("######0"); //四色五入转换成整数
            return Integer.parseInt(df.format(total * 100));
        }
        return 0;
    }

    private static double getScore(GreatValue gv, double imp, int value) {
        if (value >= gv.b && value <= gv.c) {
            return imp;
        } else {
            // 根据值线性调节，如果超出阈值，直接不得分
            if (gv.a != -1 && value <= gv.a) {
                return 0;
            } else if (gv.d != -1 && value >= gv.d) {
                return 0;
            } else {
                // 进行基础的线性计算，如果所属a侧  score = score - (score / (b-a)) * (b-value)
                if (gv.a != -1 && value < gv.b) {
                    return imp - (imp / (gv.b - gv.a)) * (gv.b - value);
                } else { // 进行基础的线性计算，如果所属d侧  score = score - (score / (d-c)) * (abs(value-d))
                    return imp - (imp / (gv.d - gv.c)) * (value - gv.c);
                }
            }
        }
    }

    private static boolean exception(Sensorsdata sensor) {
        return  false;
//        if (sensor.DHTt < 40 && sensor.DHTh > 10 && sensor.PM25 < 350 && sensor.CH2O < 250 && sensor.CO2 < 4500) {
//            return false;
//        }
//        return true;
    }
}
