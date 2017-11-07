/** 
 *文件工具类
 */
package com.umarbhutta.xlightcompanion.settings.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * @author a1
 *
 */
public class FileUtils {
	
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/formats/";
	public static String SDPATH1 = Environment.getExternalStorageDirectory()
        + "/myimages/";
	
	public static void saveBitmap(Bitmap bm, String picName) {
		Log.e("", "保存图片");
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".JPEG"); 
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.e("", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static boolean deletePhotoAtPathAndName(String path) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			if (files == null || files.length == 0) {
				return false;
			}
			for (int i = 0; i < files.length; i++) {
				if (path.endsWith(files[i].getName())) {
					files[i].delete();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 检测sd卡是否可用
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static void deleteDir(String path) {
		File dir = new File(path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(path); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}
	

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 获取图片缓存路径
	 *
	 * @return
	 */
	public static String getImgCachePath(Context context) {
		String externalPath;
		if (isSDSizeAvailable()) {
			externalPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			externalPath = context.getFilesDir().getAbsolutePath();
		}
		return externalPath;
	}

	/**
	 * 获取SD卡剩余空间，true-可用空间大于100MB，false-空间不足或无SD卡
	 */
	public static boolean isSDSizeAvailable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 取得SD卡文件路径
			String path = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(path);
			// 获取单个数据块的大小(Byte)
			long l = statFs.getBlockSize();
			if ((statFs.getAvailableBlocks() * l / 1024 / 1024) < 50) { // 可用空间小于100MB
				return false;
			} else {
				return true;
			}
		}
		return false;
	}










	// 图片类型
	public static final int PIC_TYPE = 1;
	// 文档类型
	public static final int DOC_TYPE = 2;
	// 应用类型
	public static final int APP_TYPE = 4;
	// 视频类型
	public static final int VEDIO_TYPE = 8;
	// 音频类型
	public static final int AUDIO_TYPE = 16;
	// 加密类型
	public static final int QDK_TYPE = 32;
	// 其他类型
	public static final int OTHER_TYPE = 64;

	public static String dir =
			Environment.getExternalStorageDirectory().getAbsolutePath(); //默认路径
//	 public static String dir = "/mnt/external_sd"; // 测试1sd卡路径，外卡
//	 public static String dir = "/mnt/sd-ext"; //测试2sd卡路径

	public static final int ALL_TYPE = PIC_TYPE | DOC_TYPE | APP_TYPE
			| VEDIO_TYPE | AUDIO_TYPE | QDK_TYPE | OTHER_TYPE;

	public static final String PLAYERRECORDS_DIR = dir
			+ "/Android/data/vtv/";

	public static final String VEDIO_DIR = dir
			+ "/Android/data/vtv/video/";
	public static final String AUDIO_DIR = dir
			+ "/Android/data/vtv/audio/";

	public static final String TEACHINGMATERIALS_GYMNASTICS_DIR = dir
			+ "/Android/data/vtv/TeachingMaterials/Gymnastics/";
	public static final String TEACHINGMATERIALS_ATHLETICS_DIR = dir
			+ "/Android/data/vtv/TeachingMaterials/Athletics/";

	public static final String NEWS_GYMNASTICS_DIR = dir
			+ "/Android/data/vtv/News/Gymnastics/";
	public static final String NEWS_ATHLETICS_DIR = dir
			+ "/Android/data/vtv/News/Athletics/";

	public static void createDir() {
		File root = new File(dir + "/Android");
		if (!root.exists()) {
			root.mkdir();
		}
		File data = new File(dir + "/Android/data");
		if (!data.exists()) {
			data.mkdir();
		}

		File pa = new File(dir + "/Android/data/vtv");
		if (!pa.exists()) {
			pa.mkdir();
		}

		File pa1 = new File(dir + "/Android/data/vtv/TeachingMaterials");
		if (!pa1.exists()) {
			pa1.mkdir();
		}

		File pa2 = new File(dir + "/Android/data/vtv/News");
		if (!pa2.exists()) {
			pa2.mkdir();
		}
		File pa3 = new File(PLAYERRECORDS_DIR);
		if (!pa3.exists()) {
			pa3.mkdir();
		}

		File path1 = new File(VEDIO_DIR);
		if (!path1.exists()) {
			path1.mkdir();
		}
		File path2 = new File(AUDIO_DIR);
		if (!path2.exists()) {
			path2.mkdir();
		}

		File path3 = new File(TEACHINGMATERIALS_GYMNASTICS_DIR);
		if (!path3.exists()) {
			path3.mkdir();
		}
		File path4 = new File(TEACHINGMATERIALS_ATHLETICS_DIR);
		if (!path4.exists()) {
			path4.mkdir();
		}
		File path5 = new File(NEWS_GYMNASTICS_DIR);
		if (!path5.exists()) {
			path5.mkdir();
		}
		File path6 = new File(NEWS_ATHLETICS_DIR);
		if (!path6.exists()) {
			path6.mkdir();
		}
	}

	public static String getMediaFileName(String root) {
		File dir = new File(root);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String name = DateUtil.getDate(new Date());
		if (TextUtils.isEmpty(name)) {
			name = "unknow";
		}

		return dir.getAbsolutePath() + "/" + name+".mp4";
	}

	public static ArrayList<File> getFiles(File rootFile) {
		return getFiles(rootFile, ALL_TYPE);
	}

	public static class DefaultComparator implements Comparator<File> {

		@Override
		public int compare(File file1, File file2) {
			if (file1.isDirectory() && file2.isFile()) {
				return -1;
			} else if (file1.isDirectory() && file2.isDirectory()) {
				return file1.getName().compareToIgnoreCase(file2.getName());
			} else if (file1.isFile() && file2.isFile()) {
				return file1.getName().compareToIgnoreCase(file2.getName());
			} else {
				return 1;
			}
		}
	}

	public static boolean isAllType(int type) {
		if (type <= 0) {
			return true;
		}
		return ALL_TYPE == (type & ALL_TYPE);
	}

	public static boolean typeIsExist(int type, File file) {
		if (file.exists()) {
			if (file.isFile()
					&& (type == (type & getFileType(file.getName())))) {
				return true;
			}
			// File[] files = file.listFiles();
			// if (files!= null && files.length != 0) {
			// for (File f : files) {
			// if(typeIsExist(type, f)){
			// return true;
			// }else{
			// continue;
			// }
			// }
			// }
		}
		return false;
	}

	public static int getFileType(String filename) {
		int type = 0;
		int isDefaultQdkFile = 0;
		// Log.e(TAG, "filename "+filename);
		if (filename.endsWith(".qdk")) {
			filename = filename.substring(0, filename.lastIndexOf(".qdk"));
			isDefaultQdkFile = QDK_TYPE;
		}
		if (filename.endsWith(".enc")) {
			filename = filename.substring(0, filename.lastIndexOf(".enc"));
			isDefaultQdkFile = QDK_TYPE;
		}
		if (filename.endsWith(".apk")) {
			type = APP_TYPE | isDefaultQdkFile;
		} else if (filename.endsWith(".jpg") || filename.endsWith(".png")
				|| filename.endsWith(".bmp") || filename.endsWith(".gif")) {
			type = PIC_TYPE | isDefaultQdkFile;
		} else if (filename.endsWith(".mp3") || filename.endsWith(".wma")
				|| filename.endsWith(".m4a")) {
			type = AUDIO_TYPE | isDefaultQdkFile;
		} else if (filename.endsWith(".rmbv") || filename.endsWith(".avi")
				|| filename.endsWith(".mp4")) {
			type = VEDIO_TYPE | isDefaultQdkFile;
		} else if (filename.endsWith(".pdf") || filename.endsWith(".txt")
				// || filename.endsWith(".ini") || filename.endsWith(".rtf")
				|| filename.endsWith(".xls") || filename.endsWith(".xlsx")
				// || filename.endsWith(".xlsm") || filename.endsWith(".xltx")
				// || filename.endsWith(".xltm") || filename.endsWith(".xlsb")
				// || filename.endsWith(".xlam")
				|| filename.endsWith(".docx")
				// || filename.endsWith(".docm") || filename.endsWith(".dotx")
				// || filename.endsWith(".dotm")
				|| filename.endsWith(".pptx")
				// || filename.endsWith(".pptm") || filename.endsWith(".lrc")
				// || filename.endsWith(".ppsx") || filename.endsWith(".potx")
				// || filename.endsWith(".potm") || filename.endsWith(".ppam")
				|| filename.endsWith(".ppt")
				// || filename.endsWith(".log")
				|| filename.endsWith(".doc")
			// || filename.endsWith(".vcf")
				) {
			type = DOC_TYPE | isDefaultQdkFile;
		} else {
			type = OTHER_TYPE | isDefaultQdkFile;
		}

		return type;
	}

	public static ArrayList<File> getFiles(File rootFile, int type) {
		DefaultComparator defaultComparator = new DefaultComparator();
		ArrayList<File> files = new ArrayList<File>();
		if (rootFile == null) {
			return files;
		}
		File[] childFiles = rootFile.listFiles();
		if (childFiles == null) {
			return files;
		}
		// paixu
		if (isAllType(type)) {
			for (File file : childFiles) {
				if (!file.getName().startsWith(".")) {
					files.add(file);
					// }
				}
			}
		} else {
			for (File file : childFiles) {
				if (typeIsExist(type, file)) {
					if (!file.getName().startsWith(".")) {
						files.add(file);
					}
				}
			}
		}
		Collections.sort(files, defaultComparator);
		return files;
	}

	// 复制文件
	public static void copyFile(String sourceFile, String targetFile)
			throws Exception {

		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	public static Intent createGetContentIntent() {
		// Implicitly allow the user to select a particular kind of data
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// The MIME data type filter
		intent.setType("*/*");
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		return intent;
	}

	public static String getPath(final Context context, final Uri uri) {


		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// LocalStorageProvider
			if (isLocalStorageDocument(uri)) {
				// The path is the id
				return DocumentsContract.getDocumentId(uri);
			}
			// ExternalStorageProvider
			else if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
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
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static boolean isLocalStorageDocument(Uri uri) {
		return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

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
//				if (DEBUG)
					DatabaseUtils.dumpCursor(cursor);

				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}