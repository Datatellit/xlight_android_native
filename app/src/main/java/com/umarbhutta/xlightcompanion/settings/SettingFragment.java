package com.umarbhutta.xlightcompanion.settings;

import android.app.AlertDialog;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.deviceList.DeviceListActivity;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderOptions;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.room.FamilyCodeActivity;
import com.umarbhutta.xlightcompanion.room.FamilyMemberActivity;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;
import com.umarbhutta.xlightcompanion.views.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    private List<String> settingStr = new ArrayList<String>();

    SettingListAdapter settingListAdapter;
    ListView settingListView;
    private CircleImageView userIcon;
    private TextView tv_userName, textView;
    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private ImageView iv_code;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(getString(R.string.title_setting));
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setVisibility(View.INVISIBLE);
        tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        textView = (TextView) view.findViewById(R.id.textView);
        userIcon = (CircleImageView) view.findViewById(R.id.userIcon);
        iv_code = (ImageView) view.findViewById(R.id.iv_code);

        settingListView = (ListView) view.findViewById(R.id.settingListView);
        settingListAdapter = new SettingListAdapter(getActivity(), settingStr);
        settingListView.setAdapter(settingListAdapter);

        iv_code.setOnClickListener(this);

        settingStr.add(getString(R.string.persion_inco));
        settingStr.add(getString(R.string.modify_pwd));
//        settingStr.add(getString(R.string.txt_family));
        settingStr.add(getString(R.string.txt_myController));
        settingStr.add(getString(R.string.shake));
        settingStr.add(getString(R.string.logout));

        settingListAdapter.notifyDataSetChanged();
        settingListAdapter.setmOnItemClickListener(new SettingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0://个人信息
                        if (!UserUtils.isLogin(getActivity())) {
                            onFabPressed(LoginActivity.class);
                            getActivity().finish();
                            return;
                        }
                        onFabPressed(UserMsgModifyActivity.class);
                        break;
                    case 1://修改密码
                        if (!UserUtils.isLogin(getActivity())) {
                            onFabPressed(LoginActivity.class);
                            getActivity().finish();
                            return;
                        }
                        onFabPressed(ModifyPasswordActivity.class);
                        break;
//                    case 2://家庭成员
//                        if (!UserUtils.isLogin(getActivity())) {
//                            onFabPressed(LoginActivity.class);
//                            getActivity().finish();
//                            return;
//                        }
//                        // 家庭成员页面
//                        onFabPressed(FamilyMemberActivity.class);
//                        break;
                    case 2://摇一摇
                        if (!UserUtils.isLogin(getActivity())) {
                            onFabPressed(LoginActivity.class);
                            getActivity().finish();
                            return;
                        }
                        onFabPressed(DeviceListActivity.class);
                        break;
                    case 3://摇一摇
                        if (!UserUtils.isLogin(getActivity())) {
                            onFabPressed(LoginActivity.class);
                            getActivity().finish();
                            return;
                        }
                        onFabPressed(ShakeActivity.class);
                        break;
//                    case 3://用户邀请
//                        onFabPressed(UserInvitationActivity.class);
//                        break;
//                    case 4://快速绑定
//                        onFabPressed(FastBindingActivity.class);
//                        break;
                    case 4://退出登录
                        showDeleteDialog();
                        break;
                }
            }
        });
        initUserInfo();
        return view;
    }

    public void initUserInfo() {
        if (UserUtils.isLogin(getActivity())) {
            LoginResult userInfo = UserUtils.getUserInfo(getActivity());
            String nickName = UserUtils.getUserInfo(getActivity()).getNickname();
            if (null == nickName) {
                nickName = "";
            }
            tv_userName.setText("Welcome, " + nickName);
            textView.setText(UserUtils.getUserInfo(getActivity()).getEmail());
            ImageLoader.getInstance().displayImage(userInfo.getImage(), userIcon, ImageLoaderOptions.getImageLoaderOptions());
        }
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
        initUserInfo();
    }

    private void onFabPressed(Class activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void logout() {
        if (GlanceMainFragment.devicenodes != null || GlanceMainFragment.devicenodes.size() > 0) {
            GlanceMainFragment.devicenodes.clear();
        }
        SlidingMenuMainActivity.mShakeInfo = null;
        SharedPreferencesUtils.putObject(getActivity(), SharedPreferencesUtils.KEY_DEVICE_LIST, null);
        UserUtils.saveUserInfo(getActivity(), null);
        if (UserUtils.isExpires(getContext(), SharedPreferencesUtils.KEY__ANONYMOUSINFO)) {
            //开始匿名登录
            AnonymousParams anonymousParams = UserUtils.getAnonymous(getContext());
            Gson gson = new Gson();
            String paramStr = gson.toJson(anonymousParams);
            HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ANONYMOUS_LOGIN, paramStr, AnonymousResult.class, new HttpUtils.OnHttpRequestCallBack() {
                @Override
                public void onHttpRequestSuccess(Object result) {
                    //登录成功，设置到本次的UserUtils对象中
                    AnonymousResult ar = (AnonymousResult) result;
                    UserUtils.saveAnonymousInfo(getContext(), ar);
                }

                @Override
                public void onHttpRequestFail(int code, String errMsg) {

                }
            });
        }
        //登出后回到首页
        Intent intent = new Intent(getActivity(), SlidingMenuMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.iv_code:
                // 跳转到别的页面
                onFabPressed(FamilyCodeActivity.class);
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

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(getString(R.string.set_logout));
        builder.setMessage(getString(R.string.set_logout));
        builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!UserUtils.isLogin(getActivity())) {
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                logout();
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
