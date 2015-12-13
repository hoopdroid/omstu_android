package savindev.myuniversity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.util.ArrayList;
import java.util.Locale;

import savindev.myuniversity.schedule.GroupsModel;

public class DownloadListAdapter extends BaseExpandableListAdapter implements Filterable {
    /**
     * Кастомный адаптер, используется для отображения списка групп и преподавателей
     * Совмещает в себе возможность выбора списка групп и основной группы
     */
    private ArrayFilter mFilter;
    private ArrayList<ArrayList<GroupsModel>> mGroup;
    private Context mContext;
    private ArrayList<String> mNames;
    private final Object mLock = new Object();
    private ArrayList<ArrayList<GroupsModel>> mOriginalValues;
    private ArrayList<String> mOriginalNames;
    private ThinDownloadManager mDownloadManager;
    private boolean isPerfomance;

    public DownloadListAdapter(Context context, ArrayList<String> names, ArrayList<ArrayList<GroupsModel>> groups,
                               ThinDownloadManager downloadManager, boolean isPerfomance) {
        mContext = context;
        mNames = names;
        mGroup = groups;
        mDownloadManager = downloadManager;
        this.isPerfomance = isPerfomance;
    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroup.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroup.get(groupPosition).get(childPosition);
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
        textGroup.setText(mNames.get(groupPosition));
        return convertView;
    }

    static class ViewHolder {
        public TextView textView;
        public ProgressBar pbar;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.download_child_view, null);
        final ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) view.findViewById(R.id.textChild);
        holder.pbar = (ProgressBar) view.findViewById(R.id.progress);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //Скачивание с сервера рейтинга
                if (MainActivity.isNetworkConnected(mContext)) {
                    String url;
                    if (isPerfomance)
                        url = R.string.uri + "getPerfomance?group_id=" + mGroup.get(groupPosition).get(childPosition).getId();
                    else
                        url = R.string.uri + "getDistanceSchedule?group_id=" + mGroup.get(groupPosition).get(childPosition).getId();
                    Uri downloadUri = Uri.parse(url);
                    String destFolder;
                    if (isPerfomance)
                        destFolder = "/" + mGroup.get(groupPosition).get(childPosition).getName() + "-рейтинг.xlsx";
                    else
                        destFolder = "/" + mGroup.get(groupPosition).get(childPosition).getName() + "-расписание.pdf";
                    Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + destFolder);
                    DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                            .setDestinationURI(destinationUri)
                            .setPriority(DownloadRequest.Priority.LOW)
                            .setDownloadListener(new DownloadStatusListener() {
                                @Override
                                public void onDownloadComplete(int id) {
                                    holder.pbar.setProgress(100);
                                }
                                @Override
                                public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                    Toast.makeText(mContext, "Не удалось", Toast.LENGTH_SHORT).show();
                                    Log.i("myuniversity", "Ошибка от сервера, запрос downloadListAdapter, текст:"
                                            + errorMessage);
                                    holder.pbar.setProgress(0);
                                }
                                @Override
                                public void onProgress(int id, long totalBytes, long arg3, int progress) {
                                    holder.pbar.setProgress(progress);
                                }
                            });
                } else {
                    Toast.makeText(mContext, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.setTag(holder);
        holder.textView.setText(mGroup.get(groupPosition).get(childPosition).getName());
        return view;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        FilterResults resultsNames = new FilterResults();

        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mGroup);
                    mOriginalNames = new ArrayList<>(mNames);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                ArrayList<ArrayList<GroupsModel>> list;
                ArrayList<String> names;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                    names = new ArrayList<>(mOriginalNames);
                }
                results.values = list;
                results.count = list.size();
                resultsNames.values = names;
                resultsNames.count = names.size();
            } else {
                String prefixString = prefix.toString().replaceAll(" ", "").toLowerCase();
                ArrayList<ArrayList<GroupsModel>> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }


                final ArrayList<ArrayList<GroupsModel>> newValues = new ArrayList<>();
                final ArrayList<String> newNames = new ArrayList<>();

                for (int i = 0; i < values.size(); i++) {
                    final int count = values.get(i).size();
                    final ArrayList<GroupsModel> newValuesChild = new ArrayList<>();
                    for (int j = 0; j < count; j++) {
                        final GroupsModel value = values.get(i).get(j);
                        final String valueText = value.getName().replaceAll(" ", "").toLowerCase(Locale.getDefault());

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString)) {
                            newValuesChild.add(value);
                        } else {     //TODO доработать, эта часть должна определять наличие вообще в тексте, а не вначале
                            final String[] words = valueText.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (String word : words) {
                                if (word.startsWith(prefixString)) {
                                    newValuesChild.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    if (newValuesChild.size() > 0) {
                        newValues.add(newValuesChild);
                        newNames.add(mOriginalNames.get(i));
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
                resultsNames.values = newNames;
                resultsNames.count = newNames.size();

            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mGroup = (ArrayList<ArrayList<GroupsModel>>) results.values;
            mNames = (ArrayList<String>) resultsNames.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
