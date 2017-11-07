package com.umarbhutta.xlightcompanion.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;

/**
 * Created by luomengxin on 2017/3/25.
 */

public class CircleDotView extends View {
    private int color = Color.RED;
    private int width;

    public CircleDotView(Context context) {
        super(context);
        width = DisplayUtils.dip2px(context, 30);
    }

    public CircleDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int mColor) {
        this.color = mColor;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        p.setColor(color);// 设置红色
        canvas.drawCircle(width / 2, width / 2, width / 2, p);// 小圆
        p.setAntiAlias(true);// 设置画笔的锯齿效果。
    }
}
