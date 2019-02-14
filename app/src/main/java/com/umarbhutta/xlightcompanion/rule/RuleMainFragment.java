package com.umarbhutta.xlightcompanion.rule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceGetRules;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleItem;
import com.umarbhutta.xlightcompanion.okHttp.model.Rules;
import com.umarbhutta.xlightcompanion.okHttp.model.UserScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeleteRule;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestRuleSwitch;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestRulesInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;
import com.umarbhutta.xlightcompanion.scenario.AddSceneActivity;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RuleMainFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private GridView gvRule;
    private RuleListAdapter ruleListAdapter;
    private TextView txtNone;
    private List<RuleInfo> lstRules = new ArrayList<>();
    private ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_rules, container, false);
        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.rule);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.VISIBLE);
        btn_add.setOnClickListener(this);
        gvRule = (GridView) view.findViewById(R.id.gvRules);
        txtNone = (TextView) view.findViewById(R.id.txtNone);
        mProgress = ProgressDialogUtils.showProgressDialog(getContext(), getString(R.string.loading));
        return view;
    }

    private void initList() {
        ruleListAdapter = new RuleListAdapter(getContext(), lstRules);
        ruleListAdapter.notifyDataSetChanged();
        ruleListAdapter.setOnClickCallBack(new RuleListAdapter.OnClickCallBack() {
            @Override
            public void onClickCallBack(int position) {
                // 点击进入
                Intent intent = new Intent(getContext(), AddRuleActivity.class);
                intent.putExtra("from", "list");
                intent.putExtra("rule", lstRules.get(position));
                getContext().startActivity(intent);
            }

            @Override
            public void onCheckedCallBack(int position, boolean checked) {
                // 禁用启用
                changeRuleStatus(position, checked);
            }
        });

        ruleListAdapter.setOnLongClickCallBack(new RuleListAdapter.OnLongClickCallBack() {
            @Override
            public void onLongClickCallBack(int position) {
                // 删除
                showDeleteSceneDialog(position);
            }
        });
        gvRule.setAdapter(ruleListAdapter);
    }

    private void getRules() {
        mProgress.show();
        RequestRulesInfo.getInstance().getRules(getContext(), new RequestRulesInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(DeviceGetRules mDeviceInfoResult) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress.isShowing()) {
                            mProgress.dismiss();
                        }
                        lstRules.clear();
                        lstRules.addAll(mDeviceInfoResult.rows);
                        if (lstRules.size() == 0) {
                            txtNone.setVisibility(View.VISIBLE);
                        } else {
                            txtNone.setVisibility(View.GONE);
                        }
                        initList();
                    }
                });
            }

            @Override
            public void onRequestFirstPageInfoFail(int code, String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress.isShowing()) {
                            mProgress.dismiss();
                            ToastUtil.showToast(getContext(), R.string.net_error);
                        }
                    }
                });
            }
        });
    }

    private void showDeleteSceneDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_rule_tap);
        builder.setMessage(R.string.delete_this_rule);
        builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRule(position);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void deleteRule(int position) {
        ToastUtil.showLoading(getContext());
        // 删除规则
        RequestDeleteRule.getInstance().deleteRule(getContext(), lstRules.get(position).id, new CommentRequestCallback() {
            @Override
            public void onCommentRequestCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        ToastUtil.showToast(getActivity(), getString(R.string.delete_success));
                        lstRules.remove(position);
                        ruleListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCommentRequestCallbackFail(int code, String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        if (code == 40000) {
                            ToastUtil.showToast(getContext(), R.string.rule_device_offline);
                        } else {
                            ToastUtil.showToast(getContext(), R.string.delete_fail);
                        }
                    }
                });
            }
        });
    }

    private void changeRuleStatus(int position, boolean state) {
        ToastUtil.showLoading(getContext());
        // 禁用、启用规则
        RequestRuleSwitch.getInstance().switchRule(getContext(), lstRules.get(position).id, state ? 1 : 0, new CommentRequestCallback() {
            @Override
            public void onCommentRequestCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                    }
                });
            }

            @Override
            public void onCommentRequestCallbackFail(int code, String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.dismissLoading();
                        if (code == 40000) {
                            ToastUtil.showToast(getContext(), R.string.rule_device_offline);
                        } else {
                            ToastUtil.showToast(getContext(), R.string.modify_fail);
                        }
                        ruleListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            ImmersionBar.with(this).titleBar(R.id.ll_main_top).statusBarDarkFont(true).init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getRules();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.btn_add:
                // 跳转到添加规则页面
                onFabPressed(AddRuleActivity.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
