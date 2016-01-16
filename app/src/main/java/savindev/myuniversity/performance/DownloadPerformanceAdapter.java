package savindev.myuniversity.performance;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;

public class DownloadPerformanceAdapter extends BaseExpandableListAdapter {

    private ArrayList<DownloadModel> mModels;
    private Context mContext;

    public DownloadPerformanceAdapter(Context context, ArrayList<DownloadModel> models) {
        mContext = context;
        mModels = models;
    }

    @Override
    public int getGroupCount() {
        return mModels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mModels.get(groupPosition).getPoints().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mModels.get(groupPosition).getPoints();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mModels.get(groupPosition).getPoints().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(mModels.get(groupPosition).getESTIMATION_POINT_NAME());

        return convertView;

    }

    static class ViewHolder {
        public TextView textView;
        public ProgressBar pBar;
        public CardView cv;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.download_view, null);
        final ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) view.findViewById(R.id.name);
        holder.pBar = (ProgressBar) view.findViewById(R.id.progress);
        holder.cv = (CardView) view.findViewById(R.id.cv);
        if (mModels.get(groupPosition).getPoints().get(childPosition).getFileUri() == null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.isNetworkConnected(mContext)) {
                        new DownloadRaitingTask(holder.pBar, mModels.get(groupPosition).getPoints().get(childPosition), mContext, view).execute();
                    } else {
                        Toast.makeText(mContext, "Нет интернета", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pBar.setVisibility(View.INVISIBLE);
        }
        else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    intent.setDataAndType(mModels.get(groupPosition).getPoints().get(childPosition).getFileUri(), mime.getMimeTypeFromExtension("xlsx"));
                    PackageManager packageManager = mContext.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    if (!activities.isEmpty())
                        mContext.getApplicationContext().startActivity(intent);
                }
            });
            holder.pBar.setVisibility(View.VISIBLE);
            holder.pBar.setProgress(100);
        }
        view.setTag(holder);
        holder.textView.setText(mModels.get(groupPosition).getPoints().get(childPosition).getName());
        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
