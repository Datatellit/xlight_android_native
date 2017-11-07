package com.umarbhutta.xlightcompanion.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.AndroidBug54971Workaround;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2017/3/3.
 */

public class SplashActivity extends Activity implements OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }

    //定义ViewPager对象
    private ViewPager viewPager;

    //定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    //定义一个ArrayList来存放View
    private ArrayList<View> views;

    // 定义各个界面View对象
    private View view1, view2, view4;
    private Button startBtn;
    //底部小点的图片
    private ImageView[] points;

    //记录当前选中位置
    private int currentIndex;

    public void initView() {
        //实例化各个界面的布局对象
        LayoutInflater mLi = LayoutInflater.from(this);
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        view1 = mLi.inflate(R.layout.guide_page1, null);
        ImageView iv_one = (ImageView) view1.findViewById(R.id.iv_one);
        view2 = mLi.inflate(R.layout.guide_page2, null);
        ImageView iv_two = (ImageView) view2.findViewById(R.id.iv_two);
//      view3 = mLi.inflate(R.layout.guide_page3, null);
        view4 = mLi.inflate(R.layout.guide_page4, null);
        startBtn = (Button) view4.findViewById(R.id.startBtn);
        ImageView iv_three = (ImageView) view4.findViewById(R.id.iv_three);

        if (language.endsWith("zh")) {
            iv_one.setImageResource(R.drawable.xlight_zh_one);
            iv_two.setImageResource(R.drawable.xlight_zh_two);
            iv_three.setImageResource(R.drawable.xlight_zh_three);
            startBtn.setBackgroundResource(R.drawable.zh_go);
        } else {
            iv_one.setImageResource(R.drawable.xlight_en_one);
            iv_two.setImageResource(R.drawable.xlight_en_two);
            iv_three.setImageResource(R.drawable.xlight_en_three);
            startBtn.setBackgroundResource(R.drawable.go);
        }
//        Logger.e("language", language);
//    其中languag为语言码：
//    zh：汉语
//    en：英语
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到主页面
//                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(new Intent(SplashActivity.this, SlidingMenuMainActivity.class));
                SplashActivity.this.finish();
            }
        });

        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        //将要分页显示的View装入数组中
        views.add(view1);
        views.add(view2);
//        views.add(view3);
        views.add(view4);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    public void initData() {
        // 设置监听
        viewPager.setOnPageChangeListener(this);
        // 设置适配器数据
        viewPager.setAdapter(vpAdapter);
        vpAdapter.notifyDataSetChanged();
        //初始化底部小点
        initPoint(views.size());
    }

    /**
     * 初始化底部小点
     */
    private void initPoint(int views) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.point);

        points = new ImageView[views];

        //循环取得小点图片
        for (int i = 0; i < views; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
//            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
    }
    /**
     * 通过点击事件来切换当前的页面
     */
//    @Override
//    public void onClick(View v) {
//        int position = (Integer)v.getTag();
//        setCurView(position);
//        setCurDot(position);
//    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= 4) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > 3 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

}
