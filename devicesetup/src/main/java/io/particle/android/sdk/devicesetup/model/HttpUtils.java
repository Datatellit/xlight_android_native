package io.particle.android.sdk.devicesetup.model;

import android.util.Log;

import com.google.gson.Gson;


import org.json.JSONObject;

/**
 * Created by guangbinw on 2017/3/12.
 */
public class HttpUtils extends BaseHttp {


    private final Gson gson;
    private OnHttpRequestCallBack mOnHttpRequestCallBack;
    private Class mClass;

    public static HttpUtils getInstance() {
        return new HttpUtils();
    }

    private HttpUtils() {
        gson = new Gson();
    }

    /**
     * get请求
     *
     * @param url                    请求地址
     * @param mClass                 请求结果直接解析成指定的对象，填null为不做任何处理返回字符串
     * @param mOnHttpRequestCallBack 用于接受服务器返回数据
     */
    public void getRequestInfo(String url, Class mClass, OnHttpRequestCallBack mOnHttpRequestCallBack) {

        this.mClass = mClass;
        this.mOnHttpRequestCallBack = mOnHttpRequestCallBack;
        // 添加请求日志
        Log.v("HttpUtils", url);
        getData(url);
    }

    /**
     * post请求
     *
     * @param url                    请求地址
     * @param jsonParam              请求参数json形式
     * @param mClass                 请求结果直接解析成指定的对象，填null为不做任何处理返回字符串
     * @param mOnHttpRequestCallBack 用于接受服务器返回数据
     */
    public void postRequestInfo(String url, String jsonParam, Class mClass, OnHttpRequestCallBack mOnHttpRequestCallBack) {

        this.mClass = mClass;
        this.mOnHttpRequestCallBack = mOnHttpRequestCallBack;
        Log.v("HttpUtils", url + "," + jsonParam);
        postData(url, jsonParam);

    }

    /**
     * post请求
     *
     * @param url                    请求地址
     * @param jsonParam              请求参数json形式
     * @param mClass                 请求结果直接解析成指定的对象，填null为不做任何处理返回字符串
     * @param mOnHttpRequestCallBack 用于接受服务器返回数据
     */
    public void postRequestInfoByForm(String url, String jsonParam, Class mClass, OnHttpRequestCallBack mOnHttpRequestCallBack) {

        this.mClass = mClass;
        this.mOnHttpRequestCallBack = mOnHttpRequestCallBack;
        Log.v("HttpUtils", url + "," + jsonParam);
        postData(url, jsonParam, "application/x-www-form-urlencoded");

    }


    /**
     * put请求
     *
     * @param url                    请求地址
     * @param jsonParam              请求参数json形式
     * @param mClass                 请求结果直接解析成指定的对象，填null为不做任何处理返回字符串
     * @param mOnHttpRequestCallBack 用于接受服务器返回数据
     */
    public void putRequestInfo(String url, String jsonParam, Class mClass, OnHttpRequestCallBack mOnHttpRequestCallBack) {
        Log.v("HttpUtils", url);
        this.mClass = mClass;
        this.mOnHttpRequestCallBack = mOnHttpRequestCallBack;

        putData(url, jsonParam);

    }

    /**
     * delete请求
     *
     * @param url                    请求地址
     * @param jsonParam              请求参数json形式
     * @param mClass                 请求结果直接解析成指定的对象，填null为不做任何处理返回字符串
     * @param mOnHttpRequestCallBack 用于接受服务器返回数据
     */
    public void deleteRequestInfo(String url, String jsonParam, Class mClass, OnHttpRequestCallBack mOnHttpRequestCallBack) {

        this.mClass = mClass;
        this.mOnHttpRequestCallBack = mOnHttpRequestCallBack;

        deleteData(url, jsonParam);

    }

    @Override
    public void okOnError(String errResult) {
        Log.d("XLight", "okHttp error," + errResult);
        if (null != mOnHttpRequestCallBack) {
            mOnHttpRequestCallBack.onHttpRequestFail(0, "net error");
        }
    }

    @Override
    public void okOnResponse(String result) {
        try {
            Log.e("HttpUtils", result);
            if (null != mOnHttpRequestCallBack) {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (null == mOnHttpRequestCallBack) {
                        return;
                    }

                    switch (code) {
                        case 1:
                            if (null != mClass) {
                                Object responseResult = null;
                                responseResult = gson.fromJson(result, mClass);
                                mOnHttpRequestCallBack.onHttpRequestSuccess(responseResult);
                            } else {
                                mOnHttpRequestCallBack.onHttpRequestSuccess(result);
                            }
                            break;

                        case 10000://没有找到令牌
                            mOnHttpRequestCallBack.onHttpRequestFail(code, "net error");
                            break;
                        case 10002://令牌过期
                            mOnHttpRequestCallBack.onHttpRequestFail(code, "net error");
                            break;
                        case 20000://参数不合法
                            mOnHttpRequestCallBack.onHttpRequestFail(code, "net error");
                            break;
                        case 20101:
                            mOnHttpRequestCallBack.onHttpRequestFail(code,"net error");
                            break;
                        default:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, msg);
                            break;
                    }
                } else {
                    if (null != mClass) {
                        Object responseResult = null;
                        responseResult = gson.fromJson(result, mClass);
                        mOnHttpRequestCallBack.onHttpRequestSuccess(responseResult);
                    } else {
                        mOnHttpRequestCallBack.onHttpRequestSuccess(result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != mOnHttpRequestCallBack) {
                mOnHttpRequestCallBack.onHttpRequestFail(0, "net error");
            }
        }
    }

    public interface OnHttpRequestCallBack {
        void onHttpRequestSuccess(Object result);

        void onHttpRequestFail(int code, String errMsg);
    }


}
