package com.umarbhutta.xlightcompanion.share;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderOptions;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneResult;
import com.umarbhutta.xlightcompanion.okHttp.model.ShareResult;
import com.umarbhutta.xlightcompanion.okHttp.model.UserScene;
import com.umarbhutta.xlightcompanion.scenario.AddSceneActivity;
import com.umarbhutta.xlightcompanion.views.ImageViewRadius;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class ShareReceiveListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ShareResult> shareList;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用
    private int type;

    public ShareReceiveListAdapter(Context context, List<ShareResult> sceneList, int type) {
        this.shareList = sceneList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.type = type;
    }

    @Override
    public int getCount() {
        return shareList.size();
    }

    @Override
    public Object getItem(int position) {
        return shareList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ShareResult share = shareList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            if (type == 0)
                convertView = inflater.inflate(R.layout.activity_share_item, null);
            else
                convertView = inflater.inflate(R.layout.activity_share_get_item, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.txtType = (TextView) convertView.findViewById(R.id.tvType);
            holder.txtTime = (TextView) convertView.findViewById(R.id.tvTime);
            holder.txtName = (TextView) convertView.findViewById(R.id.tvName);
            holder.txtState = (TextView) convertView.findViewById(R.id.tvState);
            holder.rlDeviceName = (RelativeLayout) convertView.findViewById(R.id.rlDeviceName);
            holder.txtDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);

            if (type == 0) {
                holder.rlBtn = (RelativeLayout) convertView.findViewById(R.id.rlBtn);
                holder.btnSure = (Button) convertView.findViewById(R.id.btnSure);
                holder.btnCancel = (Button) convertView.findViewById(R.id.btnRejct);
            }
            holder.logo = (ImageViewRadius) convertView.findViewById(R.id.ivLogo);
            holder.preview = (ImageView) convertView.findViewById(R.id.ivPreview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch (share.type) {
            case 1:
                holder.txtType.setText(R.string.share_list_type_connect);
                break;
            case 2:
                holder.txtType.setText(R.string.share_list_type_scan);
                break;
            case 3:
                holder.txtType.setText(R.string.share_list_type_account);
                break;
            default:
                holder.txtType.setText(R.string.share_list_unknown);
                break;
        }
        switch (share.state) {
            case 0:
                if (new Date().getTime() > share.expirationtime.getTime()) {
                    // 已过期
                    holder.txtState.setText(R.string.share_list_state_4);
                } else {
                    holder.txtState.setText(R.string.share_list_state_0);
                }
                break;
            case 1:
                holder.txtState.setText(R.string.share_list_state_1);
                break;
            case 2:
                holder.txtState.setText(R.string.share_list_state_2);
                break;
            case 3:
                holder.txtState.setText(R.string.share_list_state_3);
                break;
            case 4:
                holder.txtState.setText(R.string.share_list_state_4);
                break;
            default:
                holder.txtState.setText(R.string.share_list_unknown);
                break;
        }
        holder.txtTime.setText(getDateFormat(share.createdAt));
        if (type == 0) {
            holder.txtName.setText(share.user.username);
            ImageLoader.getInstance().displayImage(NetConfig.SERVER_ADDRESS_DOMAIN + share.user.image, holder.logo, ImageLoaderOptions.getImageLoaderOptions());
            if (share.state > 0 || (share.state == 0 && new Date().getTime() > share.expirationtime.getTime())) {
                holder.rlBtn.setVisibility(View.GONE);
                holder.rlDeviceName.setVisibility(View.VISIBLE);
            } else {
                holder.rlBtn.setVisibility(View.VISIBLE);
                holder.rlDeviceName.setVisibility(View.GONE);
            }
            holder.btnSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSureClickCallBack != null) {
                        mOnSureClickCallBack.onClickCallBack(position);
                    }
                }
            });
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCancelClickCallBack != null) {
                        mOnCancelClickCallBack.onClickCallBack(position);
                    }
                }
            });
        } else {
            holder.rlDeviceName.setVisibility(View.VISIBLE);
            if (share.sharedevice != null) {
                holder.txtName.setText(share.sharedevice.user.username);
                ImageLoader.getInstance().displayImage(NetConfig.SERVER_ADDRESS_DOMAIN + share.sharedevice.user.image, holder.logo, ImageLoaderOptions.getImageLoaderOptions());
            } else {
                holder.txtName.setText(R.string.share_list_unknown);
            }
        }
        holder.txtDeviceName.setText(share.device.devicename);
        return convertView;
    }

    public String getDateFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    class ViewHolder {
        private ImageView preview;

        private ImageViewRadius logo;

        private TextView txtName;

        private TextView txtType;

        private TextView txtState;

        private RelativeLayout rlBtn;

        private TextView txtTime;

        private Button btnSure;

        private Button btnCancel;

        private TextView txtDeviceName;

        private RelativeLayout rlDeviceName;

    }

    private OnSureClickCallBack mOnSureClickCallBack;

    public void setOnSureClickCallBack(OnSureClickCallBack mOnSureClickCallBack) {
        this.mOnSureClickCallBack = mOnSureClickCallBack;
    }

    public interface OnSureClickCallBack {
        void onClickCallBack(int position);
    }

    private OnCancelClickCallBack mOnCancelClickCallBack;

    public void setOnCancelClickCallBack(OnCancelClickCallBack mOnCancleClickCallBack) {
        this.mOnCancelClickCallBack = mOnCancleClickCallBack;
    }

    public interface OnCancelClickCallBack {
        void onClickCallBack(int position);
    }
}
