/** 
 * 本程序代码版权归北京快乐格子信息技术有限公司或本项目委托的客户方所有。
 * Copyright www.kuailegezi.com  
 * legal@kuailegezi.com
 */
package com.umarbhutta.xlightcompanion.imgloader;

import android.content.Context;

import java.io.File;


/**
 * @author wk
 *文件缓存处理
 */
public class FileCache {
	
	
	 private File cacheDir;
	    
	    public FileCache(Context context){
	    	/**
	    	 *  如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
			    没有SD卡就放在系统的缓存目录中
	    	 */
	        //Find the dir to save cached images
	        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
	        else
	            cacheDir=context.getCacheDir();
	        if(!cacheDir.exists())
	            cacheDir.mkdirs();
	    }
	    
	    public File getFile(String url){
	    	//将url的hashCode作为缓存的文件名
	        //I identify images by hashcode. Not a perfect solution, good for the demo.
	        String filename= String.valueOf(url.hashCode());
	        //Another possible solution (thanks to grantland)
	        //String filename = URLEncoder.encode(url);
	        File f = new File(cacheDir, filename);
	        return f;
	        
	    }
	    
	    public void clear(){
	        File[] files=cacheDir.listFiles();
	        if(files==null)
	            return;
	        for(File f:files)
	            f.delete();
	    }
	
	
	

}
