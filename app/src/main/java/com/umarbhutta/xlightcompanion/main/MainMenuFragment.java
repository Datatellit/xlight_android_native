package com.umarbhutta.xlightcompanion.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.control.ControlRuleFragment;
import com.umarbhutta.xlightcompanion.glance.GlanceMainFragment;
import com.umarbhutta.xlightcompanion.help.HelpFragment;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderOptions;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.report.ReportFragment;
import com.umarbhutta.xlightcompanion.scenario.ScenarioMainFragment;
import com.umarbhutta.xlightcompanion.settings.SettingFragment;
import com.umarbhutta.xlightcompanion.settings.UserMsgModifyActivity;
import com.umarbhutta.xlightcompanion.share.ShareMainFragment;
import com.umarbhutta.xlightcompanion.userManager.LoginActivity;
import com.umarbhutta.xlightcompanion.views.CircleImageView;

public class MainMenuFragment extends Fragment implements View.OnClickListener, App.ChangeViewListener {

    private String TAG = MainMenuFragment.class.getSimpleName();

    private TextView tv_userName, textView;
    private Button btnLogin;
    private LinearLayout llPerName;
    private TextView itemGlance, itemControl, itemScenario, itemSchedule, itemSettings, itemHelp, itemShare;
    private CircleImageView userIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.setChangeViewListener(this);
        return inflater.inflate(R.layout.fragment_main_menu, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserUtils.isLogin(getActivity())) {
            LoginResult userInfo = UserUtils.getUserInfo(getActivity());
            btnLogin.setVisibility(View.GONE);
            llPerName.setVisibility(View.VISIBLE);
            String nickName = UserUtils.getUserInfo(getActivity()).getNickname();
            if (null == nickName) {
                nickName = "";
            }
            tv_userName.setText("Welcome, " + nickName);
            textView.setText(UserUtils.getUserInfo(getActivity()).getEmail());
            ImageLoader.getInstance().displayImage(userInfo.getImage(), userIcon, ImageLoaderOptions.getImageLoaderOptions());
        } else {
            ImageLoader.getInstance().displayImage("", userIcon, ImageLoaderOptions.getImageLoaderOptions());
            btnLogin.setVisibility(View.VISIBLE);
            llPerName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChangeViewListener(int id) {
        changeMenu(id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        llPerName = (LinearLayout) getActivity().findViewById(R.id.llPerName);
        tv_userName = (TextView) getActivity().findViewById(R.id.tv_userName);
        textView = (TextView) getActivity().findViewById(R.id.textView);

        btnLogin = (Button) getActivity().findViewById(R.id.btn_login);
        userIcon = (CircleImageView) getActivity().findViewById(R.id.userIcon);
        itemGlance = (TextView) getActivity().findViewById(R.id.nav_glance);
        itemControl = (TextView) getActivity().findViewById(R.id.nav_control);
        itemScenario = (TextView) getActivity().findViewById(R.id.nav_scenario);
        itemSchedule = (TextView) getActivity().findViewById(R.id.nav_schedule);
        itemSettings = (TextView) getActivity().findViewById(R.id.nav_settings);
        itemHelp = (TextView) getActivity().findViewById(R.id.nav_help);
        itemShare = (TextView) getActivity().findViewById(R.id.nav_share);
        itemGlance.setOnClickListener(this);
        itemControl.setOnClickListener(this);
        itemScenario.setOnClickListener(this);
        itemSchedule.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemShare.setOnClickListener(this);
        itemHelp.setOnClickListener(this);
        userIcon.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        itemGlance.setBackgroundColor(getResources().getColor(R.color.bar_color));
        itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
        itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
        itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
        itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
        itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
        itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
    }

    // the meat of switching the above fragment
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.switchContent(fragment);
        }
    }

    @Override
    public void onClick(View v) {
        changeMenu(v.getId());
    }

    private void changeMenu(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.btn_login:
                showContentView();
                onFabPressed(LoginActivity.class);
                getActivity().finish();
                break;
            case R.id.userIcon:
                showContentView();
                if (!UserUtils.isLogin(getActivity())) {
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                onFabPressed(UserMsgModifyActivity.class);
                break;
            case R.id.nav_glance:
                itemGlance.setBackgroundColor(getResources().getColor(R.color.bar_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new GlanceMainFragment();//首页
                break;
            case R.id.nav_control:
                if (!UserUtils.isLogin(getActivity())) {
                    showContentView();
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                itemControl.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new ControlRuleFragment();//规则
                break;
            case R.id.nav_scenario:
                if (!UserUtils.isLogin(getActivity())) {
                    showContentView();
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                itemScenario.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new ScenarioMainFragment();//场景
                break;
            case R.id.nav_schedule:
                if (!UserUtils.isLogin(getActivity())) {
                    showContentView();
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new ReportFragment();//报表
                break;
            case R.id.nav_settings:
                if (!UserUtils.isLogin(getActivity())) {
                    showContentView();
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                itemSettings.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new SettingFragment();//设置
                break;
            case R.id.nav_help:
                itemHelp.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemShare.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new HelpFragment();//帮助
                break;
            case R.id.nav_share:
                if (!UserUtils.isLogin(getActivity())) {
                    showContentView();
                    onFabPressed(LoginActivity.class);
                    getActivity().finish();
                    return;
                }
                itemShare.setBackgroundColor(getResources().getColor(R.color.bar_color));

                itemGlance.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemControl.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemScenario.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSchedule.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemSettings.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                itemHelp.setBackgroundColor(getResources().getColor(R.color.btn_login_color));
                fragment = new ShareMainFragment();
                break;
        }
        if (fragment != null) {
            switchFragment(fragment);
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

    private void showContentView() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof SlidingMenuMainActivity) {
            SlidingMenuMainActivity ra = (SlidingMenuMainActivity) getActivity();
            ra.showContent();
        }
    }
}
