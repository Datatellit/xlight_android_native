package com.umarbhutta.xlightcompanion.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.BitmapHelper;
import com.umarbhutta.xlightcompanion.main.CCTFragment;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.ScenariosResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guangbinw on 2017/3/23.
 * 设备列表页面
 */

public class ScenarioAdapter extends BaseAdapter {

    List<ScenariosResult> scenarioList = new ArrayList<ScenariosResult>();
    private Context context;
    private final LayoutInflater inflater;
    private int index = 0;

    public ScenarioAdapter(Context context, List<ScenariosResult> scenarioList) {
        this.scenarioList = scenarioList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scenarioList.size();
    }

    @Override
    public Object getItem(int position) {
        return scenarioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ScenariosResult scenariosResult = this.scenarioList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.scenario_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.scenarioName = (TextView) convertView.findViewById(R.id.txt_sceneName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.scenarioName.setText(scenariosResult.scenarioname);
        String url = scenariosResult.image;
        if (url.indexOf("http") > -1 && cancelPotentialLoad(url, holder.icon)) {
            AsyncLoadImageTask task = new AsyncLoadImageTask(holder.icon);
            LoadedDrawable loadedDrawable = new LoadedDrawable(task);
            holder.icon.setImageDrawable(loadedDrawable);
            task.execute(position);
        } else {
            holder.icon.setImageResource(CCTFragment.getDrawResourceID(scenariosResult.image, this.context));
        }
        return convertView;
    }

    private boolean cancelPotentialLoad(String url, ImageView imageview) {
        AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);

        if (loadImageTask != null) {
            String bitmapUrl = loadImageTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                loadImageTask.cancel(true);
            } else {
                // 相同的url已经在加载中.
                return false;
            }
        }
        return true;
    }

    private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview) {
        if (imageview != null) {
            Drawable drawable = imageview.getDrawable();
            if (drawable instanceof LoadedDrawable) {
                LoadedDrawable loadedDrawable = (LoadedDrawable) drawable;
                return loadedDrawable.getLoadImageTask();
            }
        }
        return null;
    }

    //该类功能为：记录imageview加载任务并且为imageview设置默认的drawable
    public static class LoadedDrawable extends ColorDrawable {
        private final WeakReference<AsyncLoadImageTask> loadImageTaskReference;

        public LoadedDrawable(AsyncLoadImageTask loadImageTask) {
            super(Color.TRANSPARENT);
            loadImageTaskReference =
                    new WeakReference<AsyncLoadImageTask>(loadImageTask);
        }

        public AsyncLoadImageTask getLoadImageTask() {
            return loadImageTaskReference.get();
        }
    }

    private Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        // url = url.substring(0, url.lastIndexOf("/"));//处理之前的路径区分，否则路径不对，获取不到图片

        //bitmap = BitmapFactory.decodeFile(url);
        //这里我们不用BitmapFactory.decodeFile(url)这个方法
        //用decodeFileDescriptor()方法来生成bitmap会节省内存
        //查看源码对比一下我们会发现decodeFile()方法最终是以流的方式生成bitmap
        //而decodeFileDescriptor()方法是通过Native方法decodeFileDescriptor生成bitmap的

        try {
            FileInputStream is = new FileInputStream(url);
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapHelper.getBitmap(url);
        return bitmap;
    }

    //加载图片的异步任务
    private class AsyncLoadImageTask extends AsyncTask<Integer, Void, Bitmap> {
        private String url = null;
        private final WeakReference<ImageView> imageViewReference;

        public AsyncLoadImageTask(ImageView imageview) {
            super();
            // TODO Auto-generated constructor stub
            imageViewReference = new WeakReference<ImageView>(imageview);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = null;
            this.url = scenarioList.get(params[0]).image;
            bitmap = getBitmapFromUrl(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            // TODO Auto-generated method stub
            if (isCancelled()) {
                resultBitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageview = imageViewReference.get();
                AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
                if (this == loadImageTask) {
                    imageview.setImageBitmap(resultBitmap);
                    imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
            super.onPostExecute(resultBitmap);
        }
    }

    class ViewHolder {
        public TextView scenarioName;
        public ImageView icon;
    }
}