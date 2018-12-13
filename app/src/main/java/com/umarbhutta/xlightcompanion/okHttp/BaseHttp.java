package com.umarbhutta.xlightcompanion.okHttp;

import android.text.TextUtils;
import android.util.Log;

import com.umarbhutta.xlightcompanion.Tools.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by guangbinw on 2017/3/12.
 */
public abstract class BaseHttp {
    /**
     * 错误结果
     *
     * @param errResult
     */
    public abstract void okOnError(String errResult);

    /**
     * 服务器返回数据
     *
     * @param result
     */
    public abstract void okOnResponse(String result);


    /**
     * get方式获取数据
     *
     * @param url
     */
    protected void getData(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间;
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okOnError(call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Logger.i("result = " + result);
                okOnResponse(result);
            }
        });
    }

    /**
     * post方式请求数据
     *
     * @param url
     * @param jsonParam
     */
    protected void postData(String url, String jsonParam) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        if (TextUtils.isEmpty(jsonParam)) {
            jsonParam = "";
        }
        RequestBody body = RequestBody.create(JSON, jsonParam);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("XLight", call.toString(), e);
                okOnError(call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Logger.i("result = " + result);
                okOnResponse(result);
            }
        });
    }

    protected void postData(String url, String jsonParam, String connectType) {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (TextUtils.isEmpty(jsonParam)) {
            jsonParam = "";
        }
        RequestBody body = new FormBody.Builder()
                .add(jsonParam, "")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", connectType + "; charset=utf-8")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("XLight", call.toString(), e);
                okOnError(call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Logger.i("result = " + result);
                okOnResponse(result);
            }
        });
    }

    /**
     * put请求
     *
     * @param url
     * @param jsonParam
     */
    protected void putData(String url, String jsonParam) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        if (TextUtils.isEmpty(jsonParam)) {
            jsonParam = "";
        }
        RequestBody body = RequestBody.create(JSON, jsonParam);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okOnError(call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Logger.i("result = " + result);
                okOnResponse(result);
            }
        });
    }

    /**
     * Delete请求
     *
     * @param url
     * @param jsonParam
     */
    protected void deleteData(String url, String jsonParam) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        if (TextUtils.isEmpty(jsonParam)) {
            jsonParam = "";
        }
        RequestBody body = RequestBody.create(JSON, "");

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okOnError(call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Logger.i("result = " + result);
                okOnResponse(result);
            }
        });
    }
}