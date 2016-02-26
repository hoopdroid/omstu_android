package savindev.myuniversity.performance;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.mobiwise.library.ProgressLayout;
import savindev.myuniversity.R;

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
//        holder.img = (ImageView) view.findViewById(R.id.image);
//        holder.pl = (ProgressLayout) view.findViewById(R.id.progressLayout);
//        if (mModels.get(groupPosition).getPoints().get(childPosition).getFileUri() == null) {
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (MainActivity.isNetworkConnected(mContext)) {
//                        new DownloadFileTask(holder.pl, mModels.get(groupPosition).getPoints().get(childPosition), mContext, view).execute();
//                    } else {
//                        Toast.makeText(mContext, "Нет интернета", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            holder.pl.cancel();
//            holder.textView.setText("скачать " + mModels.get(groupPosition).getPoints().get(childPosition).getName());
//        }
//        else {
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    MimeTypeMap mime = MimeTypeMap.getSingleton();
//                    intent.setDataAndType(mModels.get(groupPosition).getPoints().get(childPosition).getFileUri(), mime.getMimeTypeFromExtension("xlsx"));
//                    PackageManager packageManager = mContext.getPackageManager();
//                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
//                    if (!activities.isEmpty())
//                        mContext.getApplicationContext().startActivity(intent);
//                }
//            });
//            holder.pl.setCurrentProgress(100);
//            holder.img.setImageResource(R.drawable.galochka);
//            holder.textView.setText("открыть " + mModels.get(groupPosition).getPoints().get(childPosition).getName());
//        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mContext.getResources().getString(R.string.uri) + "getRaitingFile?idProgressRaitingFile=" +
                        mModels.get(groupPosition).getPoints().get(childPosition).getID_PROGRESS_RAITNG_FILE();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
        view.setTag(holder);
        holder.textView.setText(mModels.get(groupPosition).getPoints().get(childPosition).getName());
        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
