package com.umarbhutta.xlightcompanion.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/5.
 */

public class FastBindingActivity extends BaseActivity {

    private LinearLayout llBack;
    private TextView btnSure;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_bingding);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        //hide nav bar
//        getSupportActionBar().hide();
        initViews();
    }

    private FastBindingAdapter mFastBindingAdapter;
    private ListView mListView;
    private List<String> faseBings = new ArrayList<String>();

    private void initViews() {
        mListView = (ListView) findViewById(R.id.bindingListView);
        mFastBindingAdapter = new FastBindingAdapter(FastBindingActivity.this, faseBings);
        mListView.setAdapter(mFastBindingAdapter);

        faseBings.add(getString(R.string.scan_qr));
        faseBings.add(getString(R.string.input_order));
        mFastBindingAdapter.notifyDataSetChanged();
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure = (TextView) findViewById(R.id.tvEditSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 确定提交按钮
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.bind_quick);
    }
}
