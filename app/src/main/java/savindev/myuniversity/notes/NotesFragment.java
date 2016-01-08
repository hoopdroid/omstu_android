package savindev.myuniversity.notes;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;

/**
 * Фрагмент для отображения заметок
 */


public class NotesFragment extends Fragment {
    CoordinatorLayout coordinatorLayout;
    RelativeLayout emptyNotesLayout;
    int scheduleId;
    String date;
    boolean isPair=true;
    public NotesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final SwipeMenuListView listView = (SwipeMenuListView)view.findViewById(R.id.listView);
        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.coordinatorLayout);
        MainActivity.fab.show();

        Bundle arg = getActivity().getIntent().getExtras();
        scheduleId=0;
        date = "";
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
                openItem.setWidth(250);
                openItem.setIcon(R.drawable.ic_done_white_24dp);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.md_red_800)));                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                deleteItem.setWidth(250);
                menu.addMenuItem(deleteItem);
            }
        };

        final DBHelper dbHelper = new DBHelper(getActivity());
        ArrayList<NoteModel> noteModelArrayList;

        //Выбор типа показа заметок
        if(scheduleId==0||date.equals("")){
            noteModelArrayList =  dbHelper.getNotesHelper().getAllNotes();
            isPair=false;}// Все заметки из БД
        else {
            noteModelArrayList = dbHelper.getNotesHelper().getPairNotes(scheduleId, date);// Заметки к паре
        }
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

                        //TODO Убрать дублирование кода
                        if(isPair) {

                            int note = dbHelper.getNotesHelper().getPairNotes(scheduleId, date).get(position).getNoteId();
                            dbHelper.getNotesHelper().setNoteIsDone(note);
                            setSnackBar(false);
                            refresh(R.id.pairNotesList);
                            }
                        else {
                            dbHelper.getNotesHelper().setNoteIsDone(dbHelper.getNotesHelper().getAllNotes().get(position).getNoteId());
                            setSnackBar(false);
                            refresh(R.id.content_main);
                            }
                        break;
                    case 1:
                        if(isPair) {
                            int note = dbHelper.getNotesHelper().getPairNotes(scheduleId, date).get(position).getNoteId();
                            setSnackBar(true);
                            dbHelper.getNotesHelper().deleteNote(note);
                            refresh(R.id.pairNotesList); }
                            else {
                            setSnackBar(true);
                            dbHelper.getNotesHelper().deleteNote(dbHelper.getNotesHelper().getAllNotes().get(position).getNoteId());
                            refresh(R.id.content_main); }
                        break;
                }

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

    public void refresh(int idlayout){
        FragmentManager manager  = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment newFragment = this;
        this.onDestroy();
        ft.remove(this);
        ft.replace(idlayout,newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public Snackbar setSnackBar(boolean delete){
        String message = "Заметка удалена!";
        if(!delete)
            message = "Задача выполнена, вы молодец!";
        Snackbar snackbar = Snackbar
                .make(getView(),message, Snackbar.LENGTH_LONG);
               // .setAction("ОТМЕНИТЬ", new View.OnClickListener() {
                 //   @Override
                   // public void onClick(View view) {
                     //   Snackbar snackbar1 = Snackbar.make(getView(), "Заметка восстановлена!", Snackbar.LENGTH_SHORT);
                       // snackbar1.show();
                    //}
                //});


        snackbar.show();
        return snackbar;
    }


}


