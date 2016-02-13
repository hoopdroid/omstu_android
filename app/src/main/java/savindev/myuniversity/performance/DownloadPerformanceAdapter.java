package savindev.myuniversity.performance;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.library.ProgressLayout;
import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.serverTasks.DownloadFileTask;

public class DownloadPerformanceAdapter extends BaseExpandableListAdapter {

    private ArrayList<RatingModel> mModels;
    private Context mContext;

    public DownloadPerformanceAdapter(Context context, ArrayList<RatingModel> models) {
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
        public ProgressLayout pl;
        public TextView textView;
        public ImageView img;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.download_view, null);
        final ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) view.findViewById(R.id.name);
        holder.img = (ImageView) view.findViewById(R.id.image);
        holder.pl = (ProgressLayout) view.findViewById(R.id.progressLayout);
        if (mModels.get(groupPosition).getPoints().get(childPosition).getFileUri() == null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.isNetworkConnected(mContext)) {
                        new DownloadFileTask(holder.pl, mModels.get(groupPosition).getPoints().get(childPosition), mContext, view).execute();
                    } else {
                        Toast.makeText(mContext, "Нет интернета", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pl.cancel();
            holder.textView.setText("скачать " + mModels.get(groupPosition).getPoints().get(childPosition).getName());
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
            holder.pl.setCurrentProgress(100);
            holder.img.setImageResource(R.drawable.galochka);
            holder.textView.setText("открыть " + mModels.get(groupPosition).getPoints().get(childPosition).getName());
        }
        view.setTag(holder);

        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
