package com.umarbhutta.xlightcompanion.scenario;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Scene;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.model.UserScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestDeleteScene;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestSceneListInfo;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ScenarioMainFragment extends Fragment implements View.OnClickListener {

    public static String SCENARIO_NAME = "SCENARIO_NAME";
    public static String SCENARIO_INFO = "SCENARIO_INFO";

    public static ArrayList<String> name = new ArrayList<>(Arrays.asList("Preset 1", "Preset 2", "Turn off"));
    public static ArrayList<String> info = new ArrayList<>(Arrays.asList("A bright, party room preset", "A relaxed atmosphere with yellow tones", "Turn the chandelier rings off"));

    ScenarioListAdapter sceneSysListAdapter;
    ScenarioListAdapter sceneCusListAdapter;
    GridView gvSystem;
    GridView gvCustom;
    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private RelativeLayout rl_no;
    private LinearLayout llSystem, llCustom;

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
//        btn_add.setBackgroundResource(R.drawable.control_add);
        btn_add.setOnClickListener(this);

        //setup recycler view
        gvCustom = (GridView) view.findViewById(R.id.gvCustom);
        gvSystem = (GridView) view.findViewById(R.id.gvSystem);
        //create list adapter
        rl_no = (RelativeLayout) view.findViewById(R.id.rl_no);
        llCustom = (LinearLayout) view.findViewById(R.id.llCustom);
        llSystem = (LinearLayout) view.findViewById(R.id.llSystem);
        return view;
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
        getSceneList();
    }

    /**
     * 删除场景
     */
    private void showDeleteSceneDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        UserScene us = mCusSceneList.get(position).userscenes.get(0);
        if (us.userId == 0)
            return;
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
        SceneResult mSceneInfo = mCusSceneList.get(position);
        RequestDeleteScene.getInstance().deleteScene(getActivity(), mSceneInfo.id, new CommentRequestCallback() {
            @Override
            public void onCommentRequestCallbackSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(getActivity(), R.string.delete_success);
                        mCusSceneList.remove(position);
                        sceneCusListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCommentRequestCallbackFail(int code, final String errMsg) {
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
                if (sceneCusListAdapter != null)
                    sceneCusListAdapter.notifyDataSetChanged();
                if (sceneSysListAdapter != null)
                    sceneSysListAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public List<SceneResult> mSceneList = new ArrayList<SceneResult>();
    private ProgressDialog mDialog;

    private void getSceneList() {
        mDialog = ProgressDialogUtils.showProgressDialog(getActivity(), getString(R.string.loading));
        if (mDialog != null) {
            mDialog.show();
        }
        RequestSceneListInfo.getInstance().getSceneListInfo(getActivity(), new RequestSceneListInfo.OnRequestSceneInfoCallback() {
            @Override
            public void onRequestFirstPageInfoSuccess(final List<SceneResult> sceneInfoResult) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            if (null != sceneInfoResult && sceneInfoResult.size() > 0) {
                                mSceneList.clear();
                                mSceneList.addAll(sceneInfoResult);
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

    public List<SceneResult> mSysSceneList = new ArrayList<SceneResult>();
    public List<SceneResult> mCusSceneList = new ArrayList<SceneResult>();

    private void initList() {
        if (mSceneList.size() > 0) {
            mSysSceneList.clear();
            mCusSceneList.clear();
            //进行场景分类
            for (SceneResult sr : mSceneList) {
                UserScene us = sr.userscenes.get(0);
                if (us.userId == 0) {
                    mSysSceneList.add(sr);
                } else {
                    mCusSceneList.add(sr);
                }
            }
            rl_no.setVisibility(View.GONE);
            if (mSysSceneList.size() > 0) {
                llSystem.setVisibility(View.VISIBLE);
            }
            if (mCusSceneList.size() > 0) {
                llCustom.setVisibility(View.VISIBLE);
            }
            sceneCusListAdapter = new ScenarioListAdapter(getContext(), mCusSceneList);
            sceneCusListAdapter.setOnLongClickCallBack(new ScenarioListAdapter.OnLongClickCallBack() {
                @Override
                public void onLongClickCallBack(int position) {
                    showDeleteSceneDialog(position);
                }
            });
            sceneCusListAdapter.setOnClickCallBack(new ScenarioListAdapter.OnClickCallBack() {
                @Override
                public void onClickCallBack(int position) {
                    resolveScene(mCusSceneList.get(position));
                }
            });
            gvCustom.setAdapter(sceneCusListAdapter);
            sceneSysListAdapter = new ScenarioListAdapter(getContext(), mSysSceneList);
            sceneSysListAdapter.setOnClickCallBack(new ScenarioListAdapter.OnClickCallBack() {
                @Override
                public void onClickCallBack(int position) {
                    resolveScene(mSysSceneList.get(position));
                }
            });
            gvSystem.setAdapter(sceneSysListAdapter);
        } else {
            llCustom.setVisibility(View.GONE);
            llSystem.setVisibility(View.GONE);
            rl_no.setVisibility(View.VISIBLE);
        }
    }

    public void resolveScene(final SceneResult scene) {
        try {
            HttpUtils.getInstance().putRequestInfo(String.format(NetConfig.URL_CHANGE_SCENE, scene.id, UserUtils.getAccessToken(getContext())), "", null, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_success), scene.name));
                        }
                    });
                }

                @Override
                public void onHttpRequestFail(int code, String errMsg) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_failed), scene.name));
                        }
                    });
                }
            });
        } catch (Exception e) {
            ToastUtil.showToast(getContext(), String.format(getString(R.string.scene_change_failed), scene.name));
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
                onFabPressed(AddSceneActivity.class);
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
