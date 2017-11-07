package com.umarbhutta.xlightcompanion.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.App;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.control.activity.AddControlRuleActivity;
import com.umarbhutta.xlightcompanion.control.bean.NewRuleItemInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmd;
import com.umarbhutta.xlightcompanion.okHttp.model.Actioncmdfield;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;
import com.umarbhutta.xlightcompanion.okHttp.model.SceneListResult;

import java.util.ArrayList;

/**
 */
public class ScenarioSelectListAdapter extends BaseAdapter {

    private Context mContext;
    private SceneListResult mSceneListResult;
    private Actioncmd mActioncmd;
    private LayoutInflater inflater;//这个一定要懂它的用法及作用

    public ScenarioSelectListAdapter(Context context, SceneListResult mSceneListResult, Actioncmd mActioncmd) {
        this.mSceneListResult = mSceneListResult;
        this.mContext = context;
        this.mActioncmd = mActioncmd;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mSceneListResult.rows.size();
    }

    @Override
    public Object getItem(int position) {
        return mSceneListResult.rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Rows infos = mSceneListResult.rows.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.scenario_select_list_item, parent, false);
            holder.scenarioIndex = (TextView) convertView.findViewById(R.id.scenarioIndex);
            holder.scenarioTitle = (TextView) convertView.findViewById(R.id.scenarioTitle);
            holder.scenarioDescription = (TextView) convertView.findViewById(R.id.scenarioDescription);
            holder.scenarioDelete = (ImageView) convertView.findViewById(R.id.scenarioDelete);

            holder.scenarioIcon = (ImageView) convertView.findViewById(R.id.icon_scenario);
            holder.scenarioText = (TextView) convertView.findViewById(R.id.text_scenario);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
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
        private TextView scenarioIndex, scenarioTitle, scenarioDescription;
        private ImageView scenarioDelete;

        private ImageView scenarioIcon;
        private TextView scenarioText;
        private LinearLayout ll_item;
    }

    private void onFabPressed(View view, Rows infos) {
        mActioncmd.devicenodeId = infos.id;
        mActioncmd.actioncmdType = 2;
        Actioncmdfield actioncmdfield = new Actioncmdfield();

        actioncmdfield.cmd = infos.scenarioname;
        actioncmdfield.paralist = "{" + infos.scenarioname + "}";
        if (mActioncmd.actioncmdfield == null) {
            mActioncmd.actioncmdfield = new ArrayList<Actioncmdfield>();
        }
        mActioncmd.actioncmdfield.add(actioncmdfield);

        NewRuleItemInfo mNewRuleItemInfo = new NewRuleItemInfo();
        mActioncmd.actiontype = 2;
        mNewRuleItemInfo.setmActioncmd(mActioncmd);
        AddControlRuleActivity.mNewRuleResultInfoList.add(mNewRuleItemInfo);
        ((App) mContext.getApplicationContext()).finishActivity();
    }

}
