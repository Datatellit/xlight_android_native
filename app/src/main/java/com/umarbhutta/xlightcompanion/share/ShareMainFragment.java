package com.umarbhutta.xlightcompanion.share;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ActionProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.ShareResult;
import com.umarbhutta.xlightcompanion.scenario.AddSceneActivity;
import com.umarbhutta.xlightcompanion.settings.BaseFragmentActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by 75932 on 2017/11/22.
 */

public class ShareMainFragment extends Fragment implements View.OnClickListener {

    ListView lvShare;
    ListView lvGetShare;
    private List<ShareResult> lvShareData;
    private List<ShareResult> lvGetShareData;
    private ImageView iv_menu;
    private TextView textTitle;
    private TextView txtNotFound;
    private Button btn_more;
    private ShareReceiveListAdapter shareReceiveListAdapter;
    private ShareReceiveListAdapter shareGetListAdapter;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_share_list, container, false);
        lvShare = (ListView) view.findViewById(R.id.lv_share);
        lvGetShare = (ListView) view.findViewById(R.id.lv_get_share);
        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.share);
        btn_more = (Button) view.findViewById(R.id.btn_more);
        btn_more.setVisibility(View.VISIBLE);
        btn_more.setOnClickListener(this);
        txtNotFound = (TextView) view.findViewById(R.id.tvNotFound);
        progressDialog = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
        //获取数据
        initShare(0);
        return view;
    }

    public void initShare(final int send) {
        if (!NetworkUtils.isNetworkAvaliable(getContext())) {
            ToastUtil.showToast(getContext(), R.string.net_error);
            return;
        }
        // 后台分享接口
        if (!progressDialog.isShowing())
            progressDialog.show();
        // 请求数据
        HttpUtils.getInstance().getRequestInfo(String.format(NetConfig.URL_GET_SHARE, UserUtils.getAccessToken(getContext()), UserUtils.getUserInfo(getContext()).id, send), ShareResult.class, new HttpUtils.OnHttpRequestCallBack() {
            @Override
            public void onHttpRequestSuccess(Object result) {
                if (send == 0) {
                    lvShareData = ((ShareResult) result).data;
                } else {
                    lvGetShareData = ((ShareResult) result).data;
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            if (send == 0 && null != lvShareData && lvShareData.size() > 0) {
                                txtNotFound.setVisibility(View.GONE);
                                lvGetShare.setVisibility(View.GONE);
                                lvShare.setVisibility(View.VISIBLE);
                            } else if (send == 1 && lvGetShareData != null && lvGetShareData.size() > 0) {
                                txtNotFound.setVisibility(View.GONE);
                                lvShare.setVisibility(View.GONE);
                                lvGetShare.setVisibility(View.VISIBLE);
                            } else {
                                lvGetShare.setVisibility(View.GONE);
                                lvShare.setVisibility(View.GONE);
                                txtNotFound.setVisibility(View.VISIBLE);
                            }
                            renderShareList(send);
                        }
                    });
                }
            }

            @Override
            public void onHttpRequestFail(int code, String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
                Log.d("XLight", errMsg);
            }
        });
    }

    public void renderShareList(final int send) {
        if (shareReceiveListAdapter != null && send == 0) {
            shareReceiveListAdapter.notifyDataSetChanged();
            return;
        }
        if (shareGetListAdapter != null && send == 1) {
            shareGetListAdapter.notifyDataSetChanged();
            return;
        }
        if (send == 0) {
            shareReceiveListAdapter = new ShareReceiveListAdapter(getActivity(), lvShareData, 0);
            lvShare.setAdapter(shareReceiveListAdapter);
            shareReceiveListAdapter.setOnSureClickCallBack(new ShareReceiveListAdapter.OnSureClickCallBack() {
                @Override
                public void onClickCallBack(int position) {
                    updateShareState(position, 1);
                }
            });
            shareReceiveListAdapter.setOnCancelClickCallBack(new ShareReceiveListAdapter.OnCancelClickCallBack() {
                @Override
                public void onClickCallBack(int position) {
                    // 拒绝
                    updateShareState(position, 2);
                }
            });
        } else {
            shareGetListAdapter = new ShareReceiveListAdapter(getActivity(), lvGetShareData, 1);
            lvGetShare.setAdapter(shareGetListAdapter);
            lvGetShare.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (lvGetShareData.get(position).state == 1) {
                        // 提示取消共享
                        showCancelShareDialog(position);
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 弹出解绑设备确认框
     */
    private void showCancelShareDialog(final int position) {
        AlertDialog mAlertDialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.share_list_cancel))
                .setMessage(getString(R.string.share_list_cancel_message))
                .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateShareState(position, 3);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        Button btn1 = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn1.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button btn2 = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public void updateShareState(final int i, final int state) {
        try {
            // 接受
            JSONObject jb = new JSONObject();
            jb.put("state", state);
            jb.put("lan", isZh() ? "cn" : "en");
            HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_UPDATE_SHARE, state < 3 ? lvShareData.get(i).id : lvGetShareData.get(i).id, UserUtils.getAccessToken(getContext())), jb.toString(), null, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (state < 3) {
                                // 成功
                                lvShareData.get(i).state = state;
                                shareReceiveListAdapter.notifyDataSetChanged();
                            } else {
                                lvGetShareData.get(i).state = state;
                                shareGetListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void onHttpRequestFail(int code, final String errMsg) {
                    // 失败
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("XLight", errMsg);
                            ToastUtil.showToast(getContext(), R.string.net_error);
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("XLight", e.getMessage(), e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.btn_more:
                // 弹出切换按钮
                showPopup();
                break;
        }
    }

    private void showPopup() {
        PopupMenu popup = new PopupMenu(getActivity(), btn_more);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.share_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_send:
                        initShare(1);
                        break;
                    case R.id.menu_receive:
                        initShare(0);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void switchFragment() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.toggle();
        }
    }
}
