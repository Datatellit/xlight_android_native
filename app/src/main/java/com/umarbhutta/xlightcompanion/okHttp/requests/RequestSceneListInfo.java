package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;

import java.util.List;

/**
 * Created by guangbinw on 2017/3/14.
 * 场景列表信息
 */
public class RequestSceneListInfo implements HttpUtils.OnHttpRequestCallBack {
    private OnRequestSceneInfoCallback mOnRequestSceneInfoCallback;

    public static RequestSceneListInfo getInstance() {
        return new RequestSceneListInfo();
    }

    /**
     * 获取场景列表信息
     *
     * @param context
     */
    public void getSceneListInfo(Context context, OnRequestSceneInfoCallback mOnRequestSceneInfoCallback) {
        this.mOnRequestSceneInfoCallback = mOnRequestSceneInfoCallback;
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_SCENE_LIST + UserUtils.getAccessToken(context), SceneInfo.class, this);
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        SceneInfo info = (SceneInfo) result;
        if (null != mOnRequestSceneInfoCallback) {
            mOnRequestSceneInfoCallback.onRequestFirstPageInfoSuccess(info.data);
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnRequestSceneInfoCallback) {
            mOnRequestSceneInfoCallback.onRequestFirstPageInfoFail(code, errMsg);
        }
    }

    public interface OnRequestSceneInfoCallback {
        void onRequestFirstPageInfoSuccess(List<SceneResult> mSceneInfoResult);

        void onRequestFirstPageInfoFail(int code, String errMsg);
    }


}
