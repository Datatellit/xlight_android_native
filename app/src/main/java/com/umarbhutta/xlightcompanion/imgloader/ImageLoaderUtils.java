package com.umarbhutta.xlightcompanion.imgloader;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * create by：guangbinw on 16/4/14 14:58
 * email：guangbingwang@126.com
 * ImageLoader初始化类
 */
public class ImageLoaderUtils {

    /**
     * 初始化imageloader，请在application中调用此方法
     */
    public static void initImageLoader(Context context) {
//        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
//                "AppDir/cache/images");
        File cacheDir = new File(context.getCacheDir(), "images/cache/");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2) //降低线程的优先级保证主UI线程不受太大影响
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //图片名称使用md5加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(1024 * 1024 * 50) // 硬盘存储缓存大小
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024)) //内存缓存
                .memoryCacheSize(10 * 1024 * 1024)//内存缓存
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                .taskExecutor(taskExecutor)
                .taskExecutorForCachedImages(executorForCachedImages)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .writeDebugLogs()
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    private static Executor taskExecutor = Executors.newFixedThreadPool(5, new ThreadFactory() {
        int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "network-img-thread-" + count++);
        }
    });
    private static Executor executorForCachedImages = Executors.newFixedThreadPool(2,
            new ThreadFactory() {
                int count = 0;

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "cache-img-thread-" + count++);
                }
            });


}
