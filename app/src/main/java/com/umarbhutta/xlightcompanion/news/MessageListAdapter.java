package com.umarbhutta.xlightcompanion.news;

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
import com.umarbhutta.xlightcompanion.okHttp.model.MessageResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.model.UserScene;
import com.umarbhutta.xlightcompanion.scenario.AddSceneActivity;

import java.util.List;


public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MessageResult> messageList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public MessageListAdapter(Context context, List<MessageResult> messageList) {
        this.messageList = messageList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MessageResult message = messageList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_news_list_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(message.message);
        holder.txtTime.setText(message.time);
        return convertView;
    }

    class ViewHolder {
        private TextView txtName;
        private TextView txtTime;

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
