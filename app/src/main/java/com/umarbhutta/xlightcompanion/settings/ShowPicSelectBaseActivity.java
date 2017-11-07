package com.umarbhutta.xlightcompanion.settings;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.Tools.ToastUtil;
import com.umarbhutta.xlightcompanion.settings.utils.DisplayUtils;
import com.umarbhutta.xlightcompanion.settings.utils.FileUtils;
import com.umarbhutta.xlightcompanion.settings.utils.PictureUtils;
import com.umarbhutta.xlightcompanion.views.SelectPicPopupWindow;

import java.io.File;
import java.io.IOException;

/**
 * create by：guangbinw on 16/4/1 14:38
 * email：guangbingwang@126.com
 * 如果需要在相册中选择头像或者拍摄照片作为图片的可以继承此基类
 */
public abstract class ShowPicSelectBaseActivity extends BaseActivity {
    private PopupWindow mPopSelector;
    private static final int PHOTO_REQUEST_GALLERY = 2002;
    private static final int CROP_PICTURE = 2003;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 2004;
    private final String photo_name = "lightphotoc.jpg";
    private final String scop_name = "lightscrop.jpg";

    /**
     * 原图
     */
    private File tempFile = null;
    /**
     * 切过的图
     */
    private File cropFile;
    private int screenWidth;
    private int picHeight;

    /**
     * 是不是长方形
     */
    private boolean isRect = false;

    public void setIsRect(boolean isRect) {
        this.isRect = isRect;
    }

    /**
     * 图片选择的结果
     *
     * @param picPath
     */
    public abstract void selectPicResult(String picPath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempFile = new File(FileUtils.getImgCachePath(this), photo_name);

        screenWidth = DisplayUtils.getScreenWidth(this);
        picHeight = screenWidth * 9 / 16;

    }

    /**
     * 拍照图片名称
     *
     * @return
     */
    private File getTempFile() {
        if (null == tempFile)
            tempFile = new File(FileUtils.getImgCachePath(this), photo_name);
        return tempFile;
    }

    /**
     * 剪切后的图片名字
     *
     * @return
     */
    private File getScropFile() {
        if (null == cropFile)
            cropFile = new File(FileUtils.getImgCachePath(this), scop_name);
        return cropFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//        Logger.i("图库中选中 了图片 resultCode= " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PHOTO_REQUEST_GALLERY://图库中选择的

//                    Logger.i("图库中选中 了图片 = " + (null == intent));

                    if (intent != null) {
                        // 选择图片模式，会回复一个URI
                        startPhotoZoom(intent.getData());
                    } else {
                        ToastUtil.showToast(this, getString(R.string.photo_set_fail));
                    }
                    break;
                case PHOTO_REQUEST_TAKEPHOTO: //拍照
                    getTempFile();
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;

                case CROP_PICTURE:
                    try {

                        if (null == cropFile) {
                            getScropFile();
                        }

                        if (null == tempFile) {
                            getTempFile();
                        }

                        //图片裁剪完进行压缩，并替换掉原有的tempFile图片
                        PictureUtils utils = new PictureUtils();
                        String filePath = utils.compressPic(this, cropFile.getAbsolutePath(), tempFile.getAbsolutePath());
                        //getScropFile();
                        selectPicResult(filePath);
                    } catch (Exception e) {
                        selectPicResult(null);
                    }


                    break;
            }
        }
    }


    private void startPhotoZoom(Uri uri) {

//        Logger.i("需要裁剪的图片地址 = " + uri.toString());

        if (null == uri) {
            ToastUtil.showToast(this, getString(R.string.pic_select_fail));
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = getPath(this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }

        intent.putExtra("crop", "true");

        if (isRect) {
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 16);
            intent.putExtra("aspectY", 9);
            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", screenWidth);
            intent.putExtra("outputY", picHeight);

        } else {
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        getScropFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(cropFile));
        startActivityForResult(intent, CROP_PICTURE);
    }

    /**
     * 获得rootview
     *
     * @param context
     * @return
     */
    public View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content))
                .getChildAt(0);
    }

    /**
     * 显示打开头像选择的pop
     */
    public void showPictureSelector() {

        getTempFile();

        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.i("图片文件创建失败...");
            }
        }

        mPopSelector = new SelectPicPopupWindow(this, itemsOnClick);
        mPopSelector.showAtLocation(getRootView(this), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopSelector.dismiss();
            if (TextUtils.isEmpty(tempFile.getAbsolutePath())) {
                ToastUtil.showToast(ShowPicSelectBaseActivity.this, getString(R.string.your_sdcard_error));
                return;
            }
            int id = v.getId();
            if (id == R.id.btn_take_photo) { //拍照
                // 删除上一次截图的临时文件
//                boolean enabled = FileUtils.deletePhotoAtPathAndName(tempFile.getAbsolutePath());
//                Logger.i("删除上一次的临时图片");
//                if (!enabled) {
//                    ToastUtil.showToast(SelectPhotoBaseActivity.this, getString(R.string.sdcard_error));
//                    return;
//                }
                // 保存本次截图临时文件名字

//                Logger.i("文件目录 = " + tempFile.getAbsolutePath());

                Uri imageUri = Uri.fromFile(tempFile);
                Intent cameraintent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageUri);
                ShowPicSelectBaseActivity.this.startActivityForResult(cameraintent,
                        PHOTO_REQUEST_TAKEPHOTO);

            } else if (id == R.id.btn_pick_photo) { //图库中选择
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                ShowPicSelectBaseActivity.this.startActivityForResult(openAlbumIntent, PHOTO_REQUEST_GALLERY);
            }
        }
    };


//以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {  // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
