package savindev.myuniversity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import savindev.myuniversity.performance.RatingModel;

/**
 * Класс для работы с SQlite базой данных.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "university.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";
    private static DBHelper instance = null;
    private UniversityInfoHelper universityInfoHelper;
    private TeachersHelper teachersHelper;
    private SemestersHelper semestersHelper;
    private PairsHelper pairsHelper;
    private GroupsHelper groupsHelper;
    private FacultiesHelper facultiesHelper;
    private DepartmentsHelper departmentsHelper;
    private UsedSchedulesHelper usedSchedulesHelper;
    private SchedulesHelper schedulesHelper;
    private CampusesHelper campusesHelper;
    private ClassroomsHelper classroomsHelper;
    private BuildingsHelper buildingsHelper;
    private NotesHelper notesHelper;
    private RatingHelper ratingHelper;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        universityInfoHelper = new UniversityInfoHelper(this);
        teachersHelper = new TeachersHelper();
        semestersHelper = new SemestersHelper(this);
        pairsHelper = new PairsHelper(this);
        groupsHelper = new GroupsHelper();
        facultiesHelper = new FacultiesHelper();
        departmentsHelper = new DepartmentsHelper();
        usedSchedulesHelper = new UsedSchedulesHelper();
        schedulesHelper = new SchedulesHelper();
        campusesHelper = new CampusesHelper();
        classroomsHelper = new ClassroomsHelper();
        buildingsHelper = new BuildingsHelper();
        notesHelper = new NotesHelper(this);
        ratingHelper = new RatingHelper(this);


    }

    public static DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context.getApplicationContext());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        universityInfoHelper.create(db);
        teachersHelper.create(db);
        semestersHelper.create(db);
        pairsHelper.create(db);
        groupsHelper.create(db);
        facultiesHelper.create(db);
        departmentsHelper.create(db);
        usedSchedulesHelper.create(db);
        schedulesHelper.create(db);
        campusesHelper.create(db);
        classroomsHelper.create(db);
        buildingsHelper.create(db);
        notesHelper.create(db);
        ratingHelper.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public TeachersHelper getTeachersHelper() {
        return teachersHelper;
    }

    public FacultiesHelper getFacultiesHelper() {
        return facultiesHelper;
    }

    public UniversityInfoHelper getUniversityInfoHelper() {
        return universityInfoHelper;
    }

    public SemestersHelper getSemestersHelper() {
        return semestersHelper;
    }

    public GroupsHelper getGroupsHelper() {
        return groupsHelper;
    }

    public PairsHelper getPairsHelper() {
        return pairsHelper;
    }

    public DepartmentsHelper getDepartmentsHelper() {
        return departmentsHelper;
    }

    public UsedSchedulesHelper getUsedSchedulesHelper() {
        return usedSchedulesHelper;
    }

    public SchedulesHelper getSchedulesHelper() {
        return schedulesHelper;
    }

    public CampusesHelper getCampusesHelper() {
        return campusesHelper;
    }

    public ClassroomsHelper getClassroomsHelper() {
        return classroomsHelper;
    }

    public BuildingsHelper getBuildingsHelper() {
        return buildingsHelper;
    }

    public NotesHelper getNotesHelper() {
        return notesHelper;
    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}