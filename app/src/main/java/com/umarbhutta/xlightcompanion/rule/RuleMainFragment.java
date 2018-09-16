package com.umarbhutta.xlightcompanion.rule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.news.MessageListAdapter;
import com.umarbhutta.xlightcompanion.okHttp.model.MessageResult;
import com.umarbhutta.xlightcompanion.okHttp.model.RuleItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 */
public class RuleMainFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_add;
    private GridView gvRule;
    private RuleListAdapter ruleListAdapter;

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
        ArrayList<RuleItem> lstRule = new ArrayList<>();
        // 模拟数据（10条）
        String[] testData = new String[]{
                "Sunset time",
                "At sunrise",
                "Environmental pollution",
                "Rainy day",
                "Temperature is too high"
        };
        for (int i = 0; i < 5; i++) {
            lstRule.add(new RuleItem(testData[i], i % 2 == 0 ? true : false));
        }
        ruleListAdapter = new RuleListAdapter(getContext(), lstRule);
        gvRule.setAdapter(ruleListAdapter);
        ruleListAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                switchFragment();
                break;
            case R.id.btn_add:
                // 跳转到添加场景页面
                onFabPressed(AddControlRuleActivity.class);
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
