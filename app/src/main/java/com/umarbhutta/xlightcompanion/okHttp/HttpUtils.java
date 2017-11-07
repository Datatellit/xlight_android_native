package com.umarbhutta.xlightcompanion.okHttp;

import android.util.Log;

import com.google.gson.Gson;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.Logger;

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
        Logger.v("HttpUtils", url);
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
        Logger.v("HttpUtils", url + "," + jsonParam);
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
        Logger.v("HttpUtils", url + "," + jsonParam);
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
            mOnHttpRequestCallBack.onHttpRequestFail(NetConfig.ERROR_PARSE, "网络异常，请检查网络");
        }
    }

    @Override
    public void okOnResponse(String result) {
        try {
            Logger.e("HttpUtils", result);
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
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.no_ling_pai));
                            break;
                        case 10002://令牌过期
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.ling_pai_guoqi));
                            break;
                        case 20000://参数不合法
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.param_not_legal));
                            break;
                        case 20101:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_less));
                            break;
                        case 30100:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_db_fail));
                            break;
                        case 30200:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_redis_error));
                            break;
                        case 40100:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_user_pwd_null));
                            break;
                        case 40101:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_user_exits));
                            break;
                        case 40102:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_user_create_fail));
                            break;
                        case 40200:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_username_pwd_error));
                            break;
                        case 40201:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_login_fail));
                            break;
                        case 40300:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_old_pwd_error));
                            break;
                        case 40400:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_email_fail));
                            break;
                        case 40401:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_email_no));
                            break;
                        case 40402:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_verify_out));
                            break;
                        case 40403:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_verify_error));
                            break;
                        case 40500:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_upload_pic_fail));
                            break;
                        case 40900:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_edit_rule_no));
                            break;
                        case 40901:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_edit_fail));
                            break;
                        case 41000:
                            mOnHttpRequestCallBack.onHttpRequestFail(code, App.Inst.getString(R.string.code_delete_fail));
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
                mOnHttpRequestCallBack.onHttpRequestFail(NetConfig.ERROR_PARSE, NetConfig.ERROR_PARSE_MSG);
            }
        }
    }

    public interface OnHttpRequestCallBack {
        void onHttpRequestSuccess(Object result);

        void onHttpRequestFail(int code, String errMsg);
    }

}
