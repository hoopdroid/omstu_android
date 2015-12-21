package savindev.myuniversity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.notes.NoteModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {
RelativeLayout emptyNotesLayout;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final SwipeMenuListView listView = (SwipeMenuListView)view.findViewById(R.id.listView);
        MainActivity.fab.show();
        emptyNotesLayout = (RelativeLayout)view.findViewById(R.id.no_notes);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.primary)));
                // set item width
                openItem.setWidth(200);
                // set item title
                openItem.setIcon(R.drawable.ic_done_white_24dp);


                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.md_red_500)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        final DBHelper dbHelper = new DBHelper(getActivity());

        ArrayList<NoteModel> noteModelArrayList =  dbHelper.getNotesHelper().getNotes();
        CustomNoteAdapter adapter=new CustomNoteAdapter(getActivity(),
               noteModelArrayList);



        listView.setMenuCreator(creator);
        listView.setAdapter(adapter);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //TODO add DONE move
                        break;
                    case 1:
                        dbHelper.getNotesHelper().deleteNote(dbHelper.getNotesHelper().getNotes().get(position).getNoteId());
                        refresh();
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


