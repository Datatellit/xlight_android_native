package com.umarbhutta.xlightcompanion.news;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.SharedPreferencesUtils;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.glance.LightItemAdapter;
import com.umarbhutta.xlightcompanion.main.SlidingMenuMainActivity;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Devicenodes;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;
import com.umarbhutta.xlightcompanion.okHttp.model.MessageResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.requests.RequestFirstPageInfo;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class NewsMainFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_menu;
    private TextView textTitle;
    private Button btn_more;
    private GridView gvMessage;
    private MessageListAdapter messageListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_news, container, false);
        iv_menu = (ImageView) view.findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(this);
        textTitle = (TextView) view.findViewById(R.id.tvTitle);
        textTitle.setText(R.string.message);
        btn_more = (Button) view.findViewById(R.id.btn_more);
        btn_more.setVisibility(View.VISIBLE);
        gvMessage = (GridView) view.findViewById(R.id.gvNews);
        ArrayList<MessageResult> lstMessage = new ArrayList<>();
        // 模拟数据（10条）
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String[] testData = new String[]{
                "Turn on the diamond lights",
                "Turn off the diamond lights",
                "Turn on the party time scenes",
                "Turn on the bed time scenes",
                "Turn on the dinner time scenes",
                "Turn on the diamond lights",
                "Turn off the diamond lights",
                "Turn on the bed time scenes",
                "Turn on the dinner time scenes",
                "Turn on the diamond lights"
        };
        for (int i = 0; i < 10; i++) {
            lstMessage.add(new MessageResult(testData[i], sdf.format(d)));
        }
        messageListAdapter = new MessageListAdapter(getContext(), lstMessage);
        gvMessage.setAdapter(messageListAdapter);
        messageListAdapter.notifyDataSetChanged();
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
            case R.id.btn_more:
                // 跳转到添加场景页面
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
