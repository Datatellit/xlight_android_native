package com.umarbhutta.xlightcompanion.scenario;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeleteScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSceneListInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequstCallback;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ScenarioMainFragment extends Fragment implements View.OnClickListener {

    public static String SCENARIO_NAME = "SCENARIO_NAME";
    public static String SCENARIO_INFO = "SCENARIO_INFO";

    public static ArrayList<String> name = new ArrayList<>(Arrays.asList("Preset 1", "Preset 2", "Turn off"));
    public static ArrayList<String> info = new ArrayList<>(Arrays.asList("A bright, party room preset", "A relaxed atmosphere with yellow tones", "Turn the chandelier rings off"));

    ScenarioListAdapter scenarioListAdapter;
    ListView scenarioListView;
    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private RelativeLayout rl_no, rl_hava;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_scenario, container, false);

        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.scene);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.VISIBLE);
//        btn_add.setBackground(getActivity().getDrawable(R.drawable.control_add));
        btn_add.setBackgroundResource(R.drawable.control_add);
        btn_add.setOnClickListener(this);

        //setup recycler view
        scenarioListView = (ListView) view.findViewById(R.id.scenarioListView);
        //create list adapter
        rl_no = (RelativeLayout) view.findViewById(R.id.rl_no);
        rl_hava = (RelativeLayout) view.findViewById(R.id.rl_have);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSceneList();
    }

    /**
     * 删除场景
     */
    private void showDeleteSceneDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_scene_tap);
        builder.setMessage(R.string.sure_delete_this_scene);
        builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteScene(position);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void deleteScene(final int position) {
        Rows mSceneInfo = mSceneList.get(position);
        RequestDeleteScene.getInstance().deleteScene(getActivity(), mSceneInfo.id, new CommentRequstCallback() {
            @Override
            public void onCommentRequstCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), R.string.delete_success);
                        mSceneList.remove(position);
                        scenarioListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCommentRequstCallbackFail(int code, final String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), "" + errMsg);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String incomingName = data.getStringExtra(SCENARIO_NAME);
                String incomingInfo = data.getStringExtra(SCENARIO_INFO);

                name.add(incomingName);
                info.add(incomingInfo);

                scenarioListAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public List<Rows> mSceneList = new ArrayList<Rows>();
    private ProgressDialog mDialog;

    private void getSceneList() {
        mDialog = ProgressDialogUtils.showProgressDialog(getActivity(), getString(R.string.loading));
        if (mDialog != null) {
            mDialog.show();
        }
        RequestSceneListInfo.getInstance().getSceneListInfo(getActivity(), new RequestSceneListInfo.OnRequestFirstPageInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final SceneListResult deviceInfoResult) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            if (null != deviceInfoResult && null != deviceInfoResult.rows && deviceInfoResult.rows.size() > 0) {
                                mSceneList.clear();
                                mSceneList.addAll(deviceInfoResult.rows);
                            }
                            initList();
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
                            ToastUtil.showToast(getActivity(), "" + errMsg);
                        }
                    });
                }
            }
        });
    }

    private void initList() {
        if (mSceneList.size() > 0) {
            rl_hava.setVisibility(View.VISIBLE);
            rl_no.setVisibility(View.GONE);
            scenarioListAdapter = new ScenarioListAdapter(getContext(), mSceneList);
            scenarioListAdapter.setOnLongClickCallBack(new ScenarioListAdapter.OnLongClickCallBack() {
                @Override
                public void onLongClickCallBack(int position) {
                    showDeleteSceneDialog(position);
                }
            });
            scenarioListView.setAdapter(scenarioListAdapter);
        } else {
            rl_hava.setVisibility(View.GONE);
            rl_no.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.btn_add:
                // 跳转到添加场景页面
                onFabPressed(AddScenarioNewActivity.class);
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

}
