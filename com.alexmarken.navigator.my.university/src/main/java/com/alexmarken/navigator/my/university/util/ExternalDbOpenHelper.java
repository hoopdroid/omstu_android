package com.alexmarken.navigator.my.university.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.alexmarken.navigator.my.university.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

	public static String DB_PATH;
	public static String DB_NAME;
	public SQLiteDatabase database;
	public final Context context;

	public AuditorHelper auditorHelper;
	public GrapsHelper grapsHelper;
	public CampusHelper campusHelper;
	public MapsHelper mapsHelper;
	public TeachersHelper teachersHelper;

	public SQLiteDatabase getDb() {
		return database;
	}

	public ExternalDbOpenHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;

		String packageName = context.getPackageName();

		DB_PATH = String.format("//data//data//%s//database//", packageName);
		DB_NAME = databaseName;

		openDataBase();

		auditorHelper = new AuditorHelper();
		grapsHelper = new GrapsHelper();
		campusHelper = new CampusHelper();
		mapsHelper = new MapsHelper();
		teachersHelper = new TeachersHelper();
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();

		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {

			}
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			Log.e(this.getClass().toString(), "Ошибка проверки БД");
		}

		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {
		InputStream externalDbStream = context.getAssets().open(DB_NAME);

		String outFileName = DB_PATH + DB_NAME;

		new File(outFileName).getParentFile().mkdirs();

		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}

		localDbStream.close();
		externalDbStream.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;

		if (database == null) {
			createDataBase();
			database = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}
	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	public void deleteDatabase(String p)
	{
		File file = new File(p);

		if(file.exists())
		{
			file.delete();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public class MapsHelper {

		public static final String TABLE_NAME = "MapsTbl";
		public static final String COL_ID = "ID";
		public static final String COL_NAME = "Name";
		public static final String COL_IMAGENAME = "ImageName";
		public static final String COL_CORPUS = "Corpus";
		public static final String COL_STAGE = "Stage";;
		public static final String COL_POINTSCOUNT = "PointsCount";


		public void create(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE MapsTbl (\n" +
					COL_ID			+ "INTEGER PRIMARY KEY,\n" +
					COL_NAME		+ "TEXT,\n" +
					COL_IMAGENAME	+ "TEXT,\n" +
					COL_CORPUS		+ "INTEGER,\n" +
					COL_STAGE		+ "INTEGER,\n" +
					COL_POINTSCOUNT	+ "INTEGER\n" +
					");");

		}

		public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}


	public class CampusHelper {

		public static final String TABLE_NAME = "campus";
		public static final String COL_ID = "id";
		public static final String COL_CORPUSNAME = "CorpusName";
		public static final String COL_STAGECOUNT = "StageCount";
		public static final String COL_GROUND = "isGround";
		public static final String COL_X = "X";
		public static final String COL_Y = "Y";


		public void create(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE campus (\n" +
					COL_ID			+ "INTEGER PRIMARY KEY,\n" +
					COL_CORPUSNAME	+ "TEXT,\n" +
					COL_STAGECOUNT	+ "INT,\n" +
					COL_GROUND		+ "INT,\n" +
					COL_X			+ "INTEGER,\n" +
					COL_Y			+ "INTEGER\n" +
					");");

		}

		public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public class AuditorHelper {

		public static final String TABLE_NAME = "omgups";
		public static final String COL_ID = "id";
		public static final String COL_CORPUS = "Corpus";
		public static final String COL_STAGE = "Stage";
		public static final String COL_AUDITOR = "Auditor";
		public static final String COL_CHARSTER = "Charster";
		public static final String COL_TYPE = "TypeAuditor";
		public static final String COL_NAME = "NameAuditor";
		public static final String COL_URL = "url";
		public static final String COL_X = "X";
		public static final String COL_Y = "Y";


		public void create(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE omgups (\n" +
					COL_ID			+ "INTEGER PRIMARY KEY,\n" +
					COL_CORPUS		+ "INTEGER,\n" +
					COL_STAGE		+ "INTEGER,\n" +
					COL_AUDITOR		+ "INT,\n" +
					COL_CHARSTER	+ "TEXT	DEFAULT ( 0 ),\n" +
					COL_TYPE		+ "TEXT,\n" +
					COL_NAME		+ "TEXT,\n" +
					COL_URL			+ "TEXT,\n" +
					COL_X			+ "INTEGER,\n" +
					COL_Y			+ "INTEGER,\n" +
					");");

		}

		public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}


	public class TeachersHelper {

		public static final String TABLE_NAME = "teachers";
		public static final String COL_ID = "id";
		public static final String COL_NAME = "Name";
		public static final String COL_POS = "Position";
		public static final String COL_DEG = "Degree";
		public static final String COL_RANK = "AcademicRank";
		public static final String COL_DESC = "Desciplines";
		public static final String COL_DIR = "Directing";


		public void create(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE teachers (\n" +
					COL_ID		+ "INTEGER PRIMARY KEY,\n" +
					COL_NAME	+ "TEXT,\n" +
					COL_POS		+ "TEXT,\n" +
					COL_DEG		+ "TEXT,\n" +
					COL_RANK	+ "TEXT,\n" +
					COL_DESC	+ "TEXT,\n" +
					COL_DIR		+ "TEXT,\n" +
					");");

		}

		public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public class GrapsHelper {
		public static final String TABLE_NAME = "points";
		public static final String COL_ID = "ID";
		public static final String COL_MAP = "Map";
		public static final String COL_AUDITORY = "Auditor";
		public static final String COL_GRAPS = "Graps";
		public static final String COL_NODE = "Node";
		public static final String COL_X = "X";
		public static final String COL_Y = "Y";


		public void create(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE points (\n" +
					COL_ID			+ "INTEGER PRIMARY KEY,\n" +
					COL_MAP			+ "TEXT,\n" +
					COL_AUDITORY	+ "TEXT,\n" +
					COL_GRAPS		+ "TEXT,\n" +
					COL_NODE		+ "TEXT,\n" +
					COL_X			+ "INTEGER,\n" +
					COL_Y			+ "INTEGER,\n" +
					");");

		}

		public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}