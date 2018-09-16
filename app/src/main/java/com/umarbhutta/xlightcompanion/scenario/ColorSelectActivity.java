package com.umarbhutta.xlightcompanion.scenario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.event.ColorEvent;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/3/4.
 */

public class ColorSelectActivity extends BaseActivity {
    private ColorPickerView colorPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
//        colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
//        // colorPickerView.setFlagView(new CustomFlag(this, R.layout.layout_flag));
//        colorPickerView.setPreferenceName("MyColorPickerView");
//        initViews();
    }

    /**
     * init views
     */
    private void initViews() {
        //final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
//        SVBar svBar = (SVBar) findViewById(R.id.svbar);
//        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
//        final SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
//        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);
//        int color = getIntent().getIntExtra("color", 0);
//        if (color != -1) {
////            colorPickerView.setSavedColor(color);
////            colorPickerView.getSelectedPoint();
//        }
//        Log.e("XLight", "select color :" + color);
//        // picker.setColor(color);
//        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                colorPickerView.saveData();
//                ColorSelectActivity.this.finish();
//            }
//        });
//        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 确定，选择的颜色
//                colorPickerView.saveData();
//                ColorSelectActivity.this.finish();
//            }
//        });
//
////        colorPickerView.setBackgroundColor(color);
//
//        colorPickerView.setACTON_UP(true);

//        picker.addSVBar(svBar);
//        picker.addOpacityBar(opacityBar);
//        picker.addSaturationBar(saturationBar);
//        picker.addValueBar(valueBar);

        //To get the color
        // picker.getColor();

        //To set the old selected color u can do it like this
        // picker.setOldCenterColor(picker.getColor());
        // adds listener to the colorpicker which is implemented
        //in the activity


//        colorPickerView.setColorListener(new ColorListener() {
//            @Override
//            public void onColorSelected(ColorEnvelope colorEnvelope) {
//                Log.e("XLight", "颜色选择 = " + colorEnvelope.getColor());
//                // EventBus.getDefault().post(new ColorEvent(colorEnvelope.getColor()));
//            }
//        });

//        picker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
//            @Override
//            public void onColorSelected(int color) {
//                Log.e("XLight", "颜色选择 = " + color);
//                EventBus.getDefault().post(new ColorEvent(color));
//            }
//        });

        //to turn of showing the old color
        // picker.setShowOldCenterColor(false);
//
//        saturationBar.setOnSaturationChangedListener(new SaturationBar.OnSaturationChangedListener() {
//            @Override
//            public void onSaturationChanged(int saturation) {
//                // 颜色状态变化
//                // Log.e("XLight", "颜色饱和度变化 = " + saturation);
//            }
//        });

    }
}
