package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import savindev.myuniversity.R;

public class GetScheduleTask extends AsyncTask<String, Void, Integer> {
	private Context context;
	private final static int TIMEOUT_MILLISEC = 5000;
	private Date lastModifiedDate;
	private static InputStream is = null;
	private String takenJson = "";
	private SwipeRefreshLayout mSwipeRefreshLayout;

	public GetScheduleTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
		super();
		this.context = context;
		this.mSwipeRefreshLayout = mSwipeRefreshLayout;
	}


	@Override
	protected Integer doInBackground(String... params) {
		String dateStr = null;
//		dateStr = new String(sPref.getString("DATE", ""));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
		Date date = null;
//		try {
//			date = (Date)formatter.parse(dateStr); //���������� ����
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//			dateStr = "20000101000000";
//			try {
//				date = (Date)formatter.parse(dateStr);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
		StringBuilder builder = new StringBuilder('?');
		for (String p : params) {
			if (p.charAt(0) == 'g') {
				builder.append("id_user=" + p.replace("g", "") + "&");
			}
			if (p.charAt(0) == 't') {
				builder.append("id_user=" + p.replace("t", "") + "&");
			}
		}
		String parameters = builder.toString().replaceFirst("&$", "");
		String uri = context.getResources().getString(R.string.uri) + "getSchedule" + parameters;
		URL url;
		try {
			url = new URL(uri + "&last_refresh_dt=" + dateStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			String lastModified = urlConnection.getHeaderField("Last-modified");
				lastModifiedDate = (Date)formatter.parse(lastModified); //���� � �������
				if (!dateStr.equals("")) {
					if (lastModifiedDate.getTime() == (date.getTime())) {
						//���� ���� ���������, ��������� �� ���������
						return 0;
					}
				}
//				ed.putString("DATE",lastModified).apply(); //���� �� ���������, ������� ����� ����
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				} 
				takenJson = buffer.toString();
				urlConnection.disconnect();
				if (takenJson.isEmpty()) { //���� ����� ���� �������� ��� ����� ������	
					return -1;
				}
				globalParse();
			} catch (Exception e){
				return -1;
			}

			return 1;
		}

	@Override
	protected void onPostExecute(Integer data) {
		if (mSwipeRefreshLayout != null) { //Если вызывалось из фрагмента расписания
			mSwipeRefreshLayout.setRefreshing(false); //Завершить показывать прогресс
			if (data > 0) { //Имеется новое содержимое, обновить данные
				context.sendBroadcast(new Intent("FINISH_UPDATE_DAILY")); //Отправить запрос на обновление
			}
		}
		if (data == -1)
			Toast.makeText(context, "Не удалось получить расписание" + '\n'
					+ "Проверьте соединение с сервером", Toast.LENGTH_LONG).show();
	}


	private void globalParse() {
	}
}
