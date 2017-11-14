/**
 * 本程序代码版权归北京快乐格子信息技术有限公司或本项目委托的客户方所有。
 * Copyright www.kuailegezi.com
 * legal@kuailegezi.com
 */
package com.umarbhutta.xlightcompanion.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * 共享文件类
 */
public class SharedPreferencesUtils {

    public static final String SP_NAME = "x_light_config";
    private static SharedPreferences sp;

    /**
     * 用户信息
     */
    public static final String KEY__USERINFO = "light_userinfo_key";

    /**
     * 用户信息
     */
    public static final String KEY__ANONYMOUSINFO = "lightanonymousinfo_key";

    /**
     * 设备列表
     */
    public static final String KEY_DEVICE_LIST = "device_list_key";
    /**
     * 是不是第一次启动
     */
    public static final String KEY_IS_FIRST_LUNCH = "key_is_first_launch";
    /**
     * 场景列表,,存储为List<Rows>
     */
    public static final String KEY_SCENE_LIST = "key_scene_list";


    public static boolean putObject(Context context, String key, Object obj) {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        paraCheck(sp, key);
        if (obj == null) {
            Editor e = sp.edit();
            e.putString(key, "");
            return e.commit();
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
            } catch (IOException e1) {
                e1.printStackTrace();
                return false;
            }
            String objectBase64 = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));
            Editor e = sp.edit();
            e.putString(key, objectBase64);
            return e.commit();
        }
    }

    public static Object getObject(Context context, String key,
                                   Object defaultValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        paraCheck(sp, key);
        String objectBase64 = sp.getString(key, "");
        //Log.e("Waroom", objectBase64);
        byte[] base64Bytes = Base64.decode(objectBase64.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (IOException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private static void paraCheck(SharedPreferences sp, String key) {
        if (sp == null) {
            throw new IllegalArgumentException();
        }
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException();
        }
    }

}
