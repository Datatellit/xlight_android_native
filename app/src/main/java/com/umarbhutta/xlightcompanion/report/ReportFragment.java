package com.umarbhutta.xlightcompanion.report;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/5.
 */

public class ReportFragment extends Fragment implements View.OnClickListener {

    private WebView webView;
    private ImageButton ib_back, ib_refresh;
    private String url;

    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private ProgressDialog mDialog;
    private ProgressBar pbWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.baobiao);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.INVISIBLE);

        webView = (WebView) view.findViewById(R.id.helpWebview);
        pbWebView = (ProgressBar) view.findViewById(R.id.pbWebView);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);
        ib_back.setVisibility(View.INVISIBLE);
        ib_refresh = (ImageButton) view.findViewById(R.id.ib_refresh);
        ib_refresh.setOnClickListener(this);
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        getReportForm();

        return view;
    }

    private void initViews() {
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbWebView.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbWebView.setProgress(newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    if (view.canGoBack()) {
                        ib_back.setFocusable(true);
                        ib_back.setClickable(true);
                        ib_back.setBackgroundResource(R.drawable.icon_arrow_right);
                        ib_back.setVisibility(View.VISIBLE);
                    } else {
                        ib_back.setFocusable(false);
                        ib_back.setClickable(false);
                        ib_back.setBackgroundResource(R.drawable.icon_arrow_right_gray);
                        ib_back.setVisibility(View.INVISIBLE);
                    }

                } else {

                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                if (webView.canGoBack()) {
                    webView.goBack();//返回上一页面
                }
                break;
            case R.id.ib_refresh:
                webView.reload();//刷新
                break;
            case R.id.iv_menu:
                switchFragment();
                break;
        }
    }

    // the meat of switching the above fragment
    private void switchFragment() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
//            ra.toggle();
        }
    }

    /**
     * 获取报表的url
     */
    public void getReportForm() {
        // 加载中
        mDialog = ProgressDialogUtils.showProgressDialog(getActivity(), getString(R.string.loading));
        if (mDialog != null) {
            mDialog.show();
        }
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_REPORT_FORM + UserUtils.getAccessToken(getActivity()), null, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(final Object result) {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject((String) result);
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            url = dataObj.getString("url");
                            initViews();
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {

            }
        });
    }


}
