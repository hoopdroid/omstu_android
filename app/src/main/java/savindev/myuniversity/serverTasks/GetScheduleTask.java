package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import savindev.myuniversity.R;
import savindev.myuniversity.schedule.GroupsModel;

public class GetScheduleTask extends AsyncTask<GroupsModel, Void, Integer> {
	private Context context;
	private final static int TIMEOUT_MILLISEC = 5000;
	private Date lastModifiedDate;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	public GetScheduleTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
		super();
		this.context = context;
		this.mSwipeRefreshLayout = mSwipeRefreshLayout;
	}


	@Override
	protected Integer doInBackground(GroupsModel... params) {
//		String dateStr = params[0].getLastRefresh();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
//		Date date = null;
//		try {
//			date = (Date)formatter.parse(dateStr);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//			dateStr = "20000101000000";
//			try {
//				date = (Date)formatter.parse(dateStr);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}
//		StringBuilder builder = new StringBuilder('?');
////		for (String p : params) {
////			if (p.charAt(0) == 'g') {
////				builder.append("idGroup=" + p.replace("g", "") + "&");
////			}
////			if (p.charAt(0) == 't') {
////				builder.append("idTeacher=" + p.replace("t", "") + "&");
////			}
////		}
//		String parameters = builder.toString().replaceFirst("&$", "");
//		String uri = context.getResources().getString(R.string.uri) + "getSchedule" + parameters;
		String uri = context.getResources().getString(R.string.uri) + "getSchedule?idGroup=197";
		URL url;
		try {
			url = new URL(uri);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();


			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			String reply = buffer.toString();
			urlConnection.disconnect();
			if (reply.isEmpty()) { //Если ответ пустой
				return -1;
			}
			replyParse(reply);
		} catch (Exception e) {
			return -1;
		}

		return 1;
	}

	@Override
	protected void onPostExecute(Integer data) {
		if (mSwipeRefreshLayout != null) { //Если вызывалось из фрагмента расписания
			mSwipeRefreshLayout.setRefreshing(false); //Завершить показывать прогресс
			if (data > 0) { //Имеется новое содержимое, обновить данные
				context.sendBroadcast(new Intent("FINISH_UPDATE")); //Отправить запрос на обновление
			}
		}
		if (data == -1)
			Toast.makeText(context, "Не удалось получить расписание" + '\n'
					+ "Проверьте соединение с сервером", Toast.LENGTH_LONG).show();
	}


	private boolean replyParse(String reply) throws JSONException {
		Log.d("11", reply);
		//здесь разбор json и раскладка в sqlite
		Initialization init = null;
		JSONObject obj = null;
		obj = new JSONObject(reply);
		switch (obj.get("STATE").toString()) {//определение типа полученного результата
			case "MESSAGE": //Получен адекватный результат
				JSONObject content = obj.getJSONObject("CONTENT");
				String lastResresh = obj.getString("LAST_REFRESH"); //дата обновления, в таблицу дат
				ArrayList<Schedule> sched = Schedule.fromJson(content.getJSONArray("SCHEDULES"));
				ArrayList<ScheduleDates> scheddates = ScheduleDates.fromJson(content.getJSONArray("SCHEDULE_DATES"));
//              parsetoSqlite(init);
				break;
			case "ERROR":   //Неопознанная ошибка
				Log.i("myuniversity", "Ошибка ERROR от сервера, запрос GetScheduleTask, текст:"
						+ obj.get("CONTENT"));
				break;
			case "WARNING": //Определенная сервером ошибка
				Log.i("myuniversity", "Ошибка WARNING от сервера, запрос GetScheduleTask, текст:"
						+ obj.get("CONTENT"));
				break;
		}
		return false;
	}

	private void parsetoSqlite(Schedule init){
		//TODO Parse Schedule to SQlite


	}
}
