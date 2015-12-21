package savindev.myuniversity;


//
//public class DistanceScheduleDownloadFragment extends Fragment{
//
//    private static final int DOWNLOAD_THREAD_POOL_SIZE = 5;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        ThinDownloadManager downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE); //Для загрузки файлов
//        //Заполнение списка групп
//        ExpandableListView perfomance = (ExpandableListView) view.findViewById(R.id.perfomance);
//        DBHelper dbHelper = DBHelper.getInstance(getActivity().getBaseContext());
//        ArrayList<String> faculty = dbHelper.getFacultiesHelper().getFaculties(getActivity());
//        //Создаем лист с группами
//        ArrayList<ArrayList<GroupsModel>> models = new ArrayList<>();
//        //TODO тут вместо групп очников группы заочников
//        for (int i = 0; i < faculty.size(); i++) {
//            models.add(dbHelper.getGroupsHelper().getGroups(getActivity(), faculty.get(i)));
//        }
//
//        PerfomanceListAdapter adapter = new PerfomanceListAdapter(getActivity().getApplicationContext(), faculty, models, downloadManager);
//        perfomance.setAdapter(adapter);
//        return view;
//    }
//
//    public class PerfomanceListAdapter extends DownloadListAdapter {
//        public PerfomanceListAdapter(Context context, ArrayList<String> names,
//                                     ArrayList<ArrayList<GroupsModel>> groups, ThinDownloadManager downloadManager) {
//            super(downloadManager, context, names, groups,  false);
//        }
//    }
//}
