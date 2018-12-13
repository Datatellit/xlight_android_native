package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ScenariosResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;

import java.util.List;

/**
 * Created by guangbinw on 2017/3/14.
 * 设备详细信息接口
 */
public class RequestScenarios implements HttpUtils.OnHttpRequestCallBack {
    private OnRequestScenarioInfoCallback onRequestScenarioInfoCallback;

    public static RequestScenarios getInstance() {
        return new RequestScenarios();
    }

    /**
     * 获取信息
     *
     * @param context
     */
    public void getScenarioInfo(Context context, OnRequestScenarioInfoCallback onRequestScenarioInfoCallback) {
        this.onRequestScenarioInfoCallback = onRequestScenarioInfoCallback;
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_SCENARIOS + UserUtils.getAccessToken(context), SceneListResult.class, this);
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        SceneListResult info = (SceneListResult) result;
        if (null != onRequestScenarioInfoCallback) {
            onRequestScenarioInfoCallback.onRequestScenarioInfoSuccess(info.data.rows);
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != onRequestScenarioInfoCallback) {
            onRequestScenarioInfoCallback.onRequestScenarioInfoFail(code, errMsg);
        }
    }

    public interface OnRequestScenarioInfoCallback {
        void onRequestScenarioInfoSuccess(List<ScenariosResult> mScenarioResult);

        void onRequestScenarioInfoFail(int code, String errMsg);
    }


}
