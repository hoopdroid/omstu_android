package savindev.myuniversity.notes;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;

/**
 * Фрагмент для отображения заметок
 */


public class NotesFragment extends Fragment {

    RelativeLayout emptyNotesLayout;

    public NotesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final SwipeMenuListView listView = (SwipeMenuListView)view.findViewById(R.id.listView);

        MainActivity.fab.show();

        Bundle arg = getActivity().getIntent().getExtras();
        int scheduleId=0;
        String date = "";
        if(arg!=null){
            scheduleId = arg.getInt("scheduleId",0);
            date = arg.getString("date","");}
        emptyNotesLayout = (RelativeLayout)view.findViewById(R.id.no_notes);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());

                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.primary)));
                openItem.setWidth(200);
                openItem.setIcon(R.drawable.ic_done_white_24dp);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.md_red_500)));
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

        final DBHelper dbHelper = new DBHelper(getActivity());
        ArrayList<NoteModel> noteModelArrayList;

        //Выбор типа показа заметок
        if(scheduleId==0||date.equals("")){
        noteModelArrayList =  dbHelper.getNotesHelper().getAllNotes();}// Все заметки из БД
        else
        noteModelArrayList = dbHelper.getNotesHelper().getPairNotes(scheduleId,date);// Заметки к паре

        NoteListAdapter adapter=new NoteListAdapter(getActivity(),
               noteModelArrayList);

        listView.setMenuCreator(creator);
        listView.setAdapter(adapter);
        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"EDIT MOVE DELETE", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //TODO add DONE move
                        break;
                    case 1:
                        //TODO ADD DELETING NOTE (PROBLEM : DELETE BY ? IF BY ID HOW TO SET ID IN NOTE COS ITS AUTOINCREMENT
//                        закомментировала, тбо изменила формат модели. не уверена, что на этом уровне должен быть доступ к id заметок, надо подумать (вопросы синхронизации)
//                        dbHelper.getNotesHelper().deleteNote(dbHelper.getNotesHelper().getNotes().get(position).getNoteId());
                      //  refresh();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        if(noteModelArrayList.size() == 0)
            emptyNotesLayout.setVisibility(View.VISIBLE);
        else
            emptyNotesLayout.setVisibility(View.GONE);

       //TODO Некорректно работает скроллинг на sdk <20 Следует перейти на RecyclerView
        ViewCompat.setNestedScrollingEnabled(listView, true);


        return view;
    }

    public void refresh(){
        FragmentManager manager  = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment newFragment = this;
        this.onDestroy();
        ft.remove(this);
        ft.replace(R.id.content_main,newFragment);
        //container is the ViewGroup of current fragment
        ft.addToBackStack(null);
        ft.commit();
    }



}


