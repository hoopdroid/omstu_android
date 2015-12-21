package savindev.myuniversity.performance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.List;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBRequest;

public abstract class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.DownloadHolder> {
    private ThinDownloadManager mDownloadManager;
    private boolean isPerformance;
    private List<DownloadModel> models;
    private Context context;

    public DownloadListAdapter(ThinDownloadManager mDownloadManager, Context context, List<DownloadModel> models, boolean isPerformance) {
        this.mDownloadManager = mDownloadManager;
        this.context = context;
        this.models = models;
        this.isPerformance = isPerformance;
    }

    public static class DownloadHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ProgressBar progressBar;
        private CardView cv;

        DownloadHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public DownloadHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_view, viewGroup, false);
        return new DownloadHolder(v);
    }

    @Override
    public void onBindViewHolder(final DownloadHolder downloadHolder, final int i) {
        downloadHolder.name.setText(DBRequest.getUserGroup(models.get(i).getIdGroup(), context));
        downloadHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkConnected(context)) {
                    downloadHolder.progressBar.setVisibility(View.VISIBLE);
                    String url;
                    if (isPerformance)
                        url = context.getResources().getString(R.string.uri) + "getRaitingFile?idProgressRaitingFile=" + models.get(downloadHolder.getAdapterPosition()).getID_PROGRESS_RAITNG_FILE();
                    else
                        url = context.getResources().getString(R.string.uri) + "getDistanceSchedule?group_id=" + models.get(downloadHolder.getAdapterPosition()).getID_PROGRESS_RAITNG_FILE();
                    Uri downloadUri = Uri.parse(url);
                    final String destFolder;
                    if (isPerformance)
                        destFolder = "/" + models.get(downloadHolder.getAdapterPosition()).getName() + ".xlsx";
                    else
                        destFolder = "/" + models.get(downloadHolder.getAdapterPosition()).getName() + ".pdf";
                    final Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + destFolder);
                    DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                            .setDestinationURI(destinationUri)
                            .setPriority(DownloadRequest.Priority.LOW)
                            .setDownloadListener(new DownloadStatusListener() {
                                @Override
                                public void onDownloadComplete(int id) {
                                    downloadHolder.progressBar.setProgress(100);
                                    downloadHolder.progressBar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                                    + destFolder);
                                            PackageManager packageManager = context.getPackageManager();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri uri = Uri.fromFile(file);
                                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                                            intent.setDataAndType(uri, mime.getMimeTypeFromExtension("xlsx"));
                                            context.getApplicationContext().startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                    Toast.makeText(context, "Не удалось", Toast.LENGTH_SHORT).show();
                                    Log.i("myuniversity", "Ошибка от сервера, запрос downloadListAdapter, текст:"
                                            + errorMessage);
                                    downloadHolder.progressBar.setProgress(0);
                                }

                                @Override
                                public void onProgress(int id, long totalBytes, long arg3, int progress) {
                                    downloadHolder.progressBar.setProgress(progress);
                                }
                            });
                    mDownloadManager.add(downloadRequest);
                } else {
                    Toast.makeText(context, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
