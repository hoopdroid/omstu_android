package savindev.myuniversity;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {


    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);

        ArrayList taskslist;
        ArrayList datelist;
        ArrayList tagslist;
        taskslist = new ArrayList();
        datelist = new ArrayList();
        tagslist = new ArrayList();
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        final CoordinatorLayout Clayout = (CoordinatorLayout)view.findViewById(R.id.snackbarlocation);


        for(int i = 0 ;i<10;i++){
            taskslist.add("note 1 with big awesome incredible text without any excuses");
            datelist.add(Integer.toString(i)+"декабря");
            tagslist.add("Альтман");
        }

        final SwipeMenuListView listView = (SwipeMenuListView)view.findViewById(R.id.listView);

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
        CustomListAdapter adapter=new CustomListAdapter(getActivity(), taskslist,datelist,tagslist);
        // присваиваем адаптер списку
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        listView.setActivated(false);
                        // delete
                        fab.hide();
                        Snackbar.make(Clayout, "Заметка удалена!", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getActivity(), "Удаление!", Toast.LENGTH_SHORT).show();
                                        fab.show();
                                    }
                                })
                                .show();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        listView.setMenuCreator(creator);
        listView.setAdapter(adapter);

        fab.attachToListView(listView);

        return view;
    }

}
