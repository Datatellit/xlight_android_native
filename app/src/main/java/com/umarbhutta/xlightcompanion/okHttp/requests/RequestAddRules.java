package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.google.gson.Gson;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.CreateRuleResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rules;

/**
 * Created by guangbinw on 2017/3/14.
 * 创建规则接口
 */
public class RequestAddRules implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnCreateRuleCallback mOnCreateRuleCallback;
    boolean isEdit = false;

    public static RequestAddRules getInstance() {
        return new RequestAddRules();
    }

    /**
     * 创建规则
     *
     * @param context
     * @param mOnCreateRuleCallback
     */
    public void createRule(Context context, Rules mRules, OnCreateRuleCallback mOnCreateRuleCallback) {
        this.isEdit = false;
        this.context = context;
        this.mOnCreateRuleCallback = mOnCreateRuleCallback;
        if (UserUtils.isLogin(context)) {
            Gson gson = new Gson();
            String params = gson.toJson(mRules);
            HttpUtils.getInstance().postRequestInfo(NetConfig.URL_CREATE_RULES + UserUtils.getAccessToken(context),
                    params, CreateRuleResult.class, this);


        }
    }

    /**
     * 编辑规则
     *
     * @param context
     * @param rules
     * @param mOnCreateRuleCallback
     */
    public void editRule(Context context, Rules rules, int id, OnCreateRuleCallback mOnCreateRuleCallback) {
        this.isEdit = true;
        this.context = context;
        this.mOnCreateRuleCallback = mOnCreateRuleCallback;
        if (UserUtils.isLogin(context)) {
            Gson gson = new Gson();
            String params = gson.toJson(rules);
            HttpUtils.getInstance().putRequestInfo(NetConfig.URL_EDIT_RULE + id + "?access_token=" +
                            UserUtils.getAccessToken(context),
                    params, null, this);


        }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        if (isEdit) {
            if (null != mOnCreateRuleCallback) {
                mOnCreateRuleCallback.mOnCreateRuleCallbackSuccess(null);
            }
        } else {
            CreateRuleResult infos = (CreateRuleResult) result;
            if (null != mOnCreateRuleCallback) {
                mOnCreateRuleCallback.mOnCreateRuleCallbackSuccess(infos.data);
            }
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnCreateRuleCallback) {
            mOnCreateRuleCallback.mOnCreateRuleCallbackFail(code, errMsg);
        }
    }

    public interface OnCreateRuleCallback {
        void mOnCreateRuleCallbackFail(int code, String errMsg);

        void mOnCreateRuleCallbackSuccess(CreateRuleResult mCreateRuleResult);
    }


}
