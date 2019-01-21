package com.umarbhutta.xlightcompanion.settings.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import com.umarbhutta.xlightcompanion.Tools.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * create by：guangbinw on 16/4/15 16:25
 * email：guangbingwang@126.com
 */
public class PictureUtils {
    /**
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
     * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
     * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public Bitmap getImageThumbnail(String imagePath, int width, int height) {

        File file = new File(imagePath);
        if (!file.exists()) {
            return null;
        }

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false

        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;

        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 判断图片是否需要压缩,大于100KB的图片都需要压缩
     *
     * @param path
     * @return
     * @throws Exception
     */
    private boolean isPictureNeedCompress(String path) {
        FileInputStream fis = null;
        try {
            File file = new File(path);
            long size = 0;
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
                DecimalFormat df = new DecimalFormat("#.00");
                double mbSize = Double.valueOf(df
                        .format((double) size / 1024 / 100));
                Log.i("android", "图片大小 = " + mbSize);
                if (mbSize > 1) { // 如果图片大于1M需要压缩
                    return true;
                }
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param context
     * @param orifilePath
     * @param desPath
     * @return 图片的绝对位置
     */
    public String compressPic(Context context, String orifilePath,
                              String desPath) {

        if (!isPictureNeedCompress(orifilePath)) {
            return orifilePath;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(orifilePath, options);

        options.inSampleSize = calculateInSampleSize(options);

        options.inJustDecodeBounds = false;

        Bitmap bitmap = getResizedBitmap(BitmapFactory.decodeFile(orifilePath, options), 120, 120);

        File myCaptureFile = new File(desPath);
        //Log.i("android", "图片保存位置 = " + myCaptureFile.getAbsolutePath());

        BufferedOutputStream bos;
        try {
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            // 100表示不进行压缩，70表示压缩率为30%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        BitmapFactory.Options compressOption = new BitmapFactory.Options();
        compressOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(myCaptureFile.getAbsolutePath(), compressOption);
        Logger.i("android", "压缩后的图片大小 width = " + compressOption.outWidth + ", height = " + compressOption.outHeight);

        return myCaptureFile.getAbsolutePath();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    // 计算图片的缩放值
    private int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        //int reqHeight = FabuActivity.dpTpx;
        //int reqWidth = width / height * reqHeight;
        int reqHeight = height;

        int reqWidth = width;

        Log.i("android", "原始图片的宽度 = " + options.outWidth + ", 原始图片的高度 = "
                + options.outHeight);

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 初始化存储图片的文件
     *
     * @return 初始化成功返回true，否则false
     */
    public File initImageFile() {
        // 有SD卡时才初始化文件
        File imageFile = new File(Environment.getExternalStorageDirectory(),
                "hilo" + System.currentTimeMillis() + "pic.jpg");
        if (!imageFile.exists()) {// 如果文件不存在，就创建文件
            try {
                imageFile.createNewFile();

                if (!imageFile.exists()) {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return imageFile;
    }

    /**
     * 把图片从应用内部写到sd卡上
     */
    public String reverseSavePic(Context context, String orifilePath) {
        try {
            int byteread = 0;
            File oriFile = new File(context.getFilesDir(), orifilePath);
            if (oriFile.exists()) {
                InputStream inStream = new FileInputStream(oriFile);
                FileOutputStream fs = new FileOutputStream(initImageFile());
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除图片
     *
     * @param pciUrl
     * @return
     */
    public boolean deletePicture(Context context, String pciUrl) {
        File file = new File(pciUrl);
        return file.delete();
    }
}
