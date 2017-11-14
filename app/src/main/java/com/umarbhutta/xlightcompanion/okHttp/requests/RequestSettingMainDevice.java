package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequstCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guangbinw on 2017/3/14.
 * 设置主设备接口
 */
public class RequestSettingMainDevice implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequstCallback mCommentRequstCallback;

    public static RequestSettingMainDevice getInstance() {
        return new RequestSettingMainDevice();
    }

    /**
     * 设置主设备
     *
     * @param context
     * @param maindevice             主设备标识字段，传值1，代表为主设备
     * @param deviceId               设备id
     * @param mCommentRequstCallback
     */
    public void settingDevice(Context context, int maindevice, int deviceId, CommentRequstCallback mCommentRequstCallback) {
        this.context = context;
        this.mCommentRequstCallback = mCommentRequstCallback;
//        if (UserUtils.isLogin(context)) {
        try {
            JSONObject object = new JSONObject();
            object.put("maindevice", maindevice);
            //object.put("userId", userId);
            HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_SET_MAIN_DEVICE, deviceId, UserUtils.getAccessToken(context)),
                    object.toString(), null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        if (null != mCommentRequstCallback) {
            mCommentRequstCallback.onCommentRequstCallbackSuccess();
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mCommentRequstCallback) {
            mCommentRequstCallback.onCommentRequstCallbackFail(code, errMsg);
        }
    }


}
