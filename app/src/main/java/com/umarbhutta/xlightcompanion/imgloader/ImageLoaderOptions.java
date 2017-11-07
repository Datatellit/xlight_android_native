package com.umarbhutta.xlightcompanion.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.umarbhutta.xlightcompanion.R;

/**
 * create by：guangbinw on 16/4/14 15:22
 * email：guangbingwang@126.com
 * imageloader显示图片是设置属性
 */
public class ImageLoaderOptions {

    /**
     * 动画时间
     */
    private static final int animTime = 300;


    /**
     * 图片显示配置
     *
     * @return
     */
    public static DisplayImageOptions getImageLoaderOptions() {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = 4;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_account_circle)
                .showImageForEmptyUri(R.drawable.ic_account_circle)
                .showImageOnFail(R.drawable.ic_account_circle)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .decodingOptions(op)
                .displayer(new FadeInBitmapDisplayer(animTime)) //  设置动画淡入效果
                .build();
        return options;

    }

    /**
     * 图片显示配置
     *
     * @return
     */
    public static DisplayImageOptions getImageLoaderOptions(int defaultIconId) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = 4;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIconId)
                .showImageForEmptyUri(defaultIconId)
                .showImageOnFail(defaultIconId)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(animTime)) // 设置动画淡入效果
                .decodingOptions(op)
                .build();
        return options;

    }

    /**
     * 图片显示配置
     *
     * @return
     */
    public static DisplayImageOptions getImageLoaderOptions(int loadingImgId, int emptyUriImgId, int failImgId) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = 4;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingImgId)
                .showImageForEmptyUri(emptyUriImgId)
                .showImageOnFail(failImgId)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(animTime)) // 设置动画淡入效果
                .decodingOptions(op)
                .build();
        return options;

    }

}
