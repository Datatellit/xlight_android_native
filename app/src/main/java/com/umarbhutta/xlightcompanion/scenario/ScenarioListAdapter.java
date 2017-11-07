package com.umarbhutta.xlightcompanion.scenario;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;

import java.util.List;

/**
 */
public class ScenarioListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Rows> sceneList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public ScenarioListAdapter(Context context, List<Rows> sceneList) {
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
        final Rows infos = sceneList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.scenario_list_item, parent, false);
            //通过上面layout得到的view来获取里面的具体控件
            holder.scenarioDelete = (ImageView) convertView.findViewById(R.id.scenarioDelete);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);

            holder.scenarioText = (TextView) convertView.findViewById(R.id.text_scenario);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed(v, infos);
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
        holder.scenarioDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabPressed(v, infos);
            }
        });
        holder.scenarioText.setText(infos.scenarioname);

        return convertView;
    }

    class ViewHolder {
        private ImageView scenarioDelete;

        private TextView scenarioText;

        private LinearLayout ll_item;

    }

    private void onFabPressed(View view, Rows infos) {
        Intent intent = new Intent(mContext, AddScenarioNewActivity.class);
        intent.putExtra("from", "list");
        intent.putExtra("infos", infos);
        mContext.startActivity(intent);
    }

    private OnLongClickCallBack mOnLongClickCallBack;

    public void setOnLongClickCallBack(OnLongClickCallBack mOnLongClickCallBack) {
        this.mOnLongClickCallBack = mOnLongClickCallBack;
    }

    public interface OnLongClickCallBack {
        void onLongClickCallBack(int position);
    }
}
