package com.umarbhutta.xlightcompanion.scenario;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.model.UserScene;

import java.util.List;

/**
 */
public class ScenarioListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SceneResult> sceneList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public ScenarioListAdapter(Context context, List<SceneResult> sceneList) {
        this.sceneList = sceneList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sceneList.size();
    }

    @Override
    public Object getItem(int position) {
        return sceneList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SceneResult scene = sceneList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_scene_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.sceneIcon = (ImageView) convertView.findViewById(R.id.imgLight);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);

            holder.sceneName = (TextView) convertView.findViewById(R.id.txtName);
            holder.imgButton = (RelativeLayout) convertView.findViewById(R.id.imgButton);
            UserScene us = scene.userscenes.get(0);
            if (us.userId == 0) {
                holder.imgButton.setVisibility(View.GONE);
            } else {
                holder.imgButton.setVisibility(View.VISIBLE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickCallBack) {
                    mOnClickCallBack.onClickCallBack(position);
                }
            }
        });
        holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mOnLongClickCallBack) {
                    mOnLongClickCallBack.onLongClickCallBack(position);
                }
                return true;
            }
        });
        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed(v, scene);
            }
        });
        holder.sceneName.setText(scene.name);
        holder.sceneIcon.setImageResource(AddSceneActivity.getDrawResourceID(scene.icon, mContext));
        return convertView;
    }

    class ViewHolder {
        private ImageView sceneIcon;

        private TextView sceneName;

        private LinearLayout ll_item;

        private RelativeLayout imgButton;

    }

    private void onFabPressed(View view, SceneResult scene) {
        //编辑场景
        Intent intent = new Intent(mContext, AddSceneActivity.class);
        intent.putExtra("from", "list");
        intent.putExtra("scene", scene);
        mContext.startActivity(intent);
    }

    private OnLongClickCallBack mOnLongClickCallBack;

    public void setOnLongClickCallBack(OnLongClickCallBack mOnLongClickCallBack) {
        this.mOnLongClickCallBack = mOnLongClickCallBack;
    }

    public interface OnLongClickCallBack {
        void onLongClickCallBack(int position);
    }

    private OnClickCallBack mOnClickCallBack;

    public void setOnClickCallBack(OnClickCallBack mOnClickCallBack) {
        this.mOnClickCallBack = mOnClickCallBack;
    }

    public interface OnClickCallBack {
        void onClickCallBack(int position);
    }
}
