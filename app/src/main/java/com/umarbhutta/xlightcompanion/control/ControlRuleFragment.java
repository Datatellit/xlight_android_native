package com.umarbhutta.xlightcompanion.control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.control.adapter.DeviceRulesListAdapter;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceGetRules;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeleteRuleDevice;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeviceRulesInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestRuleSwitchDevice;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则列表页面
 */
public class ControlRuleFragment extends Fragment implements View.OnClickListener, DeviceRulesListAdapter.OnItemActionListener {
    private static final String TAG = ControlRuleFragment.class.getSimpleName();
    private ProgressDialog mDialog;

    @Override
    public void onDestroyView() {
        rulesRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    private ListView rulesRecyclerView;
    private LinearLayout no_relativeLayout, relativeLayout;
    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private List<RuleInfo> mRuleInfoList = new ArrayList<RuleInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control_rule, container, false);
        rulesRecyclerView = (ListView) view.findViewById(R.id.rulesRecyclerView);

        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.rule);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.VISIBLE);
//        btn_add.setBackground(getContext().getDrawable(R.drawable.control_add));
        btn_add.setBackgroundResource(R.drawable.control_add);
        btn_add.setOnClickListener(this);
        no_relativeLayout = (LinearLayout) view.findViewById(R.id.no_relativeLayout);
        relativeLayout = (LinearLayout) view.findViewById(R.id.relativeLayout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getControlRuleList();
    }

    private void getControlRuleList() {

        if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
            ToastUtil.showToast(getActivity(), R.string.net_error);
            return;
        }

        mDialog = ProgressDialogUtils.showProgressDialog(getActivity(), getString(R.string.loading));
        if (mDialog != null) {
            mDialog.show();
        }
        RequestDeviceRulesInfo.getInstance().getRules(getActivity(), new RequestDeviceRulesInfo.OnRequestFirstPageInfoCallback() {

            @Override
            public void onRequestFirstPageInfoSuccess(final DeviceGetRules deviceInfoResult) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            mRuleInfoList.clear();
                            if (null != deviceInfoResult && null != deviceInfoResult.rows) {
                                Logger.e(TAG, "deviceInfoResult=" + deviceInfoResult.toString());
                                mRuleInfoList.addAll(deviceInfoResult.rows);
                                initList();
                            } else {
                                ToastUtil.showToast(getActivity(), getString(R.string.data_is_null));
                            }

                        }
                    });
                } catch (NullPointerException e) {
                    Log.e(TAG, "Exception caught: " + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, final String errMsg) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            ToastUtil.showToast(getActivity(), errMsg);
                        }
                    });
                }
            }
        });
    }

    private DeviceRulesListAdapter devicesListAdapter;

    private void initList() {
        if (mRuleInfoList.size() > 0) {
            no_relativeLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            if (null == devicesListAdapter) {
                devicesListAdapter = new DeviceRulesListAdapter(getContext(), mRuleInfoList);
                devicesListAdapter.addOnItemActionListener(this);
                rulesRecyclerView.setAdapter(devicesListAdapter);
            } else {
                devicesListAdapter.notifyDataSetChanged();
            }
        } else {
            no_relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            ra.toggle();
        }
    }

    private void onFabPressed(Class activity) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.onActivityPressed(activity);
        }
    }

    /**
     * item开关
     *
     * @param position
     * @param isChecked
     */
    @Override
    public void onSwitchAction(final int position, boolean isChecked) {
        //右侧开关控制 status:1代表启用，0代表禁用
        int isCheckedInt = 0;
        if (isChecked) {
            isCheckedInt = 1;
        } else {
            isCheckedInt = 0;
        }
        RequestRuleSwitchDevice.getInstance().switchRule(getActivity(), mRuleInfoList.get(position).id, isCheckedInt,  new CommentRequestCallback() {
            @Override
            public void onCommentRequestCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), getString(R.string.modify_success));
                    }
                });
                if (1 == mRuleInfoList.get(position).status) {
                    mRuleInfoList.get(position).status = 0;
                } else {
                    mRuleInfoList.get(position).status = 1;
                }
            }

            @Override
            public void onCommentRequestCallbackFail(int code, String errMsg) {
                ToastUtil.showToast(getActivity(), "errMsg=" + errMsg);
            }
        });
    }

    @Override
    public void onItemLongClick(int position) {
        showDeleteDialog(position);
    }

    @Override
    public void OnItemClick(int position) {
    }


    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.delete_rule_tap));
        builder.setMessage(getString(R.string.delete_this_rule));
        builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!NetworkUtils.isNetworkAvaliable(getActivity())) {
                    ToastUtil.showToast(getActivity(), R.string.net_error);
                    return;
                }

                mDialog.show();

                RequestDeleteRuleDevice.getInstance().deleteRule(getActivity(), mRuleInfoList.get(position).id + "", new CommentRequestCallback() {
                    @Override
                    public void onCommentRequestCallbackSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                                mRuleInfoList.remove(position);
                                initList();
                                ToastUtil.showToast(getActivity(), getString(R.string.delete_success));
                            }
                        });
                    }
                    @Override
                    public void onCommentRequestCallbackFail(int code, final String errMsg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                                ToastUtil.showToast(getActivity(), getString(R.string.delete_fail) + errMsg);
                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
